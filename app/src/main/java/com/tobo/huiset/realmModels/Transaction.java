package com.tobo.huiset.realmModels;

import com.tobo.huiset.utils.ToboTime;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Transaction extends RealmObject {

    @PrimaryKey
    private final String id = UUID.randomUUID().toString();

    private final long time = System.currentTimeMillis();
    private String personId;
    private String productId;
    private int price;
    private float amount;
    private boolean buy;

    private String otherPersonId = null;

    public Transaction() {
    }

    static public Transaction create(Person person, Product product, float amount, boolean buy) {
        Transaction t = new Transaction();
        t.personId = person.getId();
        t.productId = product.getId();
        t.buy = buy;
        t.amount = buy ? product.getBuyPerAmount() * amount : amount;
        int divideBy = buy ? 1 : product.getBuyPerAmount();
        t.price = Math.round((float) product.getPrice() / divideBy * amount);
        return t;
    }

    static public Transaction createTransfer(Person person, Person receiver, int price, Product product) {
        Transaction t = new Transaction();
        t.personId = person.getId();
        t.productId = product.getId();
        t.buy = true;
        t.amount = 1;
        t.price = price;
        t.otherPersonId = receiver.getId();
        return t;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getPersonId() {
        return personId;
    }

    public String getProductId() {
        return productId;
    }

    public float getSaldoImpact() {
        if (isBuy()) return price;
        else return -1 * price;
    }

    public boolean isBuy() {
        return buy;
    }

    public Person getPerson(Realm realm, String id) {
        return realm.where(Person.class).equalTo("id", id).findFirst();
    }

    public Product getProduct() {
        return this.getRealm().where(Product.class).equalTo("id", this.productId).findFirst();
    }

    public String getId() {
        return id;
    }

    public long getTime() {
        return time;
    }

    public ToboTime getToboTime() {
        return new ToboTime(this.getTime());
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getOtherPersonId() {
        return otherPersonId;
    }

    public void setOtherPersonId(String otherPersonId) {
        this.otherPersonId = otherPersonId;
    }
}
