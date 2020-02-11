package com.tobo.huiset

import android.app.Application
import android.content.Intent
import android.preference.PreferenceManager
import com.tobo.huiset.gui.activities.IntroActivity
import com.tobo.huiset.realmModels.Person
import com.tobo.huiset.realmModels.Product
import com.tobo.huiset.utils.ProfileColors
import com.tobo.huiset.utils.extensions.edit
import io.realm.Realm
import io.realm.RealmConfiguration

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setupRealm()

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        if(prefs.getBoolean("firstLaunch", true)){
            createInitialData()
            prefs.edit {
                it.putBoolean("firstLaunch",false)
            }
        }
        if(! prefs.getBoolean("shownIntro",false)){
            val intent = Intent(this, IntroActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }


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

    private fun createInitialData() {
        val realm = Realm.getDefaultInstance()

        //create a standard huisrekening thing
        realm.executeTransaction {
            val huisRekening = Person.create("Huisrekening", ProfileColors.huisrekeningColor, false, true, 0,true)
            realm.copyToRealm(huisRekening)
        }
    }

}
