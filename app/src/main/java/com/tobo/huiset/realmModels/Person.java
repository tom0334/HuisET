package com.tobo.huiset.realmModels;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Person extends RealmObject {

    @PrimaryKey
    private final String id = UUID.randomUUID().toString();

    private int balance = 0;
    private String name;
    private String color;
    private boolean guest;
    private boolean show;
    private int row;
    private boolean deleted = false;
    private boolean huisRekening;

    private final RealmList<AchievementCompletion> completions = new RealmList<>();

    private boolean selectedInHistoryView;

    public Person() {
    }

    static public Person create(String name, String color, boolean guest, boolean show, int row, boolean isHuisrekening) {
        Person p = new Person();
        p.name = name;
        p.color = color;
        p.guest = guest;
        p.show = show;
        p.row = row;
        p.selectedInHistoryView = false;
        p.huisRekening = isHuisrekening;

        return p;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addAchievement(AchievementCompletion a) {
        this.completions.add(a);
    }

    public void addTransaction(Transaction t) {
        int price = t.getPrice();
        if (t.isBuy()) {
            this.balance += price;
            if (t.getOtherPersonId() != null) {
                t.getPerson(getRealm(), t.getOtherPersonId()).balance -= price;
            }
        } else {
            this.balance -= price;
        }
    }

    public void undoTransaction(Transaction t) {
        int price = t.getPrice();
        if (t.isBuy()) {
            this.balance -= price;
            if (t.getOtherPersonId() != null) {
                t.getPerson(getRealm(), t.getOtherPersonId()).balance += price;
            }
        } else {
            this.balance += price;
        }
    }

    public int getBalance() {
        return balance;
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

    public boolean getShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public boolean isHuisRekening() {
        return huisRekening;
    }

    public RealmList<AchievementCompletion> getCompletions() {
        return completions;
    }

}
