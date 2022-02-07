package org.example.bot.provider.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.bot.config.HibernateConfig;
import org.example.bot.entity.JSONMessageInDB;
import org.example.bot.provider.JSONProvider;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class JSONProviderImpl implements JSONProvider {
    private static final Logger log = LogManager.getLogger(JSONProviderImpl.class);

    @Override
    public void create(JSONMessageInDB jsonMessageInDB) {
        try (Session session = HibernateConfig.getSession()) {
            Transaction transaction = session.beginTransaction();
            session.save(jsonMessageInDB);
            transaction.commit();
            log.debug("save json success");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
        }
    }
}
