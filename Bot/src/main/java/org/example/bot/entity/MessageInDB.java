package org.example.bot.entity;

import lombok.Builder;
import lombok.Data;
import org.example.bot.bot.MessageType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "descrypted_messages")
@Data
@Builder
public class MessageInDB {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "json_id", nullable = false)
    private int jsonId;
    @Column(name = "chat_id", nullable = false)
    private Long chatId;
    @Column(name = "message", nullable = false)
    private String message;
    @NaturalId
    @Column(name = "message_id", nullable = false)
    private int messageId;
    @Column(name = "type", nullable = false)
    private MessageType type;
    @CreationTimestamp
    @Column(name = "creation_date", updatable = false)
    private Timestamp creationDate;
    @UpdateTimestamp
    @Column(name = "update_date")
    private Timestamp updateDate;
    @Column(name = "is_update")
    private boolean isUpdate;
    @Column(name = "username", nullable = false)
    private String username;
}
