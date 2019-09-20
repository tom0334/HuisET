package com.tobo.huiset.realmModels;

import com.tobo.huiset.utils.ToboTime;
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
    private int amount;
    private boolean buy;

    public Transaction() {
    }

    static public Transaction create(Person person, Product product, int amount, boolean buy) {
        Transaction t = new Transaction();
        t.personId = person.getId();
        t.productId = product.getId();
        t.buy = buy;
        t.amount = amount;
        t.price = product.getPrice() * amount;
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

    public int getSaldoImpact(){
        if(isBuy()) return price;
        else return -1 * price;
    }


    public boolean isBuy() {
        return buy;
    }

    public Person getPerson(Realm realm) {
        return realm.where(Person.class).equalTo("id", this.personId).findFirst();
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
