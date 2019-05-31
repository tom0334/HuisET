package com.tobo.huiset


import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm

abstract class HuisEtActivity : AppCompatActivity() {

     val realm:Realm by lazy { Realm.getDefaultInstance() }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

}