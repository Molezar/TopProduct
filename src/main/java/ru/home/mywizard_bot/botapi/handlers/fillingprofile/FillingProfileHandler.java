package ru.home.mywizard_bot.botapi.handlers.fillingprofile;

import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.home.mywizard_bot.MyWizardTelegramBot;
import ru.home.mywizard_bot.botapi.BotState;
import ru.home.mywizard_bot.botapi.InputMessageHandler;
import ru.home.mywizard_bot.botapi.TelegramFacade;
import ru.home.mywizard_bot.cache.UserDataCache;
import ru.home.mywizard_bot.model.UserProfileData;
import ru.home.mywizard_bot.service.MainMenuService;
import ru.home.mywizard_bot.service.PredictionService;
import ru.home.mywizard_bot.service.ReplyMessagesService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Формирует анкету пользователя.
 */

@Slf4j
@Component
public class FillingProfileHandler implements InputMessageHandler {
    private UserDataCache userDataCache;
    private ReplyMessagesService messagesService;
    private PredictionService predictionService;
    private TelegramFacade telegramFacade;
    private MyWizardTelegramBot myWizardBot;
    private MainMenuService mainMenuService;


    public FillingProfileHandler( UserDataCache userDataCache, ReplyMessagesService messagesService,
                                  PredictionService predictionService, @Lazy TelegramFacade telegramFacade,
                                  @Lazy MyWizardTelegramBot myWizardBot, @Lazy MainMenuService mainMenuService) {
        this.userDataCache = userDataCache;
        this.messagesService = messagesService;
        this.predictionService = predictionService;
        this.telegramFacade = telegramFacade;
        this.myWizardBot = myWizardBot;
        this.mainMenuService = mainMenuService;

    }

    @Override
    public SendMessage handle(Message message) throws IOException, TelegramApiException {
        if (userDataCache.getUsersCurrentBotState(message.getFrom().getId()).equals(BotState.FILLING_PROFILE)) {
            userDataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.ASK_NAME);
        }
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.FILLING_PROFILE;
    }

    private SendMessage processUsersInput(Message inputMsg) throws IOException, TelegramApiException {
        String usersAnswer = inputMsg.getText();
        int userId = inputMsg.getFrom().getId();
        long chatId = inputMsg.getChatId();

        UserProfileData profileData = userDataCache.getUserProfileData(userId);
        BotState botState = userDataCache.getUsersCurrentBotState(userId);

        SendMessage replyToUser = null;

        if (botState.equals(BotState.ASK_NAME)) {
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askName");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_LAST_NAME);
        }

        if (botState.equals(BotState.ASK_LAST_NAME)) {
            profileData.setName(usersAnswer);
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askLastName");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_COUNTRY);
        }

        if (botState.equals(BotState.ASK_COUNTRY)) {
            profileData.setLastName(usersAnswer);
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askCountry");
            replyToUser.setReplyMarkup(getCountryButtonsMarkup());

        }

        if (botState.equals(BotState.ASK_NUMBER)) {
            profileData.setAdres(usersAnswer);
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askNumber");
            userDataCache.setUsersCurrentBotState(userId, BotState.PROFILE_FILLED);
        }

        if (botState.equals(BotState.PROFILE_FILLED)) {
            profileData.setNumber(usersAnswer);
            userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_MAIN_MENU);

            String profileFilledMessage = messagesService.getReplyText("reply.profileFilled",
                    profileData.getName(), EmojiParser.parseToUnicode(":sparkles:"));
            String predictionMessage = predictionService.getPrediction();

            replyToUser = new SendMessage(chatId, String.format("%s%n%n%s %s", profileFilledMessage, EmojiParser.parseToUnicode(":scroll:"), predictionMessage));
            replyToUser.setReplyMarkup(getOplatilButtonMarkup());
            replyToUser.setParseMode("HTML");
            myWizardBot.sendDocument(chatId, "Ваш счет сэр", telegramFacade.getUsersProfile(userId));

        }

        userDataCache.saveUserProfileData(userId, profileData);
        return replyToUser;
    }

    private InlineKeyboardMarkup getOplatilButtonMarkup() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton buttonOplatil = new InlineKeyboardButton().setText("А где эта долбаная кнопка?");
        buttonOplatil.setCallbackData("oplatil");
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(buttonOplatil);
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }

    private InlineKeyboardMarkup getCountryButtonsMarkup() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton buttonUkr = new InlineKeyboardButton().setText("Украина");
        InlineKeyboardButton buttonRus = new InlineKeyboardButton().setText("Россия");

        //Every button must have callBackData, or else not work !
        buttonUkr.setCallbackData("buttonUkr");
        buttonRus.setCallbackData("buttonRus");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(buttonUkr);
        keyboardButtonsRow1.add(buttonRus);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }
}



