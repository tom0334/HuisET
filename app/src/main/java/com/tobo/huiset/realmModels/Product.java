package com.tobo.huiset.realmModels;

import io.realm.RealmObject;
import java.util.UUID;

public class Product extends RealmObject {

    public static final int STANDARD_PRICE_BEER = 44;
    public static final int STANDARD_PRICE_CRATE = -1050;

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


    public String getName() {
        return name;
    }
}
