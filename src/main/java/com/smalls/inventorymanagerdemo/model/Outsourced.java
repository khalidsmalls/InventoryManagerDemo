package com.smalls.inventorymanagerdemo.model;

public class Outsourced extends Part {

    private final String companyName;

    public Outsourced(
            int id,
            String name,
            double price,
            int stock,
            int min,
            int max,
            String companyName
    ) {
        super(id, name, price, stock, min, max);
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
    }
}
