package com.tobo.huiset

import android.app.Application
import android.os.Bundle
import io.realm.Realm
import io.realm.RealmConfiguration

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setupRealm()
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

        // Use the config
        val realm = Realm.getInstance(config)

    }

}