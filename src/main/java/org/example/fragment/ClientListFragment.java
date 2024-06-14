package org.example.fragment;

import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import org.example.Database;
import org.example.NavController;
import org.example.entity.Client;

import java.io.IOException;

public class ClientListFragment extends Fragment {
    private final TableView<Client> table;

    private String searchedPhrase = "";

    public ClientListFragment() throws IOException {
        super("views/client_list_view.fxml");


        //noinspection unchecked
        table = (TableView<Client>) root.lookup("#clientTable");
        Button searchButton = (Button) root.lookup("#searchButton");
        TextField searchField = (TextField) root.lookup("#searchField");

        table.setRowFactory(clientTableView -> {
            TableRow<Client> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    NavController.navigate(OfferListFragment.class, row.getItem());
                }
            });

            return row;
        });

        TableColumn<Client, String> titleCol = new TableColumn<>("Company Name");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("companyName"));

        TableColumn<Client, String> assemblyDateCol = new TableColumn<>("Phone");
        assemblyDateCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

        TableColumn<Client, String> disassemblyDateCol = new TableColumn<>("E-mail");
        disassemblyDateCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Client, Integer> rentalCostCol = new TableColumn<>("Offer count");
        rentalCostCol.setCellValueFactory(new PropertyValueFactory<>("offerCount"));

        //noinspection unchecked
        table.getColumns().addAll(titleCol, assemblyDateCol, disassemblyDateCol, rentalCostCol);

        searchButton.setOnMouseClicked(event -> {
            searchedPhrase = searchField.getText().toLowerCase();
            updateTableItems();
        });
        searchField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                searchedPhrase = searchField.getText().toLowerCase();
                updateTableItems();
            }
        });
    }

    @Override
    public void show(Scene scene, Object... args) {
        updateTableItems();
        scene.setRoot(root);
    }

    private void updateTableItems() {
        table.setItems(FXCollections.observableArrayList(
                Database.selectFrom(Client.class).stream()
                        .filter(client -> client.getCompanyName().toLowerCase().contains(searchedPhrase))
                        .toList()
        ));
    }
}
