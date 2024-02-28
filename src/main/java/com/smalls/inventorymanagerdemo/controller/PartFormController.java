package com.smalls.inventorymanagerdemo.controller;

import com.smalls.inventorymanagerdemo.model.InHouse;
import com.smalls.inventorymanagerdemo.model.Inventory;
import com.smalls.inventorymanagerdemo.model.Outsourced;
import com.smalls.inventorymanagerdemo.model.Part;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class NewPartController implements Initializable {

    private final String IN_HOUSE_LABEL_TEXT = "Machine ID";

    @FXML
    private Label partFormLabel;

    @FXML
    private Label dynamicLabel;

    @FXML
    private RadioButton inHouseRadioBtn;

    @FXML
    private RadioButton outsourcedRadioBtn;

    @FXML
    private TextField idTextfield;

    @FXML
    private TextField nameTextfield;

    @FXML
    private TextField stockTextfield;

    @FXML
    private TextField priceTextfield;

    @FXML
    private TextField minTextfield;

    @FXML
    private TextField maxTextfield;

    @FXML
    private TextField dynamicTextfield;

    private ToggleGroup toggleGroup;

    private final UnaryOperator<TextFormatter.Change> intFilter = change -> {
        String newText = change.getControlNewText();
        if (newText.length() > 15) {
            return null;
        }
        if (newText.matches("([1-9][0-9]*)?")) {
            return change;
        }
        return null;
    };

    private final UnaryOperator<TextFormatter.Change> doubleFilter = change -> {
        String newText = change.getControlNewText();
        if (newText.length() > 15) {
            return null;
        }
        if (newText.matches("[\\d]*(\\.((\\d{0,2})?))?")) {
            return change;
        }
        return null;
    };

    private final UnaryOperator<TextFormatter.Change> stringFilter = change -> {
        String newText = change.getControlNewText();
        if (newText.length() > 35) {
            return null;
        }
        if (newText.matches("([a-zA-Z\\- ']*)")) {
            return change;
        }
        return null;
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().addAll(inHouseRadioBtn, outsourcedRadioBtn);
        inHouseRadioBtn.setSelected(true);
        dynamicLabel.setText(IN_HOUSE_LABEL_TEXT);
        idTextfield.setEditable(false);
        String PART_ID_TEXTFIELD_PROMPT = "Auto Gen - Disabled";
        idTextfield.setText(PART_ID_TEXTFIELD_PROMPT);
        partFormLabel.setText("New Part");
        setTextFormatters();

    }

    @FXML
    private void onInHouseRadioClick() {
        dynamicLabel.setText(IN_HOUSE_LABEL_TEXT);
        dynamicTextfield.clear();
        dynamicTextfield.setTextFormatter(
                new TextFormatter<>(intFilter)
        );
    }

    @FXML
    private void onOutsourcedRadioClick() {
        String OUTSOURCED_LABEL_TEXT = "Company Name";
        dynamicLabel.setText(OUTSOURCED_LABEL_TEXT);
        dynamicTextfield.clear();
        dynamicTextfield.setTextFormatter(
                new TextFormatter<>(stringFilter)
        );
    }

    @FXML
    private void onSave(ActionEvent event) {
        Part part = null;
        String name;
        double price;
        int id, stock, min, max;

        if (validateFields()) {
            if (validateStock()) {
                id = Inventory.getNextPartId();
                name = nameTextfield.getText();
                price = Double.parseDouble(priceTextfield.getText());
                stock = Integer.parseInt(stockTextfield.getText());
                min = Integer.parseInt(minTextfield.getText());
                max = Integer.parseInt(maxTextfield.getText());

                if (toggleGroup.getSelectedToggle() == inHouseRadioBtn) {
                    int machineId;
                    try {
                        machineId = Integer.parseInt(dynamicTextfield.getText());
                    } catch (NumberFormatException e) {
                        new Alert(Alert.AlertType.ERROR,
                                "Please enter a valid machine ID"
                        ).showAndWait();
                        return;
                    }
                    part = new InHouse(
                            id,
                            name,
                            price,
                            stock,
                            min,
                            max,
                            machineId
                    );
                }

                if (toggleGroup.getSelectedToggle() == outsourcedRadioBtn) {
                    String companyName = dynamicTextfield.getText();
                    if (companyName.isEmpty()) {
                        new Alert(Alert.AlertType.ERROR,
                                "Please enter a company name"
                        ).showAndWait();
                        return;
                    }
                    part = new Outsourced(
                            id,
                            name,
                            price,
                            stock,
                            min,
                            max,
                            companyName
                    );
                }

                Inventory.addPart(part);
                clearTextfields();
                ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
            } else {
                //validate stock returned false
                new Alert(Alert.AlertType.ERROR,
                        "Stock must be greater than or equal to min and less than or equal to max"
                ).showAndWait();
            }
        } else {
            //validateFields returned false
            new Alert(Alert.AlertType.ERROR,
                    "All fields are required").showAndWait();
        }
    }

    @FXML
    private void onCancel(ActionEvent event) {
        ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
    }

    private boolean validateFields() {
        return !(nameTextfield.getText().isEmpty() ||
                priceTextfield.getText().isEmpty() ||
                stockTextfield.getText().isEmpty() ||
                minTextfield.getText().isEmpty() ||
                maxTextfield.getText().isEmpty());
    }

    private boolean validateStock() {
        int min = Integer.parseInt(minTextfield.getText());
        int max = Integer.parseInt(maxTextfield.getText());
        int stock = Integer.parseInt(stockTextfield.getText());

        return min <= stock && stock <= max;
    }

    private void clearTextfields() {
        nameTextfield.clear();
        priceTextfield.clear();
        stockTextfield.clear();
        minTextfield.clear();
        maxTextfield.clear();
        dynamicTextfield.clear();
    }

    private void setTextFormatters() {
        stockTextfield.setTextFormatter(
                new TextFormatter<>(intFilter)
        );
        minTextfield.setTextFormatter(
                new TextFormatter<>(intFilter)
        );
        maxTextfield.setTextFormatter(
                new TextFormatter<>(intFilter)
        );
        priceTextfield.setTextFormatter(
                new TextFormatter<>(doubleFilter)
        );
        nameTextfield.setTextFormatter(
                new TextFormatter<>(stringFilter)
        );
        dynamicTextfield.setTextFormatter(
                new TextFormatter<>(intFilter)
        );
    }
}
