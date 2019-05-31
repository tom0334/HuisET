package com.tobo.huiset.realmModels;

import java.util.UUID;

public class Product {

    private String id = UUID.randomUUID().toString();
    private String name;
    private int price;

    public Product() {}

    static public Product create(String name, int price) {
        Product item = new Product();
        item.name = name;
        item.price = price;

        return item;
    }

}
