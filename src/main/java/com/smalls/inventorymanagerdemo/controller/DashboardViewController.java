package com.smalls.inventorymanagerdemo.controller;

import com.smalls.inventorymanagerdemo.model.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.*;
import java.util.function.UnaryOperator;

public class DashboardViewController implements Initializable {

    @FXML
    private TextField partSearchTextfield;

    @FXML
    private TextField productSearchTextfield;

    @FXML
    private TableView<Part> partsTable;

    @FXML
    private TableColumn<Part, Integer> partIdColumn;

    @FXML
    private TableColumn<Part, String> partTypeColumn;

    @FXML
    private TableColumn<Part, String> partNameColumn;

    @FXML
    private TableColumn<Part, Double> partPriceColumn;

    @FXML
    private TableColumn<Part, Integer> partStockColumn;

    @FXML
    private TableColumn<Part, Integer> partMinColumn;

    @FXML
    private TableColumn<Part, Integer> partMaxColumn;

    @FXML
    private TableView<Product> productTable;

    @FXML
    private TableColumn<Product, Integer> productIdColumn;

    @FXML
    private TableColumn<Product, String> productNameColumn;

    @FXML
    private TableColumn<Product, Double> productPriceColumn;

    @FXML
    private TableColumn<Product, Integer> productStockColumn;

    @FXML
    private TableColumn<Product, Integer> productMinColumn;

    @FXML
    private TableColumn<Product, Integer> productMaxColumn;

    private NumberFormat currencyFormat;

    private Stage stage;

    private ObservableList<Part> parts;

    private ObservableList<Product> products;

    public final Comparator<Part> comparePartsById = Comparator.comparingInt(Part::getId);

    public final Comparator<Product> compareProductsById = Comparator.comparingInt(Product::getId);

    private final UnaryOperator<TextFormatter.Change> textLengthFilterOperator = change -> {
        String newText = change.getControlNewText();
        if (newText.length() > 25) {
            return null;
        }
        return change;
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        stage = new Stage();
        initPartsTable();
        initProductTable();
        partSearchTextfield.setTextFormatter(
                new TextFormatter<>(textLengthFilterOperator)
        );
        productSearchTextfield.setTextFormatter(
                new TextFormatter<>(textLengthFilterOperator)
        );
        currencyFormat = NumberFormat.getCurrencyInstance();
    }

    @FXML
    private void onPartSearch() {
        String searchString = partSearchTextfield.getText().trim();
        if (searchString.isEmpty()) {
            partsTable.setItems(parts);
            return;
        }

        partsTable.setPlaceholder(new Text("Part not found"));
        ObservableList<Part> parts = FXCollections.observableArrayList();

        try {
            //if searchString is a number
            //then get part by id
            int id = Integer.parseInt(searchString);
            Part p = Inventory.getPartById(id);
            if (p != null) {
                parts.add(p);
            }
            partsTable.setItems(parts);
            partsTable.getSelectionModel().select(p);
        } catch (NumberFormatException e) {
            //searchString must be a string
            //search parts by name
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
    private void onProductSearch() {
        String searchString = productSearchTextfield.getText().trim();
        if (searchString.isEmpty()) {
            return;
        }

        productTable.setPlaceholder(new Text("Product not found"));
        ObservableList<Product> products = FXCollections.observableArrayList();

        try {
            int id = Integer.parseInt(searchString);
            Product p = Inventory.getProductById(id);
            if (p != null) {
                products.add(p);
            }
            productTable.setItems(products);
            productTable.getSelectionModel().select(p);
        } catch (NumberFormatException e) {
            ObservableMap<Integer, Product> productMap = Inventory.searchProducts(searchString);
            if (!productMap.isEmpty()) {
                products.addAll(productMap.values());
                productTable.setItems(products);
                if (products.size() == 1) {
                    productTable.getSelectionModel().select(products.getFirst());
                }
            }
        }
    }

    @FXML
    private void onNewPart() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(
                "/com/smalls/inventorymanagerdemo/part-view.fxml")
        );
        Parent root = loader.load();
        PartViewController controller = loader.getController();
        controller.setPart(null);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setMinWidth(1000.0);
        stage.setMinHeight(635.0);
        stage.show();
    }

    @FXML
    private void onModifyPart() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(
                "/com/smalls/inventorymanagerdemo/part-view.fxml")
        );
        Parent root = loader.load();
        PartViewController controller = loader.getController();
        Part p = partsTable.getSelectionModel().getSelectedItem();
        if (p == null) {
            new Alert(Alert.AlertType.ERROR,
                    "Please select a part")
                    .showAndWait();
            return;
        }
        controller.setPart(p);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setMinWidth(1000.0);
        stage.setMinHeight(635.0);
        stage.show();
    }

    @FXML
    private void onDeletePart() {
        Part p = partsTable.getSelectionModel().getSelectedItem();
        if (p == null) {
            new Alert(Alert.AlertType.ERROR,
                    "Please select a part")
                    .showAndWait();
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you would like to delete " +
                        p.getName() + "?"
        );
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Inventory.removePart(p.getId());
        }
    }

    @FXML
    private void onNewProduct() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(
                "/com/smalls/inventorymanagerdemo/product-view.fxml")
        );
        Parent root = loader.load();
        ProductViewController controller = loader.getController();
        controller.setProduct(null);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setMinWidth(1250.0);
        stage.setMinHeight(850.0);
        stage.show();
    }

    @FXML
    private void onModifyProduct() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(
                "/com/smalls/inventorymanagerdemo/product-view.fxml")
        );
        Parent root = loader.load();
        ProductViewController controller = loader.getController();
        Product p = productTable.getSelectionModel().getSelectedItem();
        if (p == null) {
            new Alert(Alert.AlertType.ERROR,
                    "Please select a product")
                    .showAndWait();
            return;
        }
        controller.setProduct(p);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setMinWidth(1250.0);
        stage.setMinHeight(850.0);
        stage.show();
    }

    @FXML
    private void onDeleteProduct() {
        Product p = productTable.getSelectionModel().getSelectedItem();
        if (p == null) {
            new Alert(Alert.AlertType.ERROR,
                    "Please select a product")
                    .showAndWait();
            return;
        }
        if (!p.getAssociatedParts().isEmpty()) {
            new Alert(Alert.AlertType.ERROR,
                    "Whoops! Cannot delete a product that has associated parts")
                    .showAndWait();
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you would like to delete " + p.getName() + "?"
        );
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Inventory.removeProduct(p.getId());
        }
    }

    @FXML
    private void onClose(ActionEvent event) {
        Alert confirm = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Are you sure you would like to close the application?"
        );
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
        }
    }

    private void initPartsTable() {
        ObservableMap<Integer, Part> partsMap = Inventory.getAllParts();
        parts = FXCollections.observableArrayList(partsMap.values());
        partsMap.addListener((MapChangeListener<Integer, ? super Part>) change -> {
            if (change.wasAdded()) {
                Part update = change.getValueAdded();
                parts.sort(comparePartsById);
                int index = Collections.binarySearch(parts, update, comparePartsById);
                if (index >= 0) {
                    parts.set(index, update);
                    return;
                }
                parts.add(update);
            } else {
                parts.remove(change.getValueRemoved());
            }
        });
        partsTable.setItems(parts);
        partsTable.setPlaceholder(new Text("there are no parts"));

        partIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        partNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        partPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        partStockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        partMinColumn.setCellValueFactory(new PropertyValueFactory<>("min"));
        partMaxColumn.setCellValueFactory(new PropertyValueFactory<>("max"));
        partTypeColumn.setCellValueFactory(c -> {
            Part part = c.getValue();
            if (part instanceof InHouse) {
                return new SimpleStringProperty("InHouse");
            } else if (part instanceof Outsourced) {
                return new SimpleStringProperty("Outsourced");
            } else {
                return new SimpleStringProperty("Dunno what this part is");
            }
        });

        partPriceColumn.setCellFactory(c -> new TableCell<Part, Double>() {
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

    private void initProductTable() {
        ObservableMap<Integer, Product> productsMap = Inventory.getAllProducts();
        products = FXCollections.observableArrayList(productsMap.values());
        productsMap.addListener((MapChangeListener<Integer, ? super Product>) change -> {
            if (change.wasAdded()) {
                Product update = change.getValueAdded();
                products.sort(compareProductsById);
                int index = Collections.binarySearch(products, update, compareProductsById);
                if (index >= 0) {
                    products.set(index, update);
                    return;
                }
                products.add(update);
            } else {
                products.remove(change.getValueRemoved());
            }
        });
        productTable.setItems(products);
        productTable.setPlaceholder(new Text("there are no products"));

        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        productPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        productStockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        productMinColumn.setCellValueFactory(new PropertyValueFactory<>("min"));
        productMaxColumn.setCellValueFactory(new PropertyValueFactory<>("max"));

        productPriceColumn.setCellFactory(cell -> new TableCell<Product, Double>() {
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
}