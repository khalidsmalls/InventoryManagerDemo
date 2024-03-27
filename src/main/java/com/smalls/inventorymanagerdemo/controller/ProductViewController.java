package com.smalls.inventorymanagerdemo.controller;

import com.smalls.inventorymanagerdemo.model.Inventory;
import com.smalls.inventorymanagerdemo.model.Part;
import com.smalls.inventorymanagerdemo.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.text.NumberFormat;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class ProductViewController implements Initializable {

    @FXML
    private Label productFormLabel;

    @FXML
    private TextField idTextfield;

    @FXML
    private TextField nameTextfield;

    @FXML
    private TextField priceTextfield;

    @FXML
    private TextField stockTextfield;

    @FXML
    private TextField minTextfield;

    @FXML
    private TextField maxTextfield;

    @FXML
    private TextField searchTextfield;

    @FXML
    private TableView<Part> partsTable;

    @FXML
    private TableColumn<Part, Integer> partIdColumn;

    @FXML
    private TableColumn<Part, String> partNameColumn;

    @FXML
    private TableColumn<Part, Double> partPriceColumn;

    @FXML
    private TableColumn<Part, Integer> partStockColumn;

    @FXML
    private TableView<Part> assocPartsTable;

    @FXML
    private TableColumn<Part, Integer> assocPartIdColumn;

    @FXML
    private TableColumn<Part, String> assocPartNameColumn;

    @FXML
    private TableColumn<Part, Integer> assocPartStockColumn;

    @FXML
    private TableColumn<Part, Double> assocPartPriceColumn;

    @FXML
    private ObservableList<Part> parts;

    @FXML
    private ObservableList<Part> assocParts;

    private NumberFormat currencyFormat;

    private Product product;

    //input validation
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
        if (newText.matches("([a-zA-Z\\-]*)")) {
            return change;
        }
        return null;
    };

    private final UnaryOperator<TextFormatter.Change> lengthFilter = change -> {
        String newText = change.getControlNewText();
        if (newText.length() > 35) {
            return null;
        }
        return change;
    };

    public ProductViewController() {}

    public ProductViewController(Product p) {
        this.product = p;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idTextfield.setEditable(false);
        currencyFormat = NumberFormat.getCurrencyInstance();
        setTextFormatters();
        initPartsTable();
        initAssocPartsTable();

        if (product == null) {
            idTextfield.setText("Auto Gen- Disabled");
        } else {
            populateProductData(product);
        }
    }

    public void setProductFormLabelText(String s) {
        productFormLabel.setText(s);
    }

    @FXML
    private void onSearch() {
        String searchString = searchTextfield.getText().trim();
        if (searchString.isEmpty()) {
            partsTable.setItems(parts);
            return;
        }

        partsTable.setPlaceholder(new Text("Part not found"));
        ObservableList<Part> parts = FXCollections.observableArrayList();

        try {
            int id = Integer.parseInt(searchString);
            Part p = Inventory.getPartById(id);
            if (p != null) {
                parts.add(p);
            }
            partsTable.setItems(parts);
            partsTable.getSelectionModel().select(p);
        } catch (NumberFormatException e) {
            ObservableMap<Integer, Part> partMap = Inventory.searchParts(searchString);
            if (!partMap.isEmpty()) {
                parts.addAll(partMap.values());
                partsTable.setItems(parts);
                if (parts.size() == 1) {
                    partsTable.getSelectionModel().select(parts.getFirst());
                }
            }
        }
    }

    @FXML
    private void onAddAssocPart() {
        assocParts.addAll(
                partsTable.getSelectionModel().getSelectedItems()
        );
    }

    @FXML
    private void onRemoveAssocPart() {
        assocParts.removeAll(
                assocPartsTable.getSelectionModel().getSelectedItems()
        );
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

        if (product == null) {
            id = Inventory.getNextProductId();
        } else {
            id = product.getId();
        }

        Inventory.updateProduct(
                new Product(
                        id,
                        name,
                        price,
                        stock,
                        min,
                        max,
                        assocPartsTable.getItems()
                )
        );

        clearTextfields();
        ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
    }

    @FXML
    private void onCancel(ActionEvent event) {
        ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
    }

    private void initAssocPartsTable() {
        assocParts = FXCollections.observableArrayList();
        assocPartsTable.setItems(assocParts);
        assocPartsTable.setPlaceholder(new Text("there are no associated parts"));
        assocPartsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        assocPartIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        assocPartNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        assocPartStockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        assocPartPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        assocPartPriceColumn.setCellFactory(cell -> new TableCell<Part, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(currencyFormat.format(price));
                }
            }
        });
    }

    private void initPartsTable() {
        parts = FXCollections.observableArrayList(Inventory.getAllParts().values());
        partsTable.setItems(parts);
        partsTable.setPlaceholder(new Text("there are no parts"));
        partsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        partIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        partNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        partStockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        partPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        partPriceColumn.setCellFactory(cell -> new TableCell<Part, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(currencyFormat.format(price));
                }
            }
        });
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
    }

    private void setTextFormatters() {
        searchTextfield.setTextFormatter(new TextFormatter<>(lengthFilter));
        nameTextfield.setTextFormatter(new TextFormatter<>(stringFilter));
        priceTextfield.setTextFormatter(new TextFormatter<>(doubleFilter));
        stockTextfield.setTextFormatter(new TextFormatter<>(intFilter));
        minTextfield.setTextFormatter(new TextFormatter<>(intFilter));
        maxTextfield.setTextFormatter(new TextFormatter<>(intFilter));
    }

    private void populateProductData(Product p) {
        idTextfield.setText(String.valueOf((p.getId())));
        nameTextfield.setText(p.getName());
        stockTextfield.setText(String.valueOf(p.getStock()));
        priceTextfield.setText(String.valueOf(p.getPrice()));
        minTextfield.setText(String.valueOf(p.getMin()));
        maxTextfield.setText(String.valueOf(p.getMax()));
        assocParts.addAll(product.getAssociatedParts());
    }

}
