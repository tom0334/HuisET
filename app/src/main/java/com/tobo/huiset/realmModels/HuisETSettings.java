package com.tobo.huiset.realmModels;

import io.realm.RealmObject;

public class HuisETSettings extends RealmObject {

    private Product beerProduct;
    private Product crateProduct;
    private Person huisrekening;

    public HuisETSettings() {
    }

    public static HuisETSettings create(Product beerProduct, Product crateProduct, Person huisrekening) {
        HuisETSettings settings = new HuisETSettings();
        settings.beerProduct = beerProduct;
        settings.crateProduct = crateProduct;
        settings.huisrekening = huisrekening;
        return settings;
    }

    public Product getBeerProduct() {
        return beerProduct;
    }

    public void setBeerProduct(Product beerProduct) {
        this.beerProduct = beerProduct;
    }

    public Product getCrateProduct() {
        return crateProduct;
    }

    public void setCrateProduct(Product crateProduct) {
        this.crateProduct = crateProduct;
    }

    public Person getHuisrekening() {
        return huisrekening;
    }

    public void setHuisrekening(Person huisrekening) {
        this.huisrekening = huisrekening;
    }
}
