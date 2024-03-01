package com.smalls.inventorymanagerdemo.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class Product {
    private final int id;

    private final String name;

    private final double price;

    private final int stock;

    /**
     * product minimun stock
     */
    private final int min;

    /**
     * product maximum stock
     */
    private final int max;

    ObservableList<Part> associatedParts;

    public Product(
            int id,
            String name,
            double price,
            int stock,
            int min,
            int max,
            ObservableList<Part> associatedParts
    ) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.min = min;
        this.max = max;
        this.associatedParts = associatedParts;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public ObservableList<Part> getAssociatedParts() {
        return associatedParts;
    }
}
