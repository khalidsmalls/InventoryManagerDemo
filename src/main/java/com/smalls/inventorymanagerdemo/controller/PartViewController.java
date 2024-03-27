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

public class PartViewController implements Initializable {

    @FXML
    private Label partFormLabel;

    @FXML
    private Label typeLabel;

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
    private TextField typeTextfield;

    private ToggleGroup toggleGroup;

    private Part part;

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

    public PartViewController() {}

    public PartViewController(Part p) {
        this.part = p;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().addAll(inHouseRadioBtn, outsourcedRadioBtn);
        idTextfield.setEditable(false);
        setTextFormatters();

        if (part == null) {
            idTextfield.setText("Auto Gen - Disabled");
            typeTextfield.setTextFormatter(new TextFormatter<>(intFilter));
        } else {
            if (part instanceof InHouse) {
                typeTextfield.setTextFormatter(new TextFormatter<>(intFilter));
            } else {
                typeTextfield.setTextFormatter(new TextFormatter<>(stringFilter));
            }
            populatePartData(part);
        }
    }

    public void setPartFormLabelText(String s) {
        partFormLabel.setText(s);
    }

    public void setTypeLabelText(String s) {
        typeLabel.setText(s);
    }

    public void setInHouseRadioBtnSelected() {
        inHouseRadioBtn.setSelected(true);
    }

    public void setOutsourcedRadioBtnSelected() {
        outsourcedRadioBtn.setSelected(true);
    }

    @FXML
    private void onInHouseRadioClick() {
        typeLabel.setText("Machine ID");
        typeTextfield.clear();
        typeTextfield.setTextFormatter(new TextFormatter<>(intFilter));
    }

    @FXML
    private void onOutsourcedRadioClick() {
        typeLabel.setText("Company Name");
        typeTextfield.clear();
        typeTextfield.setTextFormatter(new TextFormatter<>(stringFilter));
    }

    @FXML
    private void onSave(ActionEvent event) {
        String name;
        double price;
        int id, stock, min, max;

        if (validateFields()) {
            name = nameTextfield.getText().trim();
            stock = Integer.parseInt(stockTextfield.getText().trim());
            price = Double.parseDouble(priceTextfield.getText().trim());
            min = Integer.parseInt(minTextfield.getText().trim());
            max = Integer.parseInt(maxTextfield.getText().trim());
        } else {
            new Alert(Alert.AlertType.ERROR,
                    "All fields are required")
                    .showAndWait();
            return;
        }

        if (!validateStock()) {
            new Alert(Alert.AlertType.ERROR,
                    "Stock must be greater than or equal to" +
                            " min and less than or equal to max"
            ).showAndWait();
            return;
        }

        id = (part == null) ? Inventory.getNextPartId() : part.getId();
        Part updatedPart = null; //the part to be saved

        if (toggleGroup.getSelectedToggle() == inHouseRadioBtn) {
            int machineId;
            try {
                machineId = Integer.parseInt(typeTextfield.getText().trim());
            } catch (NumberFormatException e) {
                String msg = "Please enter a valid machine ID";
                new Alert(Alert.AlertType.ERROR, msg).showAndWait();
                return;
            }
            updatedPart = new InHouse(
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
            String companyName = typeTextfield.getText().trim();
            if (companyName.isEmpty()) {
                new Alert(Alert.AlertType.ERROR,
                        "Please enter a company name"
                ).showAndWait();
                return;
            }
            updatedPart = new Outsourced(
                    id,
                    name,
                    price,
                    stock,
                    min,
                    max,
                    companyName
            );
        }

        if (updatedPart != null) {
            Inventory.updatePart(updatedPart);
        }

        clearTextfields();
        ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
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
        int min = Integer.parseInt(minTextfield.getText().trim());
        int max = Integer.parseInt(maxTextfield.getText().trim());
        int stock = Integer.parseInt(stockTextfield.getText().trim());

        return min <= stock && stock <= max;
    }

    private void clearTextfields() {
        nameTextfield.clear();
        priceTextfield.clear();
        stockTextfield.clear();
        minTextfield.clear();
        maxTextfield.clear();
        typeTextfield.clear();
    }

    private void setTextFormatters() {
        stockTextfield.setTextFormatter(new TextFormatter<>(intFilter));
        minTextfield.setTextFormatter(new TextFormatter<>(intFilter));
        maxTextfield.setTextFormatter(new TextFormatter<>(intFilter));
        priceTextfield.setTextFormatter(new TextFormatter<>(doubleFilter));
        nameTextfield.setTextFormatter(new TextFormatter<>(stringFilter));
    }

    private void populatePartData(Part p) {
        idTextfield.setText(String.valueOf(p.getId()));
        nameTextfield.setText(p.getName());
        stockTextfield.setText(String.valueOf(p.getStock()));
        priceTextfield.setText(String.valueOf(p.getPrice()));
        minTextfield.setText(String.valueOf(p.getMin()));
        maxTextfield.setText(String.valueOf(p.getMax()));
        if (p instanceof InHouse) {
            String machineId = String.valueOf(((InHouse) p).getMachineId());
            typeTextfield.setText(machineId);
        } else {
            String companyName = ((Outsourced) p).getCompanyName();
            typeTextfield.setText(companyName);
        }
    }
}
