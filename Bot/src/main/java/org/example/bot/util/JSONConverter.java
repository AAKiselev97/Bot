package org.example.bot.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.example.bot.entity.statusentity.TGChat;
import org.example.bot.entity.statusentity.TGUser;
import org.telegram.telegrambots.meta.api.objects.Update;

public class JSONConverter {
    public static String tgUserToJSONInString(TGUser tgUser) throws JsonProcessingException {
        ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return objectWriter.writeValueAsString(tgUser);
    }

    public static String tgChatToJSONInString(TGChat tgChat) throws JsonProcessingException {
        ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return objectWriter.writeValueAsString(tgChat);
    }

    public static TGUser jsonToTGUser(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, TGUser.class);
    }

    public static TGChat jsonToTGChat(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, TGChat.class);
    }

    public static String updateToJSONInString(Update update) throws JsonProcessingException {
        ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return objectWriter.writeValueAsString(update);
    }

    public static Update jsonToUpdate(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, Update.class);
    }
}
