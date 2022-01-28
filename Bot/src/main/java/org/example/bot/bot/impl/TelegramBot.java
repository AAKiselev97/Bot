package org.example.bot.bot.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.bot.bot.Bot;
import org.example.bot.command.Commands;
import org.example.bot.config.JedisConfig;
import org.example.bot.counter.MessageCounter;
import org.example.bot.entity.TGMessage;
import org.example.bot.provider.JSONProvider;
import org.example.bot.provider.MessageProvider;
import org.example.bot.provider.impl.JSONProviderImpl;
import org.example.bot.provider.impl.MessageProviderImpl;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import redis.clients.jedis.Jedis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;


@AllArgsConstructor
public class TelegramBot extends TelegramLongPollingBot implements Bot {
    private static final Logger log = LogManager.getLogger(TelegramBot.class);
    private static final String TELEGRAM_USER_SIGN = "@";
    private static final String TELEGRAM_COMMAND_SIGN = "/";
    private static final String HELLO_MESSAGE_FILENAME = "helloText.txt";
    private static final String UNKNOWN_MESSAGE_FILENAME = "unknownText.txt";
    private static JSONProvider jsonProvider;
    private static MessageProvider messageProvider;
    private static MessageCounter messageCounter;
    private final int RECONNECT_PAUSE = 10000;

    @Setter
    @Getter
    private String userName;
    @Setter
    @Getter
    private String token;

    public TelegramBot(Properties properties) throws SQLException {
        jsonProvider = new JSONProviderImpl();
        messageProvider = new MessageProviderImpl(TELEGRAM_USER_SIGN);
        messageCounter = MessageCounter.getMessageCounter();
        messageCounter.init();
        this.userName = properties.getProperty("userName");
        this.token = properties.getProperty("token");
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            getMessage(update);
        } catch (TelegramApiException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    public void getMessage(Update update) throws TelegramApiException {
        log.debug("Receive new Update. updateID: " + update.getUpdateId());
        jsonProvider.create(update);
        if (update.hasEditedMessage()) {
            messageProvider.update(TGMessage.parseMessageToTGMessage(update.getEditedMessage()));
            return;
        }
        Message message = null;
        if (update.getMessage().hasText()) {
            message = update.getMessage();
            messageProvider.create(TGMessage.parseMessageToTGMessage(message));
            messageCounter.scanString(message.getChat(), message.getFrom(), message.getText());
            if (!isMessageForBot(update)) {
                return;
            }
            if (isCommand(update)) {
                formResponseToCommand(update, update.getMessage().getChatId());
            } else {
                log.debug(message.getText());
                formResponseByUnknownText(update);
            }
        } else if (message.hasContact() || message.hasPhoto() || message.hasDocument() || message.hasEntities() || message.hasInvoice()
                || message.hasLocation() || message.hasSuccessfulPayment() || message.hasVideo()) {
            try {
                sendMessage("Не умею обрабатывать такие сообщения", update.getMessage().getChatId());
            } catch (TelegramApiException e) {
                log.error(e);
                throw new RuntimeException(e);
            }
        }
    }

    public void formResponseByUnknownText(Update update) throws TelegramApiException {
        List<String> helloWordList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader((Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream(HELLO_MESSAGE_FILENAME))), StandardCharsets.UTF_8))) {
            br.lines().forEach(helloWordList::add);
        } catch (IOException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
        String[] words = update.getMessage().getText().split(" ");
        for (String word : words) {
            for (String helloWord : helloWordList) {
                if (word.equalsIgnoreCase(helloWord)) {
                    sendMessage(formHelloMessage(update), update.getMessage().getChatId());
                    return;
                }
            }
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream(UNKNOWN_MESSAGE_FILENAME)), StandardCharsets.UTF_8))) {
            List<String> textList = new ArrayList<>();
            br.lines().forEach(textList::add);
            sendMessage(textList.get((int) (Math.random() * (textList.size() - 1))), update.getMessage().getChatId());
        } catch (IOException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String text, Long chatId) throws TelegramApiException {
        SendChatAction sendChatAction = new SendChatAction();
        sendChatAction.setChatId(String.valueOf(chatId));
        sendChatAction.setAction(ActionType.TYPING);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(text);
        try {
            execute(sendChatAction);
            Thread.sleep(text.length() * 10L);
            execute(sendMessage);
        } catch (TelegramApiRequestException | InterruptedException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void formResponseToCommand(Update update, Long chatId) throws TelegramApiException {
        String command = update.getMessage().getText().replace(TELEGRAM_USER_SIGN + userName, "");
        String[] strings = command.split(" ");
        String userName = "";
        for (String string : strings) {
            if (string.contains(TELEGRAM_COMMAND_SIGN)) {
                command = string;
            } else if (string.contains(TELEGRAM_USER_SIGN)) {
                userName = string;
            }
        }
        switch (Commands.stringToCommand(command.replace(TELEGRAM_COMMAND_SIGN, ""))) {
            case START:
                sendMessage("Hello. This is start message", chatId);
                break;
            case HELLO:
                sendMessage(formHelloMessage(update), update.getMessage().getChatId());
                break;
            case HELP:
                StringBuilder sb = new StringBuilder();
                Arrays.stream(Commands.values()).forEach(commands -> sb.append(commands.getDescription()).append("\n"));
                sendMessage(sb.toString(), chatId);
                break;
            case STAT:
                sendMessage(messageCounter.getUserStat(chatId, userName), chatId);
                break;
            case STATCHAT:
                sendMessage(messageCounter.getChatStat(chatId), chatId);
                break;
            case TOP:
                sendMessage(messageCounter.getTop(chatId), chatId);
                break;
            case TOKEN:
                String token = String.valueOf(UUID.randomUUID());
                try (Jedis jedis = JedisConfig.getJedis()) {
                    User user = update.getMessage().getFrom();
                    jedis.setex(token, TimeUnit.DAYS.toSeconds(30), user.getId().toString());
                } catch (Exception e) {
                    log.error(e);
                    throw new RuntimeException(e);
                } finally {
                    sendMessage("Ваш токен: " + token, update.getMessage().getFrom().getId());
                }
                break;
            case GETCHATID:
                sendMessage(update.getMessage().getChatId().toString(), chatId);
            case EMPTY:
                break;
            default:
                formResponseByUnknownText(update);
        }
    }

    @Override
    public String getBotUsername() {
        return userName;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    public void botConnect() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(this);
            log.info("TelegramAPI started. Look for messages");
            log.debug("Bot connect");
        } catch (TelegramApiException e) {
            log.error("Cant Connect. Pause " + RECONNECT_PAUSE / 1000 + "sec and try again. Error: " + e.getMessage());
            try {
                Thread.sleep(RECONNECT_PAUSE);
            } catch (InterruptedException e1) {
                log.error(e1);
                return;
            }
            botConnect();
        }
    }

    private String formHelloMessage(Update update) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader((Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream(HELLO_MESSAGE_FILENAME))), StandardCharsets.UTF_8))) {
            List<String> textList = new ArrayList<>();
            br.lines().forEach(textList::add);
            String text = textList.get((int) (Math.random() * (textList.size())));
            if (Math.random() * 2 > 1) {
                text = (TELEGRAM_USER_SIGN + update.getMessage().getFrom().getUserName() + " " + text);
            }
            return text;
        } catch (IOException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    private boolean isCommand(Update update) {
        return update.getMessage().getText().contains(TELEGRAM_COMMAND_SIGN);
    }

    private boolean isMessageForBot(Update update) {
        String[] words = update.getMessage().getText().split(TELEGRAM_USER_SIGN);
        words[0] = words[0].replace(TELEGRAM_COMMAND_SIGN, "");
        if (update.getMessage().getChat().isUserChat()) {
            return true;
        } else if (Commands.isCommand(words[0])) {
            return true;
        }
        return update.getMessage().getText().startsWith(TELEGRAM_USER_SIGN + userName);
    }
}
