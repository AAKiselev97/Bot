package org.example.bot.config;

import org.example.bot.entity.JSONMessageInDB;
import org.example.bot.entity.MessageInDB;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.sql.SQLException;
import java.util.Properties;

public class HibernateConfig {
    private static SessionFactory sessionFactory;

    private HibernateConfig() {
        sessionFactory = new Configuration()
                .setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLInnoDBDialect")
                .setProperty("hibernate.connection.datasource", "java:comp/env/jdbc/test")
                .setProperty("hibernate.order_updates", "true").buildSessionFactory();
    }

    public static Session getSession() throws SQLException {
        return sessionFactory.openSession();
    }

    public static void init(Properties properties) {
        Configuration configuration = new Configuration()
                .setProperties(properties)
                .addAnnotatedClass(JSONMessageInDB.class)
                .addAnnotatedClass(MessageInDB.class);
        sessionFactory = configuration.buildSessionFactory();
    }
}
