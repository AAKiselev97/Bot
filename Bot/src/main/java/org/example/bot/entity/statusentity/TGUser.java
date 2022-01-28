package org.example.bot.entity.statusentity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TGUser implements Serializable {
    private String userName;
    private Long userId;
    private Integer badWordsCounter;
    private Timestamp lastBadWordTimeStamp;
}

