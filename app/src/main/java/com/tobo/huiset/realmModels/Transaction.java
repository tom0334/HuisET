package com.tobo.huiset.realmModels;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import java.util.UUID;

public class Transaction extends RealmObject {

    @PrimaryKey
    private String id = UUID.randomUUID().toString();

    private long time = System.currentTimeMillis();
    private String personId;
    private String productId;
    private int price;

    private boolean buy;

    public Transaction() {}

    static public Transaction create(Person person, Product product,boolean buy) {
        Transaction t = new Transaction();
        t.personId = person.getId();
        t.productId = product.getId();
        t.buy = buy;
        t.price = product.getPrice();
        return t;
    }

    public String getTimeString(){
        long secondsAgo = (System.currentTimeMillis() - time ) / 1000;
        long minutesAgo = secondsAgo /60;
        long hoursAgo = minutesAgo / 60;
        long daysAgo = hoursAgo / 24;
        //#yolo
        long monthsAgo = daysAgo /30;
        long yearsAgo = monthsAgo / 12;


        if(secondsAgo < 10){
            return "Nu";
        }
        else if(secondsAgo < 60){
            return "Zojuist";
        }
        else if (minutesAgo < 60){
            return minutesAgo + " minuten\ngeleden";
        }
        else if (hoursAgo < 24 ){
            return hoursAgo + " uur\ngeleden";
        }
        else if(daysAgo < 7){
            return daysAgo + " dagen\ngeleden";
        }
        else if (monthsAgo < 30){
            return monthsAgo + " maanden\ngeleden";
        }else{
            return yearsAgo + " jaar\ngeleden";
        }

    }

    public int getPrice() {
        return price;
    }

    public String getPersonId() {
        return personId;
    }

    public String getProductId() {
        return productId;
    }


    public boolean isBuy() {
        return buy;
    }

    public Person getPerson(Realm realm){
        return realm.where(Person.class).equalTo("id", this.personId).findFirst();
    }

    public Product getProduct(Realm realm){
        return realm.where(Product.class).equalTo("id", this.productId).findFirst();
    }

    public String getId() {
        return id;
    }
}
