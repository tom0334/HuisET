package com.tobo.huiset.gui.activities

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.widget.SwitchCompat
import com.tobo.huiset.R
import com.tobo.huiset.extendables.HuisEtActivity
import com.tobo.huiset.utils.extensions.edit

const val PREFS_FULLSCREEN_ID = "FullscreenEnabled"

class SettingsActivity : HuisEtActivity() {

    private val prefs by lazy { PreferenceManager.getDefaultSharedPreferences(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        showStatus()
    }


    fun showStatus() {
        val fullscreenSwitch = findViewById<SwitchCompat>(R.id.fullScreenSwitch)
        fullscreenSwitch.isChecked = prefs.getBoolean(PREFS_FULLSCREEN_ID, false)
    }

    private fun saveChanges() {
        val fullscreenSwitch = findViewById<SwitchCompat>(R.id.fullScreenSwitch)
        prefs.edit {
            it.putBoolean(PREFS_FULLSCREEN_ID, fullscreenSwitch.isChecked)
        }

    }


    override fun onPause() {
        super.onPause()
        saveChanges()
    }


}