package com.tobo.huiset

import android.app.Application
import android.content.Intent
import android.preference.PreferenceManager
import com.tobo.huiset.gui.activities.IntroActivity
import com.tobo.huiset.gui.activities.PREFS_INTRO_SHOWN
import com.tobo.huiset.realmModels.Person
import com.tobo.huiset.realmModels.Product
import com.tobo.huiset.utils.ProfileColors
import com.tobo.huiset.utils.extensions.edit
import io.realm.*


class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setupRealm()

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        if (prefs.getBoolean("firstLaunch", true)) {
            createInitialData()
            prefs.edit {
                it.putBoolean("firstLaunch", false)
            }
        }
        if (!prefs.getBoolean(PREFS_INTRO_SHOWN, false)) {
            val intent = Intent(this, IntroActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }


    }

    private fun setupRealm() {
        Realm.init(this)

        // The RealmConfiguration is created using the builder pattern.
        // The Realm file will be located in Context.getFilesDir() with name "myrealm.realm"
        val config = RealmConfiguration.Builder()
            .name("myrealm.realm")
            .schemaVersion(2)
            .migration(getMigration())
            .build()

        // Set the config as default configuration
        Realm.setDefaultConfiguration(config)
    }

    private fun getMigration(): RealmMigration {

        class MyMigrations : RealmMigration {
            override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
                val schema = realm.schema
                var currentVersion = oldVersion
                if (currentVersion == 0L) {

                    val transactions = realm.where("Transaction").findAll()
                    val oldAmounts =
                        transactions.groupBy { x: DynamicRealmObject -> x.getString("id") }
                            .mapValues { entry -> entry.value.first().getInt("amount") }

                    schema.get("Transaction")?.apply {
                        removeField("amount")
                        addField("amount", Float::class.java)
                    }
                    transactions.forEach { t ->
                        val id = t.getString("id")
                        t.setFloat("amount", oldAmounts.get(id)!!.toFloat())
                    }

                    currentVersion++
                }
                if (currentVersion == 1L) {
                    val products = realm.where("Product").findAll()

                    schema.get("Product")?.apply {
                        addField("buyPerAmount", Int::class.java)
                    }
                    products.forEach { p ->
                        p.setInt("buyPerAmount", 1)
                        if (p.getInt("species") == 1) {
                            p.set("species", Product.SPECIES_BEER)
                        }
                    }

                    currentVersion++
                }

            }
        }

        return MyMigrations()
    }

    private fun createInitialData() {
        val realm = Realm.getDefaultInstance()

        //create a standard huisrekening thing
        realm.executeTransaction {
            val huisRekening =
                Person.create("Huisrekening", ProfileColors.huisrekeningColor, false, true, 0, true)
            realm.copyToRealm(huisRekening)
        }
    }

}
