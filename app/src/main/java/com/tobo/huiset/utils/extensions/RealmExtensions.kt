package com.tobo.huiset.utils.extensions

import android.util.Log
import com.tobo.huiset.realmModels.HuisETSettings
import com.tobo.huiset.realmModels.Person
import com.tobo.huiset.realmModels.Product
import com.tobo.huiset.realmModels.Transaction
import io.realm.Realm
import io.realm.RealmResults
import java.lang.Exception

fun Realm.getFirstProduct() : Product? {
    return this.where(Product::class.java)
        .equalTo("deleted", false)
        .equalTo("show", true)
        .findFirst()
}

fun Realm.executeSafe(transaction: (Realm) -> Unit){
    try{
        this.executeTransaction(transaction)
    }catch (e:Exception){
        Log.e("REALMTRANSACTION", "RealmTransaction failed",e)
    }

}

fun Realm.findAllCurrentProducts(): RealmResults<Product>? {
    return this.where(Product::class.java).equalTo("deleted", false).findAll()
}

fun Realm.findAllCurrentPersons(): RealmResults<Person>? {
    return this.where(Person::class.java).equalTo("deleted", false).findAll()
}