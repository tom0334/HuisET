package com.tobo.huiset.realmModels;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import java.util.UUID;

public class Product extends RealmObject {

    public static final int KIND_TURFABLE = 0;
    public static final int KIND_BUYABLE = 1;
    public static final int KIND_BOTH = 2;
    public static final int KIND_NEITHER = 3;

    public static final int SPECIES_BEER = 0;
    public static final int SPECIES_SNACK = 2;
    public static final int SPECIES_OTHER = 3;

    @PrimaryKey
    private String id = UUID.randomUUID().toString();
    private String name;
    private int price;
    private int kind;   // what kind of product it is (Only turfable, only buyable or both)
    private int row;    // geeft row aan
    private int species;    // bier, kratje, snack, etc... (see fields)
    private int buyPerAmount; // how much is in a package of this product? a crate = 24, 6-pack = 6 etc...
    private boolean selected = false;
    private boolean deleted = false;

    public Product() {
    }

    static public Product create(String name, int price, int kind, int row, int species, int buyPerAmount) {
        Product p = new Product();
        p.name = name;
        p.price = price;
        p.kind = kind;
        p.row = row;
        p.species = species;
        p.buyPerAmount = buyPerAmount;
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

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public int getSpecies() {
        return species;
    }

    public void setSpecies(int species) {
        this.species = species;
    }

    public int getBuyPerAmount() {
        return buyPerAmount;
    }

    public void setBuyPerAmount(int buyPerAmount) {
        this.buyPerAmount = buyPerAmount;
    }
}
