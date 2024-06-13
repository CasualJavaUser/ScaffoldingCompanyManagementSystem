package org.example;

import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Database {
    private static Session currentSession = null;

    public static void openSession() {
        currentSession = HibernateUtil.getSessionFactory().openSession();
    }

    public static void closeSession() {
        currentSession.close();
    }

    public static<T> Query<T> selectFrom(Class<T> c) {
        if (c == null)
            return null;

        return currentSession.createQuery("from " + c.getSimpleName(), c);
    }

    public static void remove(Object object) {
        if (object == null)
            return;

        Transaction transaction = currentSession.beginTransaction();
        currentSession.remove(object);
        transaction.commit();
    }

    public static void persist(Object object) {
        if (object == null)
            return;

        Transaction transaction = currentSession.beginTransaction();
        currentSession.persist(object);
        transaction.commit();
    }

    public static void persist(Object... objects) {
        if (objects == null)
            return;

        Transaction transaction = currentSession.beginTransaction();
        for (Object object : objects) {
            if (object != null)
                currentSession.persist(object);
        }
        transaction.commit();
    }

    public static void merge(Object object) {
        if (object == null)
            return;

        currentSession.merge(object);
    }
}
