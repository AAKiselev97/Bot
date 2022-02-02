package org.example.bot.provider;

import org.example.bot.entity.TGMessage;

import java.util.List;

public interface MessageProvider {
    void create(TGMessage message);

    void update(TGMessage message);

    List<String> getHistory(String userName);
}
