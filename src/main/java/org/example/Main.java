package org.example;

import org.example.entity.*;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        createExampleOffers();

        Labourer labourer = new Labourer("Kowalski", "Jan");
        labourer.addQualification(Qualification.Type.OHS_TRAINING, LocalDate.now().plusMonths(1));
        labourer.makeForeman(true);

        Labourer foreman = new Labourer("Tomowski", "Tomasz");
        foreman.addQualification(Qualification.Type.OHS_TRAINING, LocalDate.now().plusMonths(1));
        foreman.makeForeman(true);

        ConstructionSite constructionSite = new ConstructionSite("ul. Piaszczysta 3");
        Warehouse warehouse = new Warehouse("ul. Lipowa 2");

        Database.openSession();

        Database.persist(labourer, foreman, constructionSite, warehouse);

        System.out.println("assign test");
        LabourerOnWorksite.assign(labourer, constructionSite, LocalDate.now(), 8);
        ForemanAtConstruction.assign(foreman, constructionSite, LocalDate.now(), 8);
        LabourerOnWorksite.assign(labourer, constructionSite, LocalDate.now(), 8);

        Database.closeSession();


        Database.openSession();
        System.out.println("construction sites:");
        for (ConstructionSite s : ConstructionSite.getConstructionSites()) {
            System.out.println(s.getAddress());
        }
        Database.closeSession();

        Database.openSession();
        App.show();
        Database.closeSession();
    }

    private static void createExampleOffers() {
        Client client1 = new Client("Javex", "+48 500 500 500", "example@javex.com");
        Client client2 = new Client("Tompol", "+48 600 600 600", "example@tompol.com");
        Database.persist(client1);
        Database.persist(client2);
        Offer o1 = Offer.createOffer(
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
        Offer o2 = Offer.createOffer(
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
        o2.addComment("Jakiś komentarz z zażaleniem");
        Offer o3 = Offer.createOffer(
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
        o3.accept();
        Offer o4 = Offer.createOffer(
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
        o4.setInvoice("012023001");
        Database.mergeAndPersist(client1, o4, o2, o1);
        Database.mergeAndPersist(client2, o3);
    }
}