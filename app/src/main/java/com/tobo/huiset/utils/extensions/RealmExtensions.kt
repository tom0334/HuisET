package com.tobo.huiset.utils.extensions

import android.util.Log
import com.tobo.huiset.realmModels.Person
import com.tobo.huiset.realmModels.Product
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort


fun Realm.executeSafe(transaction: (Realm) -> Unit) {
    try {
        this.executeTransaction(transaction)
    } catch (e: Exception) {
        Log.e("REALMTRANSACTION", "RealmTransaction failed", e)
    }

}

