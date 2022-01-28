package org.example.bot.bot;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface Bot {
    void getMessage(Update update) throws TelegramApiException;

    void sendMessage(String text, Long chatId) throws TelegramApiException;

    void formResponseToCommand(Update update, Long chatId) throws TelegramApiException;

    void botConnect() throws TelegramApiException;
}
