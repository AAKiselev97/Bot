package org.example.bot.config;


import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

public class RabbitMQConfig {
    private static ConnectionFactory connectionFactory;

    public static void init(Properties properties){
        connectionFactory = new ConnectionFactory();
        connectionFactory.setUsername(properties.getProperty("RabbitMQ.username"));
        connectionFactory.setPassword(properties.getProperty("RabbitMQ.password"));
        connectionFactory.setVirtualHost(properties.getProperty("RabbitMQ.virtualHost"));
        connectionFactory.setHost(properties.getProperty("RabbitMQ.host"));
        connectionFactory.setPort(Integer.parseInt(properties.getProperty("RabbitMQ.port")));
    }

    public static Connection getConnection() {
        try {
            return connectionFactory.newConnection();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
        throw new RuntimeException();
    }
}
