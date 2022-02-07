package org.example.bot.entity;

import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "json_messages")
@Data
@Builder
public class JSONMessageInDB {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "message", nullable = false)
    private String message;
    @CreationTimestamp
    @Column(name = "creation_date", nullable = false)
    private Timestamp creationDate;
}
