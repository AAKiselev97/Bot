package org.example.bot.provider.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.bot.config.JDBCConfig;
import org.example.bot.entity.TGMessage;
import org.example.bot.provider.MessageProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MessageProviderImpl implements MessageProvider {
    private final static Logger log = LogManager.getLogger(MessageProviderImpl.class);
    private final String TELEGRAM_USER_SIGN;
    private static Connection connection;

    public MessageProviderImpl(String sign) throws SQLException {
        this.TELEGRAM_USER_SIGN = sign;
        connection = JDBCConfig.getConnection();
    }

    @Override
    public void create(TGMessage message) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO tgbot.descrypted_messages (json_id, chat_id, message, message_id, type, username)" +
                "VALUES ((SELECT MAX(`id`) FROM tgbot.json_messages), ?, ?, ?, ?, ?);")) {
            preparedStatement.setLong(1, message.getChatId());
            preparedStatement.setString(2, message.getMessage());
            preparedStatement.setLong(3, message.getMessageId());
            preparedStatement.setString(4, message.getType().name().toLowerCase());
            preparedStatement.setString(5, TELEGRAM_USER_SIGN + message.getUserName());
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void update(TGMessage message) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE tgbot.descrypted_messages" +
                " SET message = ?, update_date = CURRENT_TIMESTAMP, is_update = true WHERE message_id = ?")) {
            preparedStatement.setString(1, message.getMessage());
            preparedStatement.setLong(2, message.getMessageId());
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error(e);
        }
    }

    @Override
    public List<String> getHistory(String userName) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT creation_date, chat_id, message FROM tgbot.descrypted_messages WHERE username = ?;")) {
            preparedStatement.setString(1, userName);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<String> list = new ArrayList<>();
            while (resultSet.next()) {
                String string = "Creation date [" + (resultSet.getTimestamp(1).toLocalDateTime()) + "], chatId [" + resultSet.getLong(2) +
                        "]\nMessage [" + resultSet.getString(3) + "]\n";
                list.add(string);
            }
            return list;
        } catch (SQLException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }
}
