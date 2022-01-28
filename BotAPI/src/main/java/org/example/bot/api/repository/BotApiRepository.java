package org.example.bot.api.repository;

import org.example.bot.api.model.telegram.TGChat;
import org.example.bot.api.model.telegram.TGUser;

public interface BotApiRepository {
    String getUserId(String token);

    TGUser getUser(String userId);

    TGChat getChat(String chatId);
}
