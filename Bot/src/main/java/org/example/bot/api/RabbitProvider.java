package org.example.bot.api;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.bot.bot.Bot;
import org.example.bot.config.RabbitMQConfig;
import org.example.bot.entity.MessageInDB;
import org.example.bot.util.JSONConverter;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class RabbitProvider {
    private static final Logger log = LogManager.getLogger(RabbitProvider.class);
    private static final String FILE_PATH = System.getProperty("user.home") + "/file/";
    private static final String QUEUE_BOT_MESSAGE_NAME = "BotMessageQueue";
    private static final String QUEUE_FORM_HISTORY_TO_PDF = "FormHistoryQueue";
    private static final String QUEUE_GET_HISTORY_TO_PDF = "GetHistoryQueue";
    private static final String QUEUE_FULLTEXT_SEARCH = "GetSearch";
    private static Connection connection;
    private static Channel channel;
    private final Bot bot;

    public RabbitProvider(Bot bot) throws IOException {
        this.bot = bot;
        connection = RabbitMQConfig.getConnection();
        channel = connection.createChannel();
        receiveMessage(QUEUE_FORM_HISTORY_TO_PDF);
        receiveMessage(QUEUE_BOT_MESSAGE_NAME);
        receiveMessage(QUEUE_GET_HISTORY_TO_PDF);
        receiveMessage(QUEUE_FULLTEXT_SEARCH);
    }

    public void sendMessage(String message, String queueName) throws IOException {
        try {
            channel.queueDeclare(QUEUE_BOT_MESSAGE_NAME, false, false, false, null);
            channel.basicPublish("", queueName, null, message.getBytes(StandardCharsets.UTF_8));
            log.debug("[x] Sent json");
        } catch (IOException e) {
            log.error(e);
        }
    }

    public void receiveMessage(String queueName) throws IOException {
        channel.queueDeclare(queueName, false, false, false, null);
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            log.debug(" [x] Received message in " + queueName + ": " + message);
            switch (queueName) {
                case QUEUE_BOT_MESSAGE_NAME:
                    try {
                        bot.getMessage(JSONConverter.jsonToUpdate(message));
                    } catch (TelegramApiException | TimeoutException e) {
                        log.error(e);
                    }
                    break;
                case QUEUE_FORM_HISTORY_TO_PDF:
                    String[] strings = message.split("_");
                    bot.formHistory(message, strings[1]);
                    break;
                case QUEUE_GET_HISTORY_TO_PDF:
                    File file = new File(FILE_PATH + message + ".pdf");
                    AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                            .Builder()
                            .correlationId(delivery.getProperties().getCorrelationId())
                            .build();
                    if (file.exists()) {
                        channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, file.getAbsolutePath().getBytes(StandardCharsets.UTF_8));
                    } else {
                        channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, "File not ready".getBytes());
                    }
                    break;
                case QUEUE_FULLTEXT_SEARCH:
                    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                         ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream)) {
                        String[] strings1 = message.split(" _ ");
                        AMQP.BasicProperties reply = new AMQP.BasicProperties
                                .Builder()
                                .correlationId(delivery.getProperties().getCorrelationId())
                                .build();
                        List<MessageInDB> messageInDBList = bot.searchByText(strings1[0], strings1[1]);
                        outputStream.writeObject(messageInDBList);
                        channel.basicPublish("", delivery.getProperties().getReplyTo(), reply, byteArrayOutputStream.toByteArray());
                    }
                    break;

            }
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });
    }

    public void disconnect() {
        try {
            channel.close();
            connection.close();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
