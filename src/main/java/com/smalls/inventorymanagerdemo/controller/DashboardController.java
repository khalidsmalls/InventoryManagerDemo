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

public class DashboardController implements Initializable {

    @FXML
    private TextField partSearchTextfield;

    @FXML
    private TextField productSearchTextfield;

    @FXML
    private TableView<Part> partTable;

    @FXML
    private TableView<Product> productTable;

    private NumberFormat currencyFormat;

    private final Stage stage = new Stage();

    private ObservableList<Part> parts;

    public final Comparator<Part> comparePartsById = Comparator.comparingInt(Part::getId);

    private final UnaryOperator<TextFormatter.Change> textLengthFilterOperator = change -> {
        String newText = change.getControlNewText();
        if (newText.length() > 25) {
            return null;
        }
        return change;
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initPartTable();
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
            partTable.setItems(parts);
            return;
        }

        partTable.setPlaceholder(new Text("Part not found"));
        ObservableList<Part> parts = FXCollections.observableArrayList();

        try {
            //if searchString is a number
            //then get part by id
            int id = Integer.parseInt(searchString);
            Part p = Inventory.getPartById(id);
            if (p != null) {
                parts.add(p);
            }
            partTable.setItems(parts);
            partTable.getSelectionModel().select(p);
        } catch (NumberFormatException e) {
            //searchString must be a string
            //search parts by name
            ObservableMap<Integer, Part> partMap = Inventory.searchParts(searchString);
            if (!partMap.isEmpty()) {
                parts.addAll(partMap.values());
                partTable.setItems(parts);
                if (parts.size() == 1) {
                    partTable.getSelectionModel().select(parts.getFirst());
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
                "/com/smalls/inventorymanagerdemo/partForm-view.fxml")
        );
        Parent root = loader.load();
        PartFormController controller = loader.getController();
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
                "/com/smalls/inventorymanagerdemo/partForm-view.fxml")
        );
        Parent root = loader.load();
        PartFormController controller = loader.getController();
        Part p = partTable.getSelectionModel().getSelectedItem();
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
        Part p = partTable.getSelectionModel().getSelectedItem();
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you would like to delete " + p.getName() + "?"
        );
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Inventory.removePart(p.getId());
        }
    }

    @FXML
    private void onNewProduct() {
    }

    @FXML
    private void onModifyProduct() {
    }

    @FXML
    private void onDeleteProduct() {
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

    private void initPartTable() {
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
        partTable.setItems(parts);

        TableColumn<Part, Integer> partIdColumn = new TableColumn<>("Part ID");
        TableColumn<Part, String> partTypeColumn = new TableColumn<>("Type");
        TableColumn<Part, String> partNameColumn = new TableColumn<>("Name");
        TableColumn<Part, Double> partPriceColumn = new TableColumn<>("Price per unit");
        TableColumn<Part, Integer> partStockColumn = new TableColumn<>("Stock");
        TableColumn<Part, Integer> partMinColumn = new TableColumn<>("Min");
        TableColumn<Part, Integer> partMaxColumn = new TableColumn<>("Max");

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

        partTable.getColumns().setAll(
                Arrays.asList(
                        partIdColumn,
                        partTypeColumn,
                        partNameColumn,
                        partPriceColumn,
                        partStockColumn,
                        partMinColumn,
                        partMaxColumn
                )
        );

        partIdColumn.prefWidthProperty().bind(
                partTable.widthProperty().multiply(.1)
        );
        partTypeColumn.prefWidthProperty().bind(
                partTable.widthProperty().multiply(.19)
        );
        partNameColumn.prefWidthProperty().bind(
                partTable.widthProperty().multiply(.19)
        );
        partPriceColumn.prefWidthProperty().bind(
                partTable.widthProperty().multiply(.19)
        );
        partStockColumn.prefWidthProperty().bind(
                partTable.widthProperty().multiply(.11)
        );
        partMinColumn.prefWidthProperty().bind(
                partTable.widthProperty().multiply(.11)
        );
        partMaxColumn.prefWidthProperty().bind(
                partTable.widthProperty().multiply(.11)
        );

        partIdColumn.setResizable(false);
        partTypeColumn.setResizable(false);
        partNameColumn.setResizable(false);
        partPriceColumn.setResizable(false);
        partStockColumn.setResizable(false);
        partMinColumn.setResizable(false);
        partMaxColumn.setResizable(false);

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
        ObservableList<Product> products = FXCollections.observableArrayList();
        products.addAll(Inventory.getAllProducts().values());
        productTable.setItems(products);

        TableColumn<Product, Integer> productIdColumn = new TableColumn<>("Product ID");
        TableColumn<Product, String> productNameColumn = new TableColumn<>("Name");
        TableColumn<Product, Double> productPriceColumn = new TableColumn<>("Price per unit");
        TableColumn<Product, Integer> productStockColumn = new TableColumn<>("Stock");
        TableColumn<Product, Integer> productMinColumn = new TableColumn<>("Min");
        TableColumn<Product, Integer> productMaxColumn = new TableColumn<>("Max");

        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        productPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        productStockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        productMinColumn.setCellValueFactory(new PropertyValueFactory<>("min"));
        productMaxColumn.setCellValueFactory(new PropertyValueFactory<>("max"));

        productTable.getColumns().setAll(
                Arrays.asList(
                        productIdColumn,
                        productNameColumn,
                        productPriceColumn,
                        productStockColumn,
                        productMinColumn,
                        productMaxColumn
                )
        );

        productIdColumn.prefWidthProperty().bind(
                productTable.widthProperty().multiply(0.14)
        );
        productNameColumn.prefWidthProperty().bind(
                productTable.widthProperty().multiply(0.22)
        );
        productPriceColumn.prefWidthProperty().bind(
                productTable.widthProperty().multiply(0.19)
        );
        productStockColumn.prefWidthProperty().bind(
                productTable.widthProperty().multiply(0.15)
        );
        productMinColumn.prefWidthProperty().bind(
                productTable.widthProperty().multiply(0.15)
        );
        productMaxColumn.prefWidthProperty().bind(
                productTable.widthProperty().multiply(0.15)
        );

        productIdColumn.setResizable(false);
        productNameColumn.setResizable(false);
        productPriceColumn.setResizable(false);
        productStockColumn.setResizable(false);
        productMinColumn.setResizable(false);
        productMaxColumn.setResizable(false);

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