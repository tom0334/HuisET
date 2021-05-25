package com.tobo.huiset

import android.app.Application
import android.content.Intent
import android.preference.PreferenceManager
import com.tobo.huiset.achievements.AchievementManager
import com.tobo.huiset.gui.activities.IntroActivity
import com.tobo.huiset.gui.activities.PREFS_INTRO_SHOWN
import com.tobo.huiset.realmModels.Person
import com.tobo.huiset.realmModels.Product
import com.tobo.huiset.utils.HuisETDB
import com.tobo.huiset.utils.ProfileColors
import com.tobo.huiset.utils.extensions.edit
import io.realm.*


class MyApplication : Application() {

    var needToRecalculateAchievements = false

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
            .schemaVersion(4)
            .migration(getMigration())
            .allowQueriesOnUiThread(true)
            .allowWritesOnUiThread(true)
            .build()

        // Set the config as default configuration
        Realm.setDefaultConfiguration(config)


        //When we add a new achievement or fix a bug in a achievement, the "Cache" needs to be cleared after fixing it.
        //all achievements are recalculated  to make sure there are no mistakes

        //If we don't this right now  it will show confetti when it notices
        // an achievement was previously achieved.
        if(needToRecalculateAchievements){
            val db = HuisETDB(Realm.getDefaultInstance())
            db.findAllCurrentPersons(true).forEach {
                AchievementManager.findNewCompletionsForPerson(it)
            }
            db.close()
        }
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


                if (currentVersion == 2L) {

                    val transactionSideEffectSchema = schema.create("TransactionSideEffect")
                    transactionSideEffectSchema.apply {
                        addField("personId", String::class.java)
                        addField("price", Int::class.java)
                        addField("buy", Boolean::class.java)
                    }

                    schema.get("Transaction")?.apply {
                        addRealmListField("sideEffects", transactionSideEffectSchema)
                        addField("message", String::class.java)
                    }

                    val transactions = realm.where("Transaction").findAll()
                    val moneyTransfers = transactions.filter {it.getString("otherPersonId") !=null }

                    moneyTransfers.forEach {
                        it.setString("message", "Overgemaakt")
                        val sideEffect = realm.createObject("TransactionSideEffect")
                        sideEffect.apply {
                            setString("personId",it.getString("otherPersonId"))
                            setInt("price",it.getInt("price"))
                            setBoolean("buy", false)
                        }
                        it.getList("sideEffects").add(sideEffect)
                    }

                    currentVersion++
                }

                if (currentVersion == 3L) {
                    val persons = realm.where("Person").findAll()
                    // Profiles that aren't huisRekening get the colour orange instead of black.
                    persons.forEach { p ->
                        if (p.getString("color") == ProfileColors.huisrekeningColor
                            && !p.getBoolean("huisRekening")) {
                            p.set("color", "#FFA000")
                        }
                    }
                    
                    currentVersion++
                }

                //The app used to have a bug where it saved achievements twice. This was fixed in 1.3.1
                if(currentVersion == 4L){
                    needToRecalculateAchievements = true
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
