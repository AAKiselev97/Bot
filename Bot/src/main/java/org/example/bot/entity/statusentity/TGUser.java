package org.example.bot.entity.statusentity;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
public class TGUser implements Serializable {
    private String userName;
    private Long userId;
    private Integer badWordsCounter;
    private Timestamp lastBadWordTimeStamp;
}

