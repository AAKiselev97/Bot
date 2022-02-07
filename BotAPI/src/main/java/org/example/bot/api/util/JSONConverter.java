package org.example.bot.api.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.bot.api.exception.ServerErrorException;
import org.example.bot.api.model.telegram.MessageInDB;
import org.example.bot.api.model.telegram.TGChat;
import org.example.bot.api.model.telegram.TGUser;

import java.io.IOException;

public class JSONConverter {
    private static final Logger log = LogManager.getLogger(JSONConverter.class);

    public static TGUser JSONToTGUser(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, TGUser.class);
        } catch (IOException e) {
            log.error(e);
            throw new ServerErrorException("convert json to java object not success", e);
        }
    }

    public static TGChat JSONToTGChat(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, TGChat.class);
        } catch (IOException e) {
            log.error(e);
            throw new ServerErrorException("convert json to java object not success", e);
        }
    }

    public static MessageInDB JSONToMessageInDB(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, MessageInDB.class);
        } catch (IOException e) {
            log.error(e);
            throw new ServerErrorException("convert json to java object not success", e);
        }
    }
}
