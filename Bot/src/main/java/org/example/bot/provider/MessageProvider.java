package org.example.bot.provider;

import org.example.bot.entity.MessageInDB;

import java.util.List;

public interface MessageProvider {
    void create(MessageInDB message);

    void update(MessageInDB message);

    List<String> getHistory(String userName);

    List<MessageInDB> searchByText(String text, String username);
}
