package com.tobo.huiset.realmModels;

import io.realm.RealmList;
import io.realm.RealmObject;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public class Person extends RealmObject {

    private String id = UUID.randomUUID().toString();
    private int balance = 0;
    private String name;
    private String color;
    private boolean guest;
    private RealmList<Transaction> transactions = new RealmList<>();

    public Person() {}

    static public Person create(String name, String color, boolean guest) {
        Person p = new Person();
        p.name = name;
        p.color = color;
        p.guest = guest;

        return p;
    }

    @NotNull
    public String getName() {
        return this.name;
    }


    public void addTransaction(Transaction t){
        this.transactions.add(t);
        this.balance -= t.getProduct().getPrice();
    }

    public int getBalance() {
        return balance;
    }

    public String getBalanceColor() {
        if (balance > 0) {
            return "#388e3c";
        } else if (balance == 0) {
            return "#000000";
        } else {
            return "#dd2c00";
        }
    }

}
