module com.smalls.inventorymanagerdemo {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.smalls.inventorymanagerdemo.model to javafx.base;
    opens com.smalls.inventorymanagerdemo to javafx.fxml;
    exports com.smalls.inventorymanagerdemo;
    exports com.smalls.inventorymanagerdemo.controller;
    exports com.smalls.inventorymanagerdemo.model;
    opens com.smalls.inventorymanagerdemo.controller to javafx.fxml;
}