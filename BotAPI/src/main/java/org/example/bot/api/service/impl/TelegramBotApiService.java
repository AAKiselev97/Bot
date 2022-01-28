package org.example.bot.api.service.impl;

import org.example.bot.api.exception.BadRequestException;
import org.example.bot.api.exception.EnoughRightException;
import org.example.bot.api.exception.ServerErrorException;
import org.example.bot.api.model.telegram.TGChat;
import org.example.bot.api.model.telegram.TGUser;
import org.example.bot.api.repository.BotApiRepository;
import org.example.bot.api.service.BotApiService;
import org.springframework.stereotype.Service;

@Service
public class TelegramBotApiService implements BotApiService {
    private final BotApiRepository botApiRepository;

    public TelegramBotApiService(BotApiRepository botAPIRepository) {
        this.botApiRepository = botAPIRepository;
    }

    @Override
    public TGUser getUserStats(String token) {
        TGUser user = botApiRepository.getUser(botApiRepository.getUserId(token));
        if (user != null) {
            return user;
        } else throw new ServerErrorException();
    }

    @Override
    public TGChat getChatStats(String chatId, String token) {
        checkId(chatId);
        TGUser user = botApiRepository.getUser(botApiRepository.getUserId(token));
        TGChat tgChat = botApiRepository.getChat(chatId);
        for (TGUser tgUser : tgChat.getTgUsers()) {
            if (tgUser.getUserId().toString().equals(user.getUserId().toString())) {
                return tgChat;
            }
        }
        throw new EnoughRightException();
    }

    @Override
    public TGUser getUserStatsByChat(String chatId, String token) {
        checkId(chatId);
        TGUser user = botApiRepository.getUser(botApiRepository.getUserId(token));
        TGChat tgChat = botApiRepository.getChat(chatId);
        for (TGUser tgUser : tgChat.getTgUsers()) {
            if (tgUser.getUserId().toString().equals(user.getUserId().toString())) {
                return tgUser;
            }
        }
        throw new EnoughRightException();
    }

    private void checkId(String id) {
        try {
            Long.parseLong(id);
        } catch (Exception e) {
            throw new BadRequestException("invalid Id", e);
        }
    }
}
