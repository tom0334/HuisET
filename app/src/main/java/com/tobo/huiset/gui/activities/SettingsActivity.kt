package com.tobo.huiset.gui.activities

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.widget.SwitchCompat
import com.tobo.huiset.R
import com.tobo.huiset.extendables.HuisEtActivity
import com.tobo.huiset.utils.extensions.edit

const val PREFS_FULLSCREEN_ID = "FullscreenEnabled"
const val PREFS_HIDEAPPBAR_ID = "HideAppBarEnabled"

class SettingsActivity : HuisEtActivity() {

    private val prefs by lazy { PreferenceManager.getDefaultSharedPreferences(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        showStatus()
    }


    private fun showStatus() {
        val fullscreenSwitch = findViewById<SwitchCompat>(R.id.fullScreenSwitch)
        val hideAppBarSwitch = findViewById<SwitchCompat>(R.id.hideAppBarSwitch)

        fullscreenSwitch.isChecked = prefs.getBoolean(PREFS_FULLSCREEN_ID, false)
        hideAppBarSwitch.isChecked = prefs.getBoolean(PREFS_HIDEAPPBAR_ID, false)

    }

    private fun saveChanges() {
        val fullscreenSwitch = findViewById<SwitchCompat>(R.id.fullScreenSwitch)
        val hideAppBarSwitch = findViewById<SwitchCompat>(R.id.hideAppBarSwitch)
        prefs.edit {
            it.putBoolean(PREFS_FULLSCREEN_ID, fullscreenSwitch.isChecked)
            it.putBoolean(PREFS_HIDEAPPBAR_ID, hideAppBarSwitch.isChecked)
        }

    }


    override fun onPause() {
        super.onPause()
        saveChanges()
    }


}