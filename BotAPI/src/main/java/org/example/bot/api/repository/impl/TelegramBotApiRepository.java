package org.example.bot.api.repository.impl;

import org.example.bot.api.exception.BadRequestException;
import org.example.bot.api.exception.ServerErrorException;
import org.example.bot.api.model.telegram.TGChat;
import org.example.bot.api.model.telegram.TGUser;
import org.example.bot.api.repository.BotApiRepository;
import org.example.bot.api.util.JSONConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Repository
public class TelegramBotApiRepository implements BotApiRepository {
    private final JedisPool jedisPool;

    @Autowired
    public TelegramBotApiRepository(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public String getUserId(String token) {
        return get(token);
    }

    public TGChat getChat(String chatId) {
        return JSONConverter.JSONToTGChat(get(chatId));
    }

    public TGUser getUser(String userId) {
        return JSONConverter.JSONToTGUser(get(userId));
    }

    private String get(String string) {
        try (Jedis jedis = jedisPool.getResource()) {
            String str = jedis.get(string);
            if (str != null) {
                return str;
            }
            throw new ServerErrorException("something wrong in data base");
        } catch (RuntimeException e) {
            //проверка, что бы контроллер не отправил в ошибке чей-то JSON из jedis
            throw new BadRequestException("value " + (string.length() > 36 ? "in redis nor found" : "by token [" + string + "] not found"));
        }
    }
}
