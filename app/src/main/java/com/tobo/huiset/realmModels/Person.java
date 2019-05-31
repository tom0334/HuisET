package com.tobo.huiset.realmModels;

import io.realm.RealmObject;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public class Person extends RealmObject {

    private String id = UUID.randomUUID().toString();
    private int balance = -13;
    private String name;
    private String color;

    public Person() {}

    static public Person create(String name, String color) {
        Person p = new Person();
        p.name = name;
        p.color = color;

        return p;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public int getBalance() {
        return balance;
    }

    public String getBalanceAsString() {
        String signed = (balance < 0) ? "-" : "";
        String euros = Integer.toString(balance / 100);
        int abscents = Math.abs(balance % 100);
        String cents = (abscents < 10) ? "0" : "";
        cents += Integer.toString(abscents);
        return "€" +signed + euros + "," + cents;
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
