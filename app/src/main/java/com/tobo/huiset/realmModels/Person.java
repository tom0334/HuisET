package com.tobo.huiset.realmModels;

import io.realm.Realm;
import io.realm.RealmObject;
import java.util.UUID;

import io.realm.annotations.PrimaryKey;
import org.jetbrains.annotations.NotNull;

public class Person extends RealmObject {

    @PrimaryKey
    private String id = UUID.randomUUID().toString();

    private int balance = 0;
    private String name;
    private String color;
    private boolean guest;
    private boolean show;

    private boolean selectedInHistoryView;

    public Person() {}

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


    public void addTransaction(Transaction t, Realm realm){
        int price =  t.getPrice();
        if(t.isBuy()){
            this.balance += price;
        }else{
            this.balance -= price;
        }
    }

    public void undoTransaction(Transaction t, Realm realm) {
        int price =  t.getPrice();
        if(t.isBuy()){
            this.balance -= price;
        }else{
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
}
