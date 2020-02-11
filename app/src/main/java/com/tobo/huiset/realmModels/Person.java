package com.tobo.huiset.realmModels;

import com.tobo.huiset.utils.HuisETDB;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
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
    private int row;
    private boolean deleted = false;
    private boolean huisRekening;

    private RealmList<AchievementCompletion> completions = new RealmList<>();

    private boolean selectedInHistoryView;

    public Person() {
    }

    static public Person create(String name, String color, boolean guest, boolean show, int row, boolean isHuisRekening) {
        Person p = new Person();
        p.name = name;
        p.color = color;
        p.guest = guest;
        p.show = show;
        p.row = row;
        p.selectedInHistoryView = false;
        p.huisRekening = isHuisRekening;

        return p;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public void addAchievement(AchievementCompletion a){
        this.completions.add(a);
    }


    public void addTransaction(Transaction t, Boolean huisRekeningEnabled) {
        int price = t.getPrice();
        if (t.isBuy()) {
            this.balance += price;
            if (t.getOtherPersonId() != null) {
                t.getPerson(getRealm(), t.getOtherPersonId()).balance -= price;
            }

            HuisETDB db = new HuisETDB(getRealm());

            this.balance += t.getDepositPrice();
            if (huisRekeningEnabled)
                db.findHuisRekening().balance -= t.getDepositPrice();
            else {
                RealmResults<Person> roommates = db.findAllRoommatesExceptHuisRekening();
                if (roommates != null) {
                    for (Person p : roommates) {
                        p.balance -= t.getDepositPrice() / roommates.size();
                    }
                }
            }
        }
        else {
            this.balance -= price;
        }
    }

    public void undoTransaction(Transaction t, boolean depositEnabled, boolean huisRekeningEnabled) {
        int price = t.getPrice();
        if (t.isBuy()) {
            this.balance -= price;
            if (t.getOtherPersonId() != null) {
                t.getPerson(getRealm(), t.getOtherPersonId()).balance += price;
            }
            if (depositEnabled) {

                HuisETDB db = new HuisETDB(getRealm());

                this.balance -= t.getDepositPrice();
                if (huisRekeningEnabled) {
                    db.findHuisRekening().balance += t.getDepositPrice();
                }
                else {
                    RealmResults<Person> roommates = db.findAllRoommatesExceptHuisRekening();
                    assert roommates != null;
                    for (Person p : roommates) {
                        p.balance += t.getDepositPrice() / roommates.size();
                    }
                }
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
