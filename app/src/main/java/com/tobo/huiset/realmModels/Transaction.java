package com.tobo.huiset.realmModels;

import io.realm.RealmObject;

import java.util.UUID;

public class Transaction extends RealmObject {

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

    public Product getProduct() {
        return this.product;
    }

    public Person getPerson() {
        return person;
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
            return minutesAgo + " minuten geleden";
        }
        else if (hoursAgo < 24 ){
            return hoursAgo + " uur geleden";
        }
        else if (monthsAgo < 30){
            return monthsAgo + "maanden geleden";
        }else{
            return yearsAgo + " jaar geleden";
        }

    }

}
