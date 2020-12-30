package com.tobo.huiset.utils.extensions

import android.util.Log
import com.tobo.huiset.utils.HuisETDB
import io.realm.Realm
import io.realm.RealmObject


fun Realm.executeSafe(transaction: (Realm) -> Unit) {
    try {
        this.executeTransaction(transaction)
    } catch (e: Exception) {
        Log.e("REALMTRANSACTION", "RealmTransaction failed", e)
    }

}

fun RealmObject.getDb(): HuisETDB {
    return HuisETDB(this.realm)
}

