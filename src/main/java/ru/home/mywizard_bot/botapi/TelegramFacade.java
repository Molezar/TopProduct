package ru.home.mywizard_bot.botapi;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.home.mywizard_bot.MyWizardTelegramBot;
import ru.home.mywizard_bot.cache.UserDataCache;
import ru.home.mywizard_bot.model.UserProfileData;
import ru.home.mywizard_bot.service.MainMenuService;
import ru.home.mywizard_bot.service.ReplyMessagesService;

import java.io.*;


@Component
@Slf4j
public class TelegramFacade {
    private BotStateContext botStateContext;
    private UserDataCache userDataCache;
    private MainMenuService mainMenuService;
    private MyWizardTelegramBot myWizardBot;
    private ReplyMessagesService messagesService;
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(TelegramFacade.class);

    public TelegramFacade(BotStateContext botStateContext, UserDataCache userDataCache, MainMenuService mainMenuService,
                          @Lazy MyWizardTelegramBot myWizardBot, ReplyMessagesService messagesService) {
        this.botStateContext = botStateContext;
        this.userDataCache = userDataCache;
        this.mainMenuService = mainMenuService;
        this.myWizardBot = myWizardBot;
        this.messagesService = messagesService;
    }

    public BotApiMethod<?> handleUpdate(Update update) throws IOException, TelegramApiException {
        SendMessage replyMessage = null;

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            log.info("New callbackQuery from User: {}, userId: {}, with data: {}", update.getCallbackQuery().getFrom().getUserName(),
                    callbackQuery.getFrom().getId(), update.getCallbackQuery().getData());
            return processCallbackQuery(callbackQuery);
        }


        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            log.info("New message from User:{}, userId: {}, chatId: {},  with text: {}",
                    message.getFrom().getUserName(), message.getFrom().getId(), message.getChatId(), message.getText());
            replyMessage = handleInputMessage(message);
        }

        return replyMessage;
    }


    private SendMessage handleInputMessage(Message message) throws IOException, TelegramApiException {
        String inputMsg = message.getText();
        int userId = message.getFrom().getId();

        long chatId = message.getChatId();
        BotState botState;
        SendMessage replyMessage;

        switch (inputMsg) {
            case "/start":
                botState = BotState.ASK_DESTINY;
                myWizardBot.sendPhoto(chatId, messagesService.getReplyText("reply.hello"), "static/images/top.png");
                break;
            case "Заполнить данные для доставки":
                botState = BotState.FILLING_PROFILE;
                break;
            case "Мои данные":
                botState = BotState.SHOW_USER_PROFILE;
                break;
            case "Скачать счет":
                myWizardBot.sendDocument(chatId, "Ваш счет сэр", getUsersProfile(userId));
                botState = BotState.SHOW_USER_PROFILE;
                break;
            case "Оплатил":
                botState = BotState.SHOW_PAYED_MENU;
                break;
            default:
                botState = userDataCache.getUsersCurrentBotState(userId);
                break;
        }

        userDataCache.setUsersCurrentBotState(userId, botState);

        replyMessage = botStateContext.processInputMessage(botState, message);

        return replyMessage;
    }


    private BotApiMethod<?> processCallbackQuery(CallbackQuery buttonQuery) throws IOException, TelegramApiException {
        final long chatId = buttonQuery.getMessage().getChatId();
        final int userId = buttonQuery.getFrom().getId();
        BotApiMethod<?> callBackAnswer = mainMenuService.getMainMenuMessage(chatId, "Воспользуйтесь главным меню");


        //From Destiny choose buttons
        if (buttonQuery.getData().equals("buttonYes")) {
            callBackAnswer = new SendMessage(chatId, "Тогда заполним данные для доставки. Как тебя зовут ?");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_LAST_NAME);
        } else if (buttonQuery.getData().equals("buttonNo")) {
            callBackAnswer = sendAnswerCallbackQuery("Тогда иди гуляй", false, buttonQuery);
        } else if (buttonQuery.getData().equals("buttonIwillThink")) {
            callBackAnswer = sendAnswerCallbackQuery("Пока ты думаешь другие уже подумали, а товара на складе все меньше!", true, buttonQuery);
            myWizardBot.sendPhoto(chatId, messagesService.getReplyText("reply.podumali"), "static/images/podumali.png");
        }

        //From Country choose buttons
        else if (buttonQuery.getData().equals("buttonUkr")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setCountry("Украина");
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_NUMBER);
            callBackAnswer = new SendMessage(chatId, "Полный адрес для доставки, твоя хата или склад доставщика");
        } else if (buttonQuery.getData().equals("buttonRus")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setCountry("Россия");
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_NUMBER);
            callBackAnswer = new SendMessage(chatId, "Полный адрес для доставки, твоя хата или склад доставщика");
        } else {
            userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_MAIN_MENU);
        }
        return callBackAnswer;
    }


    private AnswerCallbackQuery sendAnswerCallbackQuery(String text, boolean alert, CallbackQuery callbackquery) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackquery.getId());
        answerCallbackQuery.setShowAlert(alert);
        answerCallbackQuery.setText(text);
        return answerCallbackQuery;
    }

    @SneakyThrows
    public File getUsersProfile(int userId) throws IOException {
        UserProfileData userProfileData = userDataCache.getUserProfileData(userId);

        InputStream inputStream = new ClassPathResource(
                "static/docs/zakaz.txt").getInputStream();

//        If u want get rid of numbers in file name use this code but u must change directory path to Heroku dp
/*
        File profileFile = new File("static/docs/bill.txt");

        byte[] buffer = new byte[inputStream.available()];
        inputStream.read(buffer);

        OutputStream outStream = new FileOutputStream(profileFile);
        outStream.write(buffer);
*/

        File profileFile = File.createTempFile("bill", ".txt");

        try {
            FileUtils.copyInputStreamToFile(inputStream, profileFile);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        try (FileWriter fw = new FileWriter(profileFile.getAbsoluteFile());
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(userProfileData.toString());
        }

        return profileFile;

    }


}
