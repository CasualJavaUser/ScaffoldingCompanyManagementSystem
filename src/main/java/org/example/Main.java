package org.example;

import org.example.entity.*;

import javax.xml.crypto.Data;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        Database.openSession();
        exampleData();
        Database.closeSession();

        Database.openSession();
        System.out.println("\nclients: ");
        Database.selectFrom(Client.class).stream().forEach(System.out::println);
        System.out.println("\noffers: ");
        Database.selectFrom(Offer.class).stream().forEach(System.out::println);
        System.out.println("\nworksites: ");
        Database.selectFrom(Worksite.class).stream().forEach(System.out::println);
        System.out.println("\nmanagers: ");
        Database.selectFrom(Manager.class).stream().forEach(System.out::println);
        System.out.println("\nlabourers: ");
        Database.selectFrom(Labourer.class).stream().forEach(System.out::println);
        Database.closeSession();

        Database.openSession();
        App.show();
        Database.closeSession();
    }

    private static void exampleData() {
        Client client1 = new Client("Javex", "+48 500 500 500", "example@javex.com");
        Client client2 = new Client("Tompol", "+48 600 600 600", "example@tompol.com");

        Offer offer1 = Offer.createOffer(
                "Lipowa 2",
                "Opis oferty na ulicy Lipowej. Takie tam rusztowanie z jakimiś elementami.",
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(4),
                LocalDate.now().plusMonths(3).plusDays(13),
                LocalDate.now().plusMonths(3).plusDays(15),
                5000,
                5000,
                0.1f,
                client1
        );
        Offer offer2 = Offer.createOffer(
                "Astronomow 3",
                "Jakiś inny opis oferty. Zamontować, rozmontować. Wiadomo o co chodzi.",
                LocalDate.now().plusDays(6),
                LocalDate.now().plusDays(7),
                LocalDate.now().plusMonths(1).plusDays(4),
                LocalDate.now().plusMonths(1).plusDays(5),
                4000,
                4000,
                0.1f,
                client1
        );
        offer2.addComment("Jakiś komentarz z zażaleniem");
        Offer offer3 = Offer.createOffer(
                "Prozna 12",
                "Kolejny opis oferty. Cośtam, cośtam...",
                LocalDate.now().minusDays(10),
                LocalDate.now().minusDays(9),
                LocalDate.now().plusMonths(1).plusDays(1),
                LocalDate.now().plusMonths(1).plusDays(2),
                3000,
                3000,
                0.05f,
                client2
        );
        offer3.accept();
        Offer offer4 = Offer.createOffer(
                "Zelazna 68",
                "Cośtam, cośtam, cośtam, cośtam, cośtam, cośtam...",
                LocalDate.now().minusMonths(2).minusDays(3),
                LocalDate.now().minusMonths(2).minusDays(1),
                LocalDate.now().minusMonths(1).minusDays(3),
                LocalDate.now().minusMonths(1).minusDays(1),
                4500,
                4500,
                0.1f,
                client1
        );
        offer4.setInvoice("012023001");

        Labourer labourer1 = new Labourer("Kijek", "Eugeniusz");
        labourer1.addQualification(Qualification.Type.OHS_TRAINING, LocalDate.now().plusMonths(1));
        labourer1.addQualification(Qualification.Type.SCAFFOLDER, LocalDate.now().plusMonths(1));

        Labourer labourer2 = new Labourer("Kowalski", "Jan");
        labourer2.addQualification(Qualification.Type.OHS_TRAINING, LocalDate.now().plusMonths(1));

        Labourer foreman = new Labourer("Kosowski", "Augustyn");
        foreman.addQualification(Qualification.Type.OHS_TRAINING, LocalDate.now().plusMonths(1));
        foreman.addQualification(Qualification.Type.SCAFFOLDER, LocalDate.now().plusMonths(1));
        foreman.makeForeman(true);

        Labourer warehouseManager = new Labourer("Tomowski", "Tomasz");
        foreman.addQualification(Qualification.Type.OHS_TRAINING, LocalDate.now().plusMonths(1));
        foreman.makeWarehouseManager(true);

        ConstructionSite constructionSite = new ConstructionSite("ul. Piaszczysta 3", true, offer1);

        Warehouse warehouse = new Warehouse("ul. Lipowa 2");

        Manager manager = new Manager("Hetmański", "Benedykt");
        manager.setEmail("ben@domain.com");

        Database.persist(labourer1, labourer2, warehouseManager, foreman, constructionSite, warehouse, client1, client2, offer4, offer3, offer2, offer1);

        ForemanAtConstruction.assign(foreman, constructionSite, LocalDate.now(), 8);
        LabourerOnWorksite.assign(labourer1, constructionSite, LocalDate.now(), 8);

        WarehouseManagerAtWarehouse.assign(warehouseManager, warehouse, LocalDate.now(), 8);
        LabourerOnWorksite.assign(labourer2, warehouse, LocalDate.now(), 8);

        manager.assignToConstructionSite(constructionSite);
    }
}