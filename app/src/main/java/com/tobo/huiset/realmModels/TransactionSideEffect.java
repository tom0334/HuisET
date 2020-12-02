package com.tobo.huiset.realmModels;

import io.realm.RealmObject;

public class TransactionSideEffect extends RealmObject {
    private String personId;
    private int price;
    private boolean buy;

    public TransactionSideEffect() {
    }

    public static TransactionSideEffect create(String personId, int price, boolean buy){
        TransactionSideEffect effect = new TransactionSideEffect();
        effect.personId = personId;
        effect.price = price;
        effect.buy = buy;
        return effect;
    }

    public float getSaldoImpact(){
        if(isBuy()) return  price;
        else return -1 * price;
    }

    public String getPersonId() {
        return personId;
    }

    public int getPrice() {
        return price;
    }

    public boolean isBuy() {
        return buy;
    }
}
