package org.example.bot.api.model.telegram;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class MessageInDB {
    private int id;

    private int jsonId;

    private Long chatId;

    private String message;

    private int messageId;

    private MessageType type;

    private Timestamp creationDate;

    private Timestamp updateDate;

    private boolean isUpdate;

    private String username;
}
