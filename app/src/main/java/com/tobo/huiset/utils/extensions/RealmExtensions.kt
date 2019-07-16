package com.tobo.huiset.utils.extensions

import android.util.Log
import com.tobo.huiset.realmModels.Person
import com.tobo.huiset.realmModels.Product
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort


fun Realm.getProductWithId(productId: String): Product? {
    return this.where(Product::class.java)
        .equalTo("id", productId)
        .findFirst()
}

fun Realm.executeSafe(transaction: (Realm) -> Unit) {
    try {
        this.executeTransaction(transaction)
    } catch (e: Exception) {
        Log.e("REALMTRANSACTION", "RealmTransaction failed", e)
    }

}

fun Realm.findAllCurrentProducts(): RealmResults<Product>? {
    return this.where(Product::class.java)
        .equalTo("deleted", false)
        .sort("row", Sort.ASCENDING)
        .findAll()
}

fun Realm.findAllCurrentPersons(): RealmResults<Person>? {
    return this.where(Person::class.java)
        .equalTo("deleted", false)
        .sort("row", Sort.ASCENDING)
        .findAll()
}