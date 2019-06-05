package com.tobo.huiset.realmModels;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import java.util.UUID;

public class Product extends RealmObject {

    public static final int STANDARD_PRICE_BEER = 44;
    public static final int STANDARD_PRICE_CRATE = -1050;

    @PrimaryKey
    private String id = UUID.randomUUID().toString();
    private String name;
    private int price;
    private boolean show;

    public Product() {}


    static public Product create(String name, int price, boolean show) {
        Product item = new Product();
        item.name = name;
        item.price = price;
        item.show = show;

        return item;
    }


    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getId() {
        return this.id;
    }
}
