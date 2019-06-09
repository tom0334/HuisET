package com.tobo.huiset.realmModels;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import java.util.UUID;

public class Product extends RealmObject {

    public static final int STANDARD_PRICE_BEER = 44;
    public static final int STANDARD_PRICE_CRATE = 1050;

    @PrimaryKey
    private String id = UUID.randomUUID().toString();
    private String name;
    private int price;
    private boolean show;
    private boolean selected = false;
    //TODO: implement deleted functionality
    private boolean deleted = false;

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

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public boolean getShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
