package org.example.bot.provider.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.bot.config.JDBCConfig;
import org.example.bot.provider.JSONProvider;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JSONProviderImpl implements JSONProvider {
    private static final Logger log = LogManager.getLogger(JSONProviderImpl.class);
    private static Connection connection = null;

    public JSONProviderImpl() throws SQLException {
        connection = JDBCConfig.getConnection();
    }

    @Override
    public void create(Update update) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO tgbot.json_messages (message) VALUES (?);")) {
            preparedStatement.setString(1, update.toString());
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }
}
