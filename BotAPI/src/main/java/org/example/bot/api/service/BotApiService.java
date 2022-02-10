package org.example.bot.api.service;

import org.example.bot.api.model.telegram.MessageInDB;
import org.example.bot.api.model.telegram.TGChat;
import org.example.bot.api.model.telegram.TGUser;
import org.springframework.core.io.InputStreamResource;

import java.util.List;

public interface BotApiService {
    TGUser getUserStats(String token);

    TGChat getChatStats(String token, String chatName);

    TGUser getUserStatsByChat(String token, String chatName);

    String formUserHistory(String token);

    InputStreamResource getUserHistory(String token);

    List<MessageInDB> searchByText(String token, String text, int page);
}
