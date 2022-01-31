package org.example.bot.counter;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.bot.config.JedisConfig;
import org.example.bot.entity.statusentity.TGChat;
import org.example.bot.entity.statusentity.TGUser;
import org.example.bot.util.JSONConverter;
import org.example.bot.util.TXTScanner;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import redis.clients.jedis.Jedis;

import java.io.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


public class MessageCounter implements Serializable {
    private static final Logger log = LogManager.getLogger(MessageCounter.class);
    private static final String FILE_PATH = System.getProperty("user.home") + "/file/badWordsCounter.txt";
    private static final String TELEGRAM_USER_SIGN = "@";
    private static final int MINUTE_IN_HOUR = 60;
    private static List<String> wordSeparatorArray;
    private static MessageCounter messageCounter;
    private List<String> badWords;
    private List<TGChat> tgChats;

    private MessageCounter() {
    }

    public void init() {
        if (tgChats == null) {
            try (FileInputStream fileInputStream = new FileInputStream(FILE_PATH);
                 ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
                tgChats = (List<TGChat>) objectInputStream.readObject();
                try (Jedis jedis = JedisConfig.getJedis()) {
                    for (TGChat tgChat : tgChats) {
                        boolean doubleId = false;
                        for (TGUser tgUser : tgChat.getTgUsers()) {
                            jedis.set(tgUser.getUserId().toString(), JSONConverter.tgUserToJSONInString(tgUser));
                            doubleId = tgUser.getUserId().equals(tgChat.getChatId());
                        }
                        if (!doubleId) {
                            jedis.set(tgChat.getChatId().toString(), JSONConverter.tgChatToJSONInString(tgChat));
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                tgChats = new ArrayList<>();
                File file = new File(FILE_PATH);
                try {
                    file.createNewFile();
                } catch (IOException ex) {
                    log.error(e);
                }
                log.error(e);
            }
        }
        try {
            badWords = TXTScanner.getBadWordsList();
        } catch (IOException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    public static MessageCounter getMessageCounter() {
        if (messageCounter == null) {
            return messageCounter = new MessageCounter();
        } else {
            return messageCounter;
        }
    }

    public TGChat getChat(Long chatId) {
        Optional<TGChat> tgChat = tgChats.stream().filter(s -> Objects.equals(s.getChatId(), chatId)).findAny();
        if (tgChat.isPresent()) {
            return tgChat.get();
        } else {
            throw new RuntimeException("chat not found");
        }
    }

    public String getUserStat(Long chatId, String userName) {
        try {
            TGUser user = getChat(chatId).getUser(userName);
            LocalDateTime localDateTime = user.getLastBadWordTimeStamp().toLocalDateTime();
            String time;
            time = String.format("%d.%d.%d в %d:%d", localDateTime.getDayOfMonth(), localDateTime.getMonthValue(), localDateTime.getYear(), localDateTime.getHour(), localDateTime.getMinute());
            return String.format("Статистика пользователя %s - количество матных слов = [%d]\nПоследний раз матерился %s\n", user.getUserName(), user.getBadWordsCounter(), time);
        } catch (RuntimeException e) {
            log.error(e);
            return "Статистика по данному пользователю не доступна";
        }
    }

    public String getTop(Long chatId) {
        StringBuilder stringBuilder = new StringBuilder();
        TGChat chat = getChat(chatId);
        int top = 1;
        stringBuilder.append("Топ 5 юзеров:\n");
        for (TGUser user : chat.getTop5List()) {
            stringBuilder.append(top).append(". ").append(user.getUserName()).append(" количество матюков: ").append(user.getBadWordsCounter()).append("\n");
            top++;
        }
        return stringBuilder.toString();
    }

    public String getChatStat(Long chatId) {
        try {
            TGChat chat = getChat(chatId);
            StringBuilder sb = new StringBuilder();
            sb.append("Статистика по чату ").append(chat.getChatName()).append("\n");
            sb.append("ID чата - ").append(chat.getChatId()).append("\n");
            sb.append("\nОбщее количество матных слов -").append(chat.getBadWords()).append("\n\n");
            int top = 1;
            for (TGUser user : chat.getTop5List()) {
                long lastBadWord = TimeUnit.HOURS.toMinutes(user.getLastBadWordTimeStamp().toLocalDateTime().getHour()) + user.getLastBadWordTimeStamp().toLocalDateTime().getMinute();
                long nowDate = TimeUnit.HOURS.toMinutes(LocalDateTime.now().getHour()) + LocalDateTime.now().getMinute();
                String time;
                int minute = (int) (nowDate - lastBadWord);
                int hour = minute >= MINUTE_IN_HOUR ? minute / MINUTE_IN_HOUR : 0;
                if (hour > 0) {
                    minute = minute % MINUTE_IN_HOUR;
                }
                LocalDateTime localDateTime = user.getLastBadWordTimeStamp().toLocalDateTime();
                if (localDateTime.isAfter(LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT))) {
                    time = ((hour > 0 ? hour + (hour > 4 & hour < 21 ? " часов " : hour == 1 || hour == 21 ? " час " : " часа ") : "")
                            + (minute < 2 ? "только что" : (minute > 5 ? minute + " минут назад" : minute + " минуты назад")));
                } else {
                    time = String.format("%d.%d.%d в %d:%d", localDateTime.getDayOfMonth(), localDateTime.getMonthValue(), localDateTime.getYear(), localDateTime.getHour(), localDateTime.getMinute());
                }
                sb.append("\n\n").append(top).append(" место, ").append(" количество матных слов - [").append(user.getBadWordsCounter()).append("]\nЮзер ").append(user.getUserName()).append("\n")
                        .append("Последний раз матерился ").append(time);
                top++;
            }
            return sb.toString();
        } catch (RuntimeException e) {
            log.error(e);
            return "Статистика по чату недоступна";
        }
    }

    public void scanString(Chat chat, User user, String string) {
        string = changeEnglishLettersToRusAndChangeAllWordSeparator(string).toLowerCase();
        String[] strings = string.split(" ");
        for (String word : strings) {
            scanWord(chat, user, word);
        }
    }

    public void scanWord(Chat chat, User user, String word) {
        for (String badWord : badWords) {
            if (word.toLowerCase().contains(badWord)) {
                addScore(chat.getTitle(), chat.getId(), TELEGRAM_USER_SIGN + user.getUserName(), user.getId());
                String newWord = word.replaceFirst(badWord, "");
                if (newWord.toLowerCase().contains(badWord)) {
                    scanWord(chat, user, word.replaceFirst(badWord, ""));
                }
            }
        }
    }

    private void save() {
        try (FileOutputStream fileOutputStream = new FileOutputStream(FILE_PATH);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(tgChats);
            log.debug("Save to file success");
        } catch (IOException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    private String changeEnglishLettersToRusAndChangeAllWordSeparator(String string) {
        String[] rusLetter = {"а", "в", "е", "к", "м", "р", "с", "т", "х",};
        String[] engLetter = {"a", "b", "e", "k", "m", "p", "c", "t", "x",};
        for (int i = 0; i < rusLetter.length; i++) {
            string = string.replace(engLetter[i], rusLetter[i]);
        }
        try {
            wordSeparatorArray = TXTScanner.getWordSeparatorList();
        } catch (IOException e) {
            log.error(e);
        }
        for (String separator : wordSeparatorArray) {
            string = string.replace(separator, " ");
        }
        return string;
    }

    private void addScore(String chatName, Long chatId, String userName, Long userId) {
        TGChat chat;
        TGUser user;
        try {
            chat = getChat(chatId);
            chat.setBadWords(chat.getBadWords() + 1);
        } catch (RuntimeException e) {
            log.debug(e.getMessage());
            chat = new TGChat();
            chat.setChatId(chatId);
            tgChats.add(chat);
            chat.setBadWords(1);
            chat.setTgUsers(new ArrayList<>());
        }
        chat.setChatName(chatName);
        try {
            user = chat.getUser(userName);
            user.setBadWordsCounter(user.getBadWordsCounter() + 1);
        } catch (RuntimeException e) {
            log.debug(e.getMessage());
            user = new TGUser();
            user.setUserName(userName);
            user.setUserId(userId);
            user.setBadWordsCounter(1);
            chat.getTgUsers().add(user);
        }
        user.setLastBadWordTimeStamp(new Timestamp(System.currentTimeMillis()));
        chat.checkTop5();
        try (Jedis jedis = JedisConfig.getJedis()) {
            try {
                String json = jedis.get(userId.toString());
                if (json != null) {
                    TGUser userInDB = JSONConverter.jsonToTGUser(jedis.get(userId.toString()));
                    userInDB.setLastBadWordTimeStamp(user.getLastBadWordTimeStamp());
                    userInDB.setBadWordsCounter(userInDB.getBadWordsCounter() + 1);
                    jedis.set(user.getUserId().toString(), JSONConverter.tgUserToJSONInString(userInDB));
                } else {
                    jedis.set(user.getUserId().toString(), JSONConverter.tgUserToJSONInString(user));
                }
                if (!chat.getChatId().equals(user.getUserId())) {
                    jedis.set(chat.getChatId().toString(), JSONConverter.tgChatToJSONInString(chat));
                }
                log.debug("Save to Redis success");
            } catch (JsonProcessingException e) {
                log.error(e);
                throw new RuntimeException(e);
            }
        }
        save();
    }
}
