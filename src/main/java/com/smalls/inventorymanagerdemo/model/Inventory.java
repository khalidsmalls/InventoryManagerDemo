package com.smalls.inventorymanagerdemo.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.Map;

public class Inventory {

    /**
     * initialize part id to a randomly
     * selected even integer
     */
    private static int nextPartId = 1012;

    /**
     * initialize product id to a randomly
     * selected odd integer
     */
    private static int nextProductId = 5033;

    private static final ObservableMap<Integer, Part> parts = FXCollections.observableHashMap();

    private static final ObservableMap<Integer, Product> products = FXCollections.observableHashMap();

    public static ObservableMap<Integer, Part> getAllParts() {
        return parts;
    }

    public static ObservableMap<Integer, Product> getAllProducts() {
        return products;
    }

    /**
     * add new part and increment id
     * by 2 so that it remains even
     *
     * @param p the part to be added
     */
    public static void addPart(Part p) {
        parts.put(p.getId(), p);
        nextPartId += 2;
    }

    /**
     * add new product and increment id
     * by 2 so that it remains odd
     *
     * @param p
     */
    public static void addProduct(Product p) {
        products.put(p.getId(), p);
        nextProductId += 2;
    }

    public static int getNextPartId() {
        return nextPartId;
    }

    public static int getNextProductId() {
        return nextProductId;
    }

    public static Part getPartById(int id) {
        return parts.get(id);
    }

    public static Product getProductById(int id) {
        return products.get(id);
    }

    /**
     * @param searchString the part name string to be matched
     * @return a map of all parts with name attribute
     * containing the searchString
     */
    public static ObservableMap<Integer, Part> searchParts(String searchString) {
        ObservableMap<Integer, Part> matchingParts = FXCollections.observableHashMap();

        for (Map.Entry<Integer, Part> entry : parts.entrySet()) {
            Part p = entry.getValue();
            if (p.getName().toLowerCase().contains(searchString.toLowerCase())) {
                matchingParts.put(p.getId(), p);
            }
        }

        return matchingParts;
    }

    /**
     * @param searchString the product name string to be matched
     * @return a map of all products with name attribute
     * containing the searchString
     */
    public static ObservableMap<Integer, Product> searchProducts(String searchString) {
        ObservableMap<Integer, Product> matchingProducts = FXCollections.observableHashMap();

        for (Map.Entry<Integer, Product> entry : products.entrySet()) {
            Product p = entry.getValue();
            if (p.getName().toLowerCase().contains(searchString.toLowerCase())) {
                matchingProducts.put(p.getId(), p);
            }
        }

        return matchingProducts;
    }

    public static void updatePart(int id, Part part) {
        parts.put(id, part);
    }

    public static void updateProduct(int id, Product product) {
        products.put(id, product);
    }

    public static void removePart(int id) {
        parts.remove(id);
    }

    public static void removeProduct(int id) {
        products.remove(id);
    }
}
