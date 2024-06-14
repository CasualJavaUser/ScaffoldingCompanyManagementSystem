package org.example;

import com.sun.javafx.scene.control.skin.resources.ControlResources;
import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.time.LocalDate;

public class DateInputDialog extends Dialog<LocalDate> {
    private final GridPane grid;
    private final DatePicker datePicker;

    public DateInputDialog() {
        DialogPane dialogPane = this.getDialogPane();
        datePicker = new DatePicker();
        datePicker.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(this.datePicker, Priority.ALWAYS);
        GridPane.setFillWidth(this.datePicker, true);
        grid = new GridPane();
        grid.setHgap(10.0);
        grid.setMaxWidth(Double.MAX_VALUE);
        grid.setAlignment(Pos.CENTER_LEFT);
        dialogPane.contentTextProperty().addListener((var1x) -> {
            updateGrid();
        });
        dialogPane.getStyleClass().add("text-input-dialog");
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        updateGrid();
        setResultConverter((buttonType) -> {
            ButtonBar.ButtonData buttonData = buttonType == null ? null : buttonType.getButtonData();
            return buttonData == ButtonData.OK_DONE ? this.datePicker.getValue() : null;
        });
    }

    private void updateGrid() {
        this.grid.getChildren().clear();
        this.grid.add(this.datePicker, 1, 0);
        this.getDialogPane().setContent(this.grid);
        Platform.runLater(this.datePicker::requestFocus);
    }
}
