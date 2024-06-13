package org.example;

import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.entity.*;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class App {//} extends Application {
    public static void main(String[] args) {
        //launch();

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

        HibernateUtil.shutdown();
    }

    /*@Override
    public void start(Stage stage) throws Exception {
        //Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("views/new_offer_view.fxml"));
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("views/offer_list_view.fxml"));
        //Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("views/offer_details_view.fxml"));
        Scene scene = new Scene(root, 960, 540);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("style/style.css").toExternalForm());
        stage.setScene(scene);

        initOfferListView(scene);

        stage.show();
    }*/

    public void initOfferListView(Scene scene) {
        TableView<Offer> table = (TableView<Offer>) scene.lookup("#offers");

        table.setRowFactory(offerTableView -> new TableRow<>() {
            @Override
            protected boolean isItemChanged(Offer offer, Offer t1) {
                this.setStyle("-fx-background-color: " + offer.getStatus().getColor());
                return super.isItemChanged(offer, t1);
            }
        });

        TableColumn<Offer, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Offer, LocalDate> assemblyDateCol = new TableColumn<>("Assembly date");
        assemblyDateCol.setCellValueFactory(new PropertyValueFactory<>("assemblyStartDate"));

        TableColumn<Offer, LocalDate> disassemblyDateCol = new TableColumn<>("Disassembly date");
        disassemblyDateCol.setCellValueFactory(new PropertyValueFactory<>("disassemblyStartDate"));

        TableColumn<Offer, Float> rentalCostCol = new TableColumn<>("Rental cost");
        rentalCostCol.setCellValueFactory(new PropertyValueFactory<>("rentalCost"));

        TableColumn<Offer, Float> totalCostCol = new TableColumn<>("Total Cost");
        totalCostCol.setCellValueFactory(new PropertyValueFactory<>("totalCost"));

        TableColumn<Offer, Offer.Status> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Offer, String> invoiceNumberCol = new TableColumn<>("Invoice number");
        invoiceNumberCol.setCellValueFactory(new PropertyValueFactory<>("invoiceNumber"));

        table.getColumns().addAll(titleCol, assemblyDateCol, disassemblyDateCol, rentalCostCol, totalCostCol, statusCol, invoiceNumberCol);

        table.setItems(FXCollections.observableArrayList(exampleOffers()));

        ComboBox<String> comboBox = (ComboBox<String>) scene.lookup("#filters");
        List<String> values = Stream.concat(Stream.of("ALL"), Arrays.stream(Offer.Status.values()).map(Offer.Status::name)).toList();
        comboBox.getItems().addAll(values);
        comboBox.getSelectionModel().selectFirst();
    }

    private static List<Offer> exampleOffers() {
        List<Offer> list = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Offer offer = Offer.createOffer(
                    "tytuł" + i,
                    "opis" + i,
                    LocalDate.now(),
                    LocalDate.now().plusDays(2),
                    LocalDate.now().plusMonths(1),
                    LocalDate.now().plusMonths(2).plusDays(2),
                    100,
                    100,
                    0.05f);
            offer.setStatus(Offer.Status.values()[(int)(Math.random() * Offer.Status.values().length)]);
            list.add(offer);
        }
        return list;
    }

    private static void dropTables() {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.createQuery("from Employee ", Employee.class).list().forEach(session::remove);
            //session.createQuery("select TABLE_NAME from DB_SCAFFOLDING.TABLES where TABLE_TYPE = 'BASE TABLE");
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