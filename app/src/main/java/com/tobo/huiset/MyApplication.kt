package com.tobo.huiset

import android.app.Application
import com.tobo.huiset.realmModels.HuisETSettings
import com.tobo.huiset.realmModels.Product
import io.realm.Realm
import io.realm.RealmConfiguration

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setupRealm()
        createHuisEtSettingsNeeded()
    }

    private fun setupRealm() {
        Realm.init(this)

        // The RealmConfiguration is created using the builder pattern.
        // The Realm file will be located in Context.getFilesDir() with name "myrealm.realm"
        val config = RealmConfiguration.Builder()
            .name("myrealm.realm")
            .schemaVersion(0)
            .build()

        // Set the config as default configuration
        Realm.setDefaultConfiguration(config)
    }

    private fun createHuisEtSettingsNeeded() {
        val realm = Realm.getDefaultInstance()

        val settings = realm.where(HuisETSettings::class.java)

        if (settings.count() > 0) return

        realm.executeTransaction {
            val beer = Product.create("Bier", Product.STANDARD_PRICE_BEER, Product.ONLY_TURFABLE, 0, Product.BEERPRODUCT)
            beer.isSelected = true
            //no need copy, it is copy with the settings
            val crate = Product.create("Kratje", Product.STANDARD_PRICE_CRATE, Product.ONLY_BUYABLE, 1, Product.CRATEPRODUCT)
            //no need to copy, it is copied with the settings
            val newSettingsObj = HuisETSettings.create(beer, crate)
            realm.copyToRealm(newSettingsObj)
        }

    }

}
