package com.tobo.huiset.realmModels;

import io.realm.RealmObject;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public class Person extends RealmObject {

    private String id = UUID.randomUUID().toString();
    private int balans = 0;
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
        return balans;
    }
}
