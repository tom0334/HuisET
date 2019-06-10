package com.tobo.huiset.realmModels;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class Person extends RealmObject {

    @PrimaryKey
    private String id = UUID.randomUUID().toString();

    private int balance = 0;
    private String name;
    private String color;
    private boolean guest;
    private boolean show;
    //TODO: deleted implementation, can be done if no transactions exist yet
    private boolean deleted = false;

    private boolean selectedInHistoryView;

    public Person() {
    }

    static public Person create(String name, String color, boolean guest, boolean show) {
        Person p = new Person();
        p.name = name;
        p.color = color;
        p.guest = guest;
        p.show = show;
        p.selectedInHistoryView = false;

        return p;
    }

    @NotNull
    public String getName() {
        return this.name;
    }


    public void addTransaction(Transaction t) {
        int price = t.getPrice();
        if (t.isBuy()) {
            this.balance += price;
        } else {
            this.balance -= price;
        }
    }

    public void undoTransaction(Transaction t) {
        int price = t.getPrice();
        if (t.isBuy()) {
            this.balance -= price;
        } else {
            this.balance += price;
        }
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

    public String getId() {
        return this.id;
    }

    public boolean isSelectedInHistoryView() {
        return selectedInHistoryView;
    }

    public void setSelectedInHistoryView(boolean selectedInHistoryView) {
        this.selectedInHistoryView = selectedInHistoryView;
    }

    public boolean isGuest() {
        return guest;
    }

    public void setGuest(boolean guest) {
        this.guest = guest;
    }

    public boolean isShow() {
        return show;
    }

    public String getColor() {
        return color;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
