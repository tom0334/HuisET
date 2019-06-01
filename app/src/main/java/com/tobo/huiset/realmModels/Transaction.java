package com.tobo.huiset.realmModels;

import io.realm.RealmObject;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class Transaction extends RealmObject {

    private String id = UUID.randomUUID().toString();
    private long time = System.currentTimeMillis();

    private Person person;
    private Product product;

    public Transaction() {}

    static public Transaction create(Person person, Product product) {
        Transaction t = new Transaction();
        t.person = person;
        t.product = product;

        return t;
    }

    public Product getProduct() {
        return this.product;
    }

    public Person getPerson() {
        return person;
    }
}
