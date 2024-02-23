module com.smalls.inventorymanagerdemo {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.smalls.inventorymanagerdemo to javafx.fxml;
    exports com.smalls.inventorymanagerdemo;
    exports com.smalls.inventorymanagerdemo.controller;
    opens com.smalls.inventorymanagerdemo.controller to javafx.fxml;
}