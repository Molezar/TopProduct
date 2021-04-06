package ru.home.mywizard_bot.botapi;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

/**Обработчик сообщений
 */
public interface InputMessageHandler {
    SendMessage handle(Message message) throws IOException, TelegramApiException;

    BotState getHandlerName();
}
