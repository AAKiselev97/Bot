package org.example.bot.provider;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface JSONProvider {
    void create(Update update);
}
