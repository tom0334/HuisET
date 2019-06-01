package com.tobo.huiset.utils.extensions

import com.tobo.huiset.realmModels.HuisETSettings
import com.tobo.huiset.realmModels.Product
import io.realm.Realm

fun Realm.getBeerProduct() : Product{
    return this.where(HuisETSettings::class.java).findFirst()!!.beerProduct

}

fun Realm.getCrateProduct() : Product{
    return this.where(HuisETSettings::class.java).findFirst()!!.beerProduct

}
