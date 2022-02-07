package org.example.bot;

import org.example.bot.bot.Bot;
import org.example.bot.bot.impl.TelegramBot;
import org.example.bot.config.HibernateConfig;
import org.example.bot.config.JedisConfig;
import org.example.bot.config.RabbitMQConfig;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

public class App {
    public static void main(String[] args) throws IOException, SQLException, TelegramApiException {
        App app = new App();
        HibernateConfig.init(app.getProperties("/hibernate.properties"));
        JedisConfig.init(app.getProperties("/jedis.properties"));
        RabbitMQConfig.init(app.getProperties("/mq.properties"));
        Bot bot = new TelegramBot(app.getProperties("/telegramBot.properties"));
        bot.botConnect();
    }

    private Properties getProperties(String fileName) throws IOException {
        try (InputStream is = this.getClass().getResourceAsStream(fileName)) {
            Properties properties = new Properties();
            properties.load(is);
            return properties;
        }
    }
}