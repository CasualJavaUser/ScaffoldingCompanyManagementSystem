package org.example.fragment;

import javafx.beans.InvalidationListener;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import org.example.Database;
import org.example.NavController;
import org.example.entity.Client;
import org.example.entity.Offer;

import java.io.IOException;
import java.time.LocalDate;

public class NewOfferFragment extends Fragment {
    private final TextField titleTF;
    private final TextArea descriptionTF;
    private final DatePicker assemblyStartPicker;
    private final DatePicker disassemblyStartPicker;
    private final DatePicker assemblyEndPicker;
    private final DatePicker disassemblyEndPicker;
    private final TextField assemblyCostTF;
    private final TextField disassemblyCostTF;
    private final TextField rentalCostTF;
    private final Text totalCostText;
    private final Button saveButton;
    private final Button backButton;

    public NewOfferFragment() throws IOException {
        super("views/new_offer_view.fxml");

        titleTF = (TextField) root.lookup("#title");
        descriptionTF = (TextArea) root.lookup("#description");
        assemblyStartPicker = (DatePicker) root.lookup("#assemblyStart");
        disassemblyStartPicker = (DatePicker) root.lookup("#disassemblyStart");
        assemblyEndPicker = (DatePicker) root.lookup("#assemblyEnd");
        disassemblyEndPicker = (DatePicker) root.lookup("#disassemblyEnd");
        assemblyCostTF = (TextField) root.lookup("#assemblyCost");
        disassemblyCostTF = (TextField) root.lookup("#disassemblyCost");
        rentalCostTF = (TextField) root.lookup("#rentalCost");
        totalCostText = (Text) root.lookup("#totalCost");
        saveButton = (Button) root.lookup("#saveButton");
        backButton = (Button) root.lookup("#backButton");

        InvalidationListener invalidationListener = observable -> {
            float totalCost = 0;
            try {
                float assemblyCost = Float.parseFloat(assemblyCostTF.getText().replace(',', '.'));
                float disassemblyCost = Float.parseFloat(disassemblyCostTF.getText().replace(',', '.'));
                float rentalCost = Float.parseFloat(rentalCostTF.getText().replace(',', '.'));

                if (assemblyEndPicker.getValue() != null && disassemblyStartPicker.getValue() != null) {
                    totalCost = Offer.calculateTotalCost(
                            assemblyEndPicker.getValue(),
                            disassemblyStartPicker.getValue(),
                            assemblyCost,
                            disassemblyCost,
                            rentalCost
                    );
                }
            } catch (NumberFormatException ignored) {
            }
            totalCostText.setText("Total cost = " + totalCost + " PLN");
        };

        assemblyStartPicker.valueProperty().addListener(invalidationListener);
        assemblyEndPicker.valueProperty().addListener(invalidationListener);
        disassemblyStartPicker.valueProperty().addListener(invalidationListener);
        disassemblyEndPicker.valueProperty().addListener(invalidationListener);
        assemblyCostTF.textProperty().addListener(invalidationListener);
        disassemblyCostTF.textProperty().addListener(invalidationListener);
        rentalCostTF.textProperty().addListener(invalidationListener);
    }

    public void show(Scene scene, Object... args) {
        Client client = (Client) args[0];
        saveButton.setOnMouseClicked(event -> {
            String warning = null;

            String title = titleTF.getText();
            String description = descriptionTF.getText();
            LocalDate assemblyStart = assemblyStartPicker.getValue();
            LocalDate assemblyEnd = assemblyEndPicker.getValue();
            LocalDate disassemblyStart = disassemblyStartPicker.getValue();
            LocalDate disassemblyEnd = disassemblyEndPicker.getValue();
            String assemblyCostString = assemblyCostTF.getText().replace(',', '.');
            String disassemblyCostString = disassemblyCostTF.getText().replace(',', '.');
            String rentalCostString = rentalCostTF.getText().replace(',', '.');

            float assemblyCost = 0;
            float disassemblyCost = 0;
            float rentalCost = 0;
            try {assemblyCost = Float.parseFloat(assemblyCostString);}
            catch (NumberFormatException e) {warning = "Invalid assembly cost";}
            try {disassemblyCost = Float.parseFloat(disassemblyCostString);}
            catch (NumberFormatException e) {warning = "Invalid disassembly cost";}
            try {rentalCost = Float.parseFloat(rentalCostString);}
            catch (NumberFormatException e) {warning = "Invalid rental cost";}

            if (warning != null) {
                showAlert(warning);
            } else {
                warning = Offer.isOfferValid(title, description, assemblyStart, assemblyEnd, disassemblyStart, disassemblyEnd, assemblyCost, disassemblyCost, rentalCost, client);
                if (warning != null) {
                    showAlert(warning);
                } else {
                    Offer offer = Offer.createOffer(
                            titleTF.getText(),
                            descriptionTF.getText(),
                            assemblyStartPicker.getValue(),
                            assemblyEndPicker.getValue(),
                            disassemblyStartPicker.getValue(),
                            disassemblyEndPicker.getValue(),
                            Float.parseFloat(assemblyCostTF.getText()),
                            Float.parseFloat(disassemblyCostTF.getText()),
                            Float.parseFloat(rentalCostTF.getText()),
                            client
                    );
                    Database.mergeAndPersist(client, offer);
                    NavController.navigate(OfferListFragment.class, client);
                }
            }
        });

        backButton.setOnMouseClicked(event -> NavController.navigate(OfferListFragment.class, client));

        scene.setRoot(root);
    }

    private void showAlert(String header) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid Data");
        alert.setHeaderText(header);
        alert.show();
    }
}
