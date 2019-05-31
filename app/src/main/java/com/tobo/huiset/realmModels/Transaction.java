package com.tobo.huiset.realmModels;

import java.util.UUID;

public class Transaction {

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

}
