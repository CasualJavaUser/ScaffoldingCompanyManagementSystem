package org.example.fragment;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.example.DateInputDialog;
import org.example.NavController;
import org.example.entity.Client;
import org.example.entity.Offer;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Consumer;

public class OfferDetailsFragment extends Fragment {
    private final Text titleText;
    private final Text descriptionText;
    private final Text assemblyStartText;
    private final Text assemblyEndText;
    private final Text disassemblyStartText;
    private final Text disassemblyEndText;
    private final Text assemblyCostText;
    private final Text disassemblyCostText;
    private final Text rentalCostText;
    private final Text totalCostText;
    private final Text statusText;
    private final Text commentTitle;
    private final Text comment;
    private final Text invoice;
    private final HBox controls;
    private final Button backButton;

    private Client client;
    private Offer offer;

    public OfferDetailsFragment() throws IOException {
        super("views/offer_details_view.fxml");

        titleText = (Text) root.lookup("#title");
        descriptionText = (Text) root.lookup("#description");
        assemblyStartText = (Text) root.lookup("#assemblyStart");
        assemblyEndText = (Text) root.lookup("#assemblyEnd");
        disassemblyStartText = (Text) root.lookup("#disassemblyStart");
        disassemblyEndText = (Text) root.lookup("#disassemblyEnd");
        assemblyCostText = (Text) root.lookup("#assemblyCost");
        disassemblyCostText = (Text) root.lookup("#disassemblyCost");
        rentalCostText = (Text) root.lookup("#rentalCost");
        totalCostText = (Text) root.lookup("#totalCost");
        statusText = (Text) root.lookup("#status");
        commentTitle = (Text) root.lookup("#commentTitle");
        comment = (Text) root.lookup("#comment");
        invoice = (Text) root.lookup("#invoice");
        controls = (HBox) root.lookup("#controls");
        backButton = (Button) root.lookup("#backButton");
    }

    @Override
    public void show(Scene scene, Object... args) {
        client = (Client) args[0];
        offer = (Offer) args[1];
        titleText.setText(offer.getTitle());
        descriptionText.setText(offer.getDescription());
        assemblyStartText.setText(offer.getAssemblyStartDateString());
        assemblyEndText.setText(offer.getAssemblyEndDateString());
        disassemblyStartText.setText(offer.getDisassemblyStartDateString());
        disassemblyEndText.setText(offer.getDisassemblyEndDateString());
        assemblyCostText.setText(offer.getAssemblyCost() + "");
        disassemblyCostText.setText(offer.getDisassemblyCost() + "");
        rentalCostText.setText("Rental cost (per day): " + offer.getRentalCost() + " PLN");
        totalCostText.setText("Total cost: " + offer.getTotalCost() + " PLN");
        statusText.setText("Status: " + offer.getStatus().name());

        backButton.setOnMouseClicked(event -> NavController.navigate(OfferListFragment.class, client));

        controls.getChildren().clear();
        commentTitle.setManaged(false);
        commentTitle.setVisible(false);
        comment.setManaged(false);
        comment.setVisible(false);
        invoice.setManaged(false);
        invoice.setVisible(false);

        switch (offer.getStatus()) {
            case CREATED, REVISED -> {
                Button acceptButton = new Button("Accept");
                acceptButton.setStyle("-fx-background-color: lightgreen; -fx-font-size:18;");
                acceptButton.setOnMouseClicked(event -> showAlert("Accept offer?", () -> offer.accept()));

                Button commentButton = new Button("Comment");
                commentButton.setStyle("-fx-background-color: lightyellow; -fx-font-size:18;");
                commentButton.setOnMouseClicked(event -> showTextDialog("Add Comment", "Enter comment", comment -> offer.addComment(comment)));

                Button rejectButton = new Button("Reject");
                rejectButton.setStyle("-fx-background-color: pink; -fx-font-size:18;");
                rejectButton.setOnMouseClicked(event -> showAlert("Reject offer?", () -> offer.reject()));

                controls.getChildren().addAll(acceptButton, commentButton, rejectButton);
            }
            case COMMENTED -> {
                Button reviseButton = new Button("Revise");
                reviseButton.setStyle("-fx-font-size:18;");
                reviseButton.setOnMouseClicked(event -> NavController.navigate(OfferEditFragment.class, client, offer));
                controls.getChildren().addAll(reviseButton);
                commentTitle.setManaged(true);
                commentTitle.setVisible(true);
                comment.setManaged(true);
                comment.setVisible(true);
                comment.setText(offer.getComment());
            }
            case ACTIVE -> {
                Button changeDateButton = new Button("Change Disassembly Date");
                changeDateButton.setStyle("-fx-font-size:18;");
                changeDateButton.setOnMouseClicked(event -> showDisassemblyDateDialog());
                controls.getChildren().addAll(changeDateButton);
            }
            case UNPAID -> {
                Button setInvoiceButton = new Button("Set Invoice Number");
                setInvoiceButton.setStyle("-fx-font-size:18;");
                setInvoiceButton.setOnMouseClicked(event -> showTextDialog("Set Invoice", "Enter invoice number", inv -> offer.setInvoice(inv)));
                controls.getChildren().addAll(setInvoiceButton);
            }
            case CLOSED -> {
                invoice.setManaged(true);
                invoice.setVisible(true);
                invoice.setText("Invoice Number: " + offer.getInvoiceNumber());
            }
            default -> {}
        }
        scene.setRoot(root);
    }

    private void showAlert(String header, Runnable onOk) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setHeaderText(header);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            onOk.run();
            NavController.navigate(OfferListFragment.class, client);
        }
    }

    private void showTextDialog(String title, String header, Consumer<String> onOk) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().isEmpty()) {
            onOk.accept(result.get());
            NavController.navigate(OfferListFragment.class, client);
        }
    }

    private void showDisassemblyDateDialog() {
        DateInputDialog dialog = new DateInputDialog();
        dialog.setTitle("Change Disassembly Date");
        dialog.setHeaderText("Choose new disassembly date");
        Optional<LocalDate> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (offer.isDisassemblyDateValid(result.get())) {
                offer.moveDisassemblyDate(result.get());
                NavController.navigate(OfferListFragment.class, client);
            }
            else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Invalid disassembly date");
                alert.setHeaderText("The disassembly date must be later than\nthe assembly end date (" + offer.getAssemblyEndDateString() + ").");
                alert.show();
            }
        }
    }
}
