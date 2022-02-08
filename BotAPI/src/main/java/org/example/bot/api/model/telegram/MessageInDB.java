package org.example.bot.api.model.telegram;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageInDB implements Serializable {
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
