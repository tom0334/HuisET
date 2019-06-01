package com.tobo.huiset.utils.extensions

import android.util.Log
import com.tobo.huiset.realmModels.HuisETSettings
import com.tobo.huiset.realmModels.Person
import com.tobo.huiset.realmModels.Product
import com.tobo.huiset.realmModels.Transaction
import io.realm.Realm
import java.lang.Exception

fun Realm.getBeerProduct() : Product{
    return this.where(HuisETSettings::class.java).findFirst()!!.beerProduct

}

fun Realm.getCrateProduct() : Product{
    return this.where(HuisETSettings::class.java).findFirst()!!.beerProduct
}

fun Realm.executeSafe(transaction: (Realm) -> Unit){
    try{
        this.executeTransaction(transaction)
    }catch (e:Exception){
        Log.e("REALMTRANSACTION", "RealmTransaction failed",e)
    }

}

