package ru.home.mywizard_bot.botapi.handlers.menu;

import com.vdurmont.emoji.EmojiParser;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.home.mywizard_bot.MyWizardTelegramBot;
import ru.home.mywizard_bot.botapi.BotState;
import ru.home.mywizard_bot.botapi.InputMessageHandler;
import ru.home.mywizard_bot.botapi.TelegramFacade;
import ru.home.mywizard_bot.service.MainMenuService;
import ru.home.mywizard_bot.service.ReplyMessagesService;

import java.io.IOException;


@Component
public class PayedMenuHandler implements InputMessageHandler {
    private MainMenuService mainMenuService;
    private ReplyMessagesService messagesService;
    private MyWizardTelegramBot myWizardBot;
    private TelegramFacade telegramFacade;

    public PayedMenuHandler(MainMenuService mainMenuService, ReplyMessagesService messagesService, @Lazy TelegramFacade telegramFacade, @Lazy MyWizardTelegramBot myWizardBot) {
        this.mainMenuService = mainMenuService;
        this.messagesService = messagesService;
        this.myWizardBot = myWizardBot;
        this.telegramFacade = telegramFacade;

    }

    @Override
    public SendMessage handle(Message message) throws IOException, TelegramApiException {
        int userId = message.getFrom().getId();

        myWizardBot.sendPhoto(-504852965, messagesService.getReplyText("reply.adminInform"), "static/images/podumali.png");
        myWizardBot.sendDocument(-504852965, "Оплаченный заказ", telegramFacade.getUsersProfile(userId));

        return mainMenuService.getMainMenuMessage(message.getChatId(),
                messagesService.getReplyText("reply.showPayed", EmojiParser.parseToUnicode(":mage:")));

    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_PAYED_MENU;
    }
}
