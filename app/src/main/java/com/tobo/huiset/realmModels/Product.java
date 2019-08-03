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
    private int row;    // geeft row aan
    private boolean selected = false;
    private boolean deleted = false;
    private boolean isBeer = false;
    private boolean isCrate;

    public Product() {
    }

    static public Product create(String name, int price, boolean show, int row, boolean isBeer, boolean isCrate) {
        Product p = new Product();
        p.name = name;
        p.price = price;
        p.show = show;
        p.row = row;
        p.isBeer = isBeer;
        p.isCrate = isCrate;

        return p;
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

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public boolean isBeer() {
        return isBeer;
    }

    public boolean isCrate() {
        return isCrate;
    }
}
