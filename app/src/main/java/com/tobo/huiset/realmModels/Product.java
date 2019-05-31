package com.tobo.huiset.realmModels;

import io.realm.RealmObject;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class Product extends RealmObject {

    public static final int STANDARD_PRICE_BEER = 44;
    public static final int STANDARD_PRICE_CRATE = -1050;

    public static final String ID_CRATE = "STANDARD_ID_CRATE";
    public static final String ID_BEER = "STANDARD_ID_BEER";


    private String id;
    private String name;
    private int price;

    private boolean builtIn;

    public Product() {}


    static public Product createBuiltInProduct(String id, String name, int price){
        Product item = new Product();
        item.id = id;
        item.name = name;
        item.price = price;
        item.builtIn = true;
        return item;
    }

    static public Product create(String name, int price) {
        Product item = new Product();
        item.name = name;
        item.price = price;
        item.builtIn = false;
        return item;
    }


    public String getName() {
        return name;
    }
}
