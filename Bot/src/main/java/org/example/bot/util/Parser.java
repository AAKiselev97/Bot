package org.example.bot.util;

import org.example.bot.bot.MessageType;
import org.example.bot.entity.JSONMessageInDB;
import org.example.bot.entity.MessageInDB;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.sql.Timestamp;

public class Parser {
    private static final String TELEGRAM_USER_SIGN = "@";

    public static JSONMessageInDB parseUpdateToJsonMessageInDB(Update update) {
        return JSONMessageInDB.builder().message(update.toString()).build();
    }

    public static MessageInDB parseUpdateToMessageInDB(Update update, JSONMessageInDB jsonMessageInDB) {
        if (!update.hasEditedMessage()) {
            return MessageInDB.builder()
                    .jsonId(jsonMessageInDB.getId())
                    .chatId(update.getMessage().getChatId())
                    .message(update.getMessage().getText())
                    .messageId(update.getMessage().getMessageId())
                    .type(update.getMessage().getText().startsWith("/") ? MessageType.COMMAND : MessageType.TEXT)
                    .isUpdate(update.hasEditedMessage())
                    .username(TELEGRAM_USER_SIGN + update.getMessage().getFrom().getUserName())
                    .build();
        }
        return MessageInDB.builder()
                .jsonId(jsonMessageInDB.getId())
                .chatId(update.getEditedMessage().getChatId())
                .message(update.getEditedMessage().getText())
                .messageId(update.getEditedMessage().getMessageId())
                .type(update.getEditedMessage().getText().startsWith("/") ? MessageType.COMMAND : MessageType.TEXT)
                .isUpdate(update.hasEditedMessage())
                .username(TELEGRAM_USER_SIGN + update.getEditedMessage().getFrom().getUserName())
                .build();
    }
}
