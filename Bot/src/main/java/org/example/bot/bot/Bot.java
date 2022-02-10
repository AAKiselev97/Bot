package org.example.bot.bot;

import org.example.bot.entity.MessageInDB;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

public interface Bot {
    void getMessage(Update update) throws TelegramApiException, IOException, TimeoutException;

    void sendMessage(String text, Long chatId) throws TelegramApiException;

    void formResponseToCommand(Update update, Long chatId) throws TelegramApiException;

    void botConnect() throws TelegramApiException;

    void formHistory(String fileName, String id);

    List<MessageInDB> searchByText(String text, String username, int page);

    void botDisconnect();
}
