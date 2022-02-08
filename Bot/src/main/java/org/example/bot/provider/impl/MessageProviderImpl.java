package org.example.bot.provider.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.bot.config.HibernateConfig;
import org.example.bot.entity.MessageInDB;
import org.example.bot.provider.MessageProvider;
import org.example.bot.util.Parser;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MessageProviderImpl implements MessageProvider {
    private final static Integer LINE_IN_PAGE = 10;
    private final static Logger log = LogManager.getLogger(MessageProviderImpl.class);

    @Override
    public void create(MessageInDB messageInDB) {
        try (Session session = HibernateConfig.getSession()) {
            Transaction transaction = session.beginTransaction();
            session.save(messageInDB);
            transaction.commit();
        } catch (SQLException e) {
            log.error(e);
        }
    }

    @Override
    public void update(MessageInDB messageInDB) {
        try (Session session = HibernateConfig.getSession()) {
            messageInDB.setId((int) session.createQuery("SELECT M.id  FROM MessageInDB M WHERE M.chatId = :chatId AND M.messageId = :messageId")
                    .setParameter("chatId", messageInDB.getChatId())
                    .setParameter("messageId", messageInDB.getMessageId()).uniqueResult());
            Transaction transaction = session.beginTransaction();
            session.update(messageInDB);
            transaction.commit();
        } catch (SQLException e) {
            log.error(e);
        }
    }

    @Override
    public List<String> getHistory(String userName) {
        List<String> historyStrings = new ArrayList<>();
        try (Session session = HibernateConfig.getSession()) {
            List<Object[]> result = session.createQuery("SELECT M.creationDate, M.updateDate, M.chatId, M.message FROM MessageInDB M WHERE M.username = :username")
                    .setParameter("username", userName).list();
            for (Object[] objects : result) {
                historyStrings.add("Creation date [" + objects[0] + (objects[1] == null ? "" : "], update date [" + objects[1]) + "], chatId [" + objects[2] +
                        "]\nMessage [" + objects[3] + "]\n");
            }
        } catch (SQLException e) {
            log.error(e);
        }
        return historyStrings;
    }

    @Override
    public List<MessageInDB> searchByText(String text, String username, int page) {
        List<MessageInDB> messageInDBList = new ArrayList<>();
        try (Session session = HibernateConfig.getSession()) {
            messageInDBList = (List<MessageInDB>)session.createSQLQuery("SELECT * FROM tgbot.descrypted_messages WHERE MATCH (message) AGAINST (:message) AND username = :username;")
                    .setParameter("message", text).setParameter("username", username).setFirstResult((page-1)*LINE_IN_PAGE).setMaxResults(page*LINE_IN_PAGE).addEntity(MessageInDB.class).list();
        } catch (SQLException e) {
            log.error(e);
        }
        return messageInDBList;
    }
}

