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
     * by 2 if key is not present in
     * partsMap so that it remains even.
     * otherwise, update part
     *
     * @param part the part to be added or
     *             updated
     */
    public static void updatePart(Part part) {
        if (!parts.containsKey(part.getId())) {
            nextPartId += 2;
        }
        parts.put(part.getId(), part);
    }

    /**
     * add new product and increment id
     * by 2 so that it remains odd
     *
     * @param product the product to be added
     *                of updated
     */
    public static void updateProduct(Product product) {
        if (!products.containsKey(product.getId())) {
            nextProductId += 2;
        }
        products.put(product.getId(), product);
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

    public static void removePart(int id) {
        parts.remove(id);
    }

    public static void removeProduct(int id) {
        products.remove(id);
    }
}
