package com.tobo.huiset.realmModels;

import io.realm.Realm;
import io.realm.RealmObject;

/**
 * This contains an effect to the balance of an user, additional to the main person of a transaction
 * <p>
 * This is used for custom turfs for example, where the main person's balance increases and other
 * users' balance decreases. (The side effect is the other users)
 */
public class TransactionSideEffect extends RealmObject {
    private String personId;
    private int price;
    //buy means the price will be added to the balance
    private boolean buy;

    public TransactionSideEffect() {
    }

    public static TransactionSideEffect create(String personId, int price, boolean buy) {
        TransactionSideEffect effect = new TransactionSideEffect();
        effect.personId = personId;
        effect.price = price;
        effect.buy = buy;
        return effect;
    }

    public int getBalanceImpact() {
        if (isBuy()) return price;
        else return -1 * price;
    }

    public Person getPerson(Realm realm) {
        return realm.where(Person.class).equalTo("id", this.personId).findFirst();
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
