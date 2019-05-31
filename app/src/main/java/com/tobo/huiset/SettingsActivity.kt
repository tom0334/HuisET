package com.tobo.huiset

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class SettingsActivity : HuisEtActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }
}