package com.smalls.inventorymanagerdemo.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class Product {
    private final int id;

    private String name;

    private double price;

    private int stock;

    /**
     * product minimun stock
     */
    private int min;

    /**
     * product maximum stock
     */
    private int max;

    ObservableMap<Integer, Part> associatedParts;

    public Product(
            int id,
            String name,
            double price,
            int stock,
            int min,
            int max
    ) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.min = min;
        this.max = max;
        associatedParts = FXCollections.observableHashMap();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public void addAssociatedPart(Part part) {
        associatedParts.put(part.getId(), part);
    }

    public void addAssociatedParts(ObservableList<Part> parts) {

        ObservableMap<Integer, Part> partsMap = FXCollections.observableHashMap();

        for (Part p : parts) {
           partsMap.put(p.getId(), p);
        }

        associatedParts.putAll(partsMap);
    }

    public void removeAssociatedPart(Part p) {
       associatedParts.remove(p.getId(), p);
    }

    public void removeAssociatedParts(ObservableList<Part> parts) {

        for (Part p : parts) {
            associatedParts.remove(p.getId(), p);
        }
    }

    public ObservableMap<Integer, Part> getAssociatedParts() {
        return associatedParts;
    }
}
