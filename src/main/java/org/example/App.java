package org.example;

import org.example.entity.*;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;

public class App {
    public static void main(String[] args) {
        Labourer labourer = new Labourer("Kowalski", "Jan");
        labourer.addQualification(Qualification.Type.OHS_TRAINING, LocalDate.now());
        Foreman foreman = new Foreman("Adamowski", "Adam");
        WarehouseManager wm = new WarehouseManager("Tomowski", "Tomo");
        Warehouse warehouse = new Warehouse("ul. Lipowa 2");
        ConstructionSite cs = new ConstructionSite("ul. Piaszczysta 3");
        cs.setForemanToday(foreman);
        LabourerOnWorksite low = warehouse.addLabourer(labourer, LocalDate.now());
        warehouse.setManagerToday(wm);

        //dropTables();
        saveData(labourer, foreman, wm, warehouse, low, cs);

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            foreman.setName("Jakub");
            session.merge(foreman);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }

        printTableData(Labourer.class, Warehouse.class, LabourerOnWorksite.class);

        HibernateUtil.shutdown();
    }

    private static void dropTables() {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            //session.createQuery("from Employee ", Employee.class).list().forEach(session::remove);
            //session.createQuery("select TABLE_NAME from DB_SCAFFOLDING.TABLES where TABLE_TYPE = 'BASE TABLE");
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    private static void saveData(Object... objects) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            for (Object object : objects) {
                session.persist(object);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    private static void printTableData(Class<?>... classes) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            for (Class<?> c : classes) {
                session.createQuery("from " + c.getSimpleName(), c).list().forEach(o -> System.out.println(o + "\n"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}