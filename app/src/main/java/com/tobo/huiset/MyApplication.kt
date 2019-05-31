package com.tobo.huiset

import android.app.Application
import com.tobo.huiset.realmModels.Product
import io.realm.Realm
import io.realm.RealmConfiguration

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setupRealm()
        createStandardProductsIfNeeded()
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

    private fun createStandardProductsIfNeeded() {
        val realm = Realm.getDefaultInstance()

        val builtIn = realm.where(Product::class.java)
            .equalTo("builtIn", true)

        if(builtIn.count()  > 0L) return


        realm.executeTransaction {
            val beer = Product.createBuiltInProduct(Product.ID_BEER,"Bier",Product.STANDARD_PRICE_BEER)
            realm.copyToRealm(beer)

            val crate = Product.createBuiltInProduct(Product.ID_CRATE,"Kratje",Product.STANDARD_PRICE_CRATE)
            realm.copyToRealm(crate)
        }

    }

}
