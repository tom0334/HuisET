package com.tobo.huiset.gui.activities

import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.RelativeLayout
import android.widget.Switch
import androidx.appcompat.widget.SwitchCompat
import com.tobo.huiset.R
import com.tobo.huiset.extendables.HuisEtActivity
import com.tobo.huiset.utils.extensions.edit

const val PREFS_FULLSCREEN_ID = "FullscreenEnabled"
const val PREFS_HIDEAPPBAR_ID = "HideAppBarEnabled"

class SettingsActivity : HuisEtActivity() {

    private val prefs by lazy { PreferenceManager.getDefaultSharedPreferences(this) }
    private lateinit var hideAppBarSetting: RelativeLayout
    private lateinit var fullscreenSwitch: SwitchCompat
    private lateinit var hideAppBarSwitch: SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)
        showStatus()

        showOrHideAppBarSetting()

        fullscreenSwitch.setOnClickListener {
            showOrHideAppBarSetting()
        }
    }

    /**
     * hideAppBarSetting greys out and is unclickable iff fullScreenSwitch is not checked
     */
    private fun showOrHideAppBarSetting() {
        if (fullscreenSwitch.isChecked) {
            hideAppBarSetting.alpha = 1f
            hideAppBarSwitch.isClickable = true
        } else {
            hideAppBarSetting.alpha = 0.38f
            hideAppBarSwitch.isClickable = false
        }
    }

    private fun showStatus() {
        fullscreenSwitch = findViewById(R.id.fullScreenSwitch)
        hideAppBarSwitch = findViewById(R.id.hideAppBarSwitch)
        hideAppBarSetting = findViewById(R.id.hideAppBarSetting)

        fullscreenSwitch.isChecked = prefs.getBoolean(PREFS_FULLSCREEN_ID, false)
        hideAppBarSwitch.isChecked = prefs.getBoolean(PREFS_HIDEAPPBAR_ID, false)
    }

    private fun saveChanges() {
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