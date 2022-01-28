package org.example.bot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.bot.bot.MessageType;
import org.telegram.telegrambots.meta.api.objects.Message;

@Data
@AllArgsConstructor
public class TGMessage {
    private Long chatId;
    private String message;
    private Integer messageId;
    private MessageType type;
    private String userName;

    public static TGMessage parseMessageToTGMessage(Message message) {
        if (message.getText().contains("/")) {
            return new TGMessage(message.getChatId(), message.getText(), message.getMessageId(), MessageType.COMMAND, message.getFrom().getUserName());
        } else {
            return new TGMessage(message.getChatId(), message.getText(), message.getMessageId(), MessageType.TEXT, message.getFrom().getUserName());
        }
    }
}

