package org.example.bot.provider;

import org.example.bot.entity.JSONMessageInDB;

public interface JSONProvider {
    void create(JSONMessageInDB jsonMessageInDB);
}
