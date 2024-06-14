package org.example.fragment;

import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import org.example.NavController;
import org.example.entity.Client;
import org.example.entity.Offer;

import java.io.IOException;
import java.util.Comparator;

public class OfferListFragment extends Fragment {
    private final Text titleText;
    private final Button newOfferButton;
    private final Button searchButton;
    private final TextField searchField;
    private final TableView<Offer> table;
    private final ComboBox<Offer.Status> comboBox;

    private Offer.Status currentStatus = Offer.Status.ALL;
    private String searchedPhrase = "";

    @SuppressWarnings("unchecked")
    public OfferListFragment() throws IOException {
        super("views/offer_list_view.fxml");

        titleText = (Text) root.lookup("#pageTitle");
        newOfferButton = (Button) root.lookup("#newOfferButton");
        searchButton = (Button) root.lookup("#searchButton");
        searchField = (TextField) root.lookup("#searchField");
        table = (TableView<Offer>) root.lookup("#offerTable");
        comboBox = (ComboBox<Offer.Status>) root.lookup("#filterOptions");
        Button backButton = (Button) root.lookup("#backButton");

        TableColumn<Offer, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Offer, String> assemblyDateCol = new TableColumn<>("Assembly date");
        assemblyDateCol.setCellValueFactory(new PropertyValueFactory<>("assemblyStartDateString"));

        TableColumn<Offer, String> disassemblyDateCol = new TableColumn<>("Disassembly date");
        disassemblyDateCol.setCellValueFactory(new PropertyValueFactory<>("disassemblyStartDateString"));

        TableColumn<Offer, Float> rentalCostCol = new TableColumn<>("Rental cost");
        rentalCostCol.setCellValueFactory(new PropertyValueFactory<>("rentalCost"));

        TableColumn<Offer, Float> totalCostCol = new TableColumn<>("Total Cost");
        totalCostCol.setCellValueFactory(new PropertyValueFactory<>("totalCost"));

        TableColumn<Offer, Offer.Status> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Offer, String> invoiceNumberCol = new TableColumn<>("Invoice number");
        invoiceNumberCol.setCellValueFactory(new PropertyValueFactory<>("invoiceNumber"));

        //noinspection unchecked
        table.getColumns().addAll(titleCol, assemblyDateCol, disassemblyDateCol, rentalCostCol, totalCostCol, statusCol, invoiceNumberCol);

        comboBox.getItems().addAll(Offer.Status.values());
        comboBox.getSelectionModel().selectFirst();

        backButton.setOnMouseClicked(event -> NavController.navigate(ClientListFragment.class));
    }

    @Override
    public void show(Scene scene, Object... args) {
        Client client = (Client) args[0];
        titleText.setText("Offers for client - " + client.getCompanyName());


        newOfferButton.setOnMouseClicked(event -> NavController.navigate(NewOfferFragment.class, client));

        table.setRowFactory(offerTableView -> {
            TableRow<Offer> row = new TableRow<>() {
                @Override
                protected void updateItem(Offer offer, boolean empty) {
                    super.updateItem(offer, empty);
                    if (!empty) this.setStyle("-fx-background-color: " + offer.getStatus().getColor());
                }
            };

            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    NavController.navigate(OfferDetailsFragment.class, client, row.getItem());
                }
            });

            return row;
        });

        updateTableItems(client);
        scene.setRoot(root);

        table.scrollTo(1);

        comboBox.valueProperty().addListener((observableValue, oldStatus, newStatus) -> {
            currentStatus = newStatus;
            updateTableItems(client);
        });

        searchButton.setOnMouseClicked(event -> {
            searchedPhrase = searchField.getText().toLowerCase();
            updateTableItems(client);
        });
        searchField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                searchedPhrase = searchField.getText().toLowerCase();
                updateTableItems(client);
            }
        });
    }

    private void updateTableItems(Client client) {
        table.setItems(FXCollections.observableArrayList(
                client.getOffersCopy().stream()
                        .filter(offer -> currentStatus.equals(Offer.Status.ALL) || offer.getStatus().equals(currentStatus))
                        .filter(offer -> searchedPhrase.isEmpty() || offer.getTitle().toLowerCase().contains(searchedPhrase))
                        .sorted(Comparator.comparing(Offer::getId).reversed())
                        .toList()
        ));
        table.refresh();
    }
}
