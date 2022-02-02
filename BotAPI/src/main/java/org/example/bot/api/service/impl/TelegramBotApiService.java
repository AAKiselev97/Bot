package org.example.bot.api.service.impl;

import org.example.bot.api.exception.BadRequestException;
import org.example.bot.api.exception.EnoughRightException;
import org.example.bot.api.exception.ServerErrorException;
import org.example.bot.api.model.telegram.TGChat;
import org.example.bot.api.model.telegram.TGUser;
import org.example.bot.api.repository.BotApiRepository;
import org.example.bot.api.service.BotApiService;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.UUID;

@Service
public class TelegramBotApiService implements BotApiService {
    private static final String QUEUE_GET_HISTORY_TO_PDF = "GetHistoryQueue";
    private final RabbitTemplate rabbitTemplate;
    private final Binding binding;
    private final BotApiRepository botApiRepository;

    public TelegramBotApiService(RabbitTemplate rabbitTemplate, Binding binding, BotApiRepository botApiRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.binding = binding;
        this.botApiRepository = botApiRepository;
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

    @Override
    public InputStreamResource getUserHistory(String message) {
        try {
            byte[] bytes = (byte[]) rabbitTemplate.convertSendAndReceive(QUEUE_GET_HISTORY_TO_PDF, message);
            if (bytes != null) {
                File file = new File(new String(bytes));
                if (file.exists()) {
                    return new InputStreamResource(new FileInputStream((file)));
                }
            }
        } catch (FileNotFoundException e) {
            throw new ServerErrorException(e);
        }
        throw new ServerErrorException("Bot not available");
    }

    @Override
    public String formUserHistory(String token) {
        try {
            String tokenByHistory = UUID.randomUUID() + "_" + botApiRepository.getUser(botApiRepository.getUserId(token)).getUserId();
            rabbitTemplate.convertAndSend(binding.getExchange(), binding.getRoutingKey(), tokenByHistory);
            return "all good, history in /telegram/bot/get/getUserHistory/{token} by token" + tokenByHistory;
        } catch (Exception e) {
            return "something wrong";
        }
    }

    private void checkId(String id) {
        try {
            Long.parseLong(id);
        } catch (Exception e) {
            throw new BadRequestException("invalid Id", e);
        }
    }
}
