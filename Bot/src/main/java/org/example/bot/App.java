package org.example.bot;

import org.example.bot.bot.Bot;
import org.example.bot.bot.impl.TelegramBot;
import org.example.bot.config.JDBCConfig;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

public class App {
    public static void main(String[] args) throws IOException, SQLException, TelegramApiException {

        App app = new App();
        Properties properties = app.getProperties();
        JDBCConfig.init(properties);
        Bot bot = new TelegramBot(properties);
        bot.botConnect();
    }

    private Properties getProperties() throws IOException {
        try (InputStream is = this.getClass().getResourceAsStream("/config.properties")) {
            Properties properties = new Properties();
            properties.load(is);
            return properties;
        }
    }
}