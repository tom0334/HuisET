package com.tobo.huiset.realmModels;

import io.realm.RealmObject;

public class Person extends RealmObject {

    int balans;
    String name;

    public Person() {

    }

    static public Person create(String name) {
        Person p = new Person();
        p.balans = 0;
        p.name = name;

        return p;
    }
}
