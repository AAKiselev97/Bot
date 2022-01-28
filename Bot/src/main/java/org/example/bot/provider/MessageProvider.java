package org.example.bot.provider;

import org.example.bot.entity.TGMessage;

public interface MessageProvider {
    void create(TGMessage message);

    void update(TGMessage message);
}
