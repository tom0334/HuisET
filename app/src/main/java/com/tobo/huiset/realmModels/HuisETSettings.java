package com.tobo.huiset.realmModels;

import io.realm.RealmObject;

public class HuisETSettings extends RealmObject {

    private Product beerProduct;
    private Product crateProduct;

    public HuisETSettings(){}

    public static HuisETSettings create(Product beerProduct, Product crateProduct ){
        HuisETSettings settings = new HuisETSettings();
        settings.beerProduct = beerProduct;
        settings.crateProduct = crateProduct;
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
}
