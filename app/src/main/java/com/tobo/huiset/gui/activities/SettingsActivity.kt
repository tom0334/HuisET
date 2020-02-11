package com.tobo.huiset.gui.activities

import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.RelativeLayout
import androidx.appcompat.widget.SwitchCompat
import com.tobo.huiset.R
import com.tobo.huiset.extendables.HuisEtActivity
import com.tobo.huiset.utils.extensions.edit

const val PREFS_DEPOSIT_ID = "DepositEnabled"
const val PREFS_HUISREKENING_ID = "HuisRekeningEnabled"
const val PREFS_FULLSCREEN_ID = "FullscreenEnabled"
const val PREFS_HIDE_APPBAR_ID = "HideAppBarEnabled"
const val PREFS_TURF_CONFETTI_ID = "TurfConfettiEnabled"

class SettingsActivity : HuisEtActivity() {

    private val prefs by lazy { PreferenceManager.getDefaultSharedPreferences(this) }

    private lateinit var hideAppBarSetting: RelativeLayout
    private lateinit var huisRekeningSwitch: SwitchCompat
    private lateinit var depositSwitch: SwitchCompat
    private lateinit var fullscreenSwitch: SwitchCompat
    private lateinit var hideAppBarSwitch: SwitchCompat
    private lateinit var turfConfettiSwitch: SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        initViewFields()

        showStatus()
        showOrHideAppBarSetting()

        fullscreenSwitch.setOnClickListener {
            showOrHideAppBarSetting()
        }
    }

    private fun initViewFields(){
        huisRekeningSwitch = findViewById(R.id.huisRekeningSwitch)
        depositSwitch = findViewById(R.id.depositSwitch)
        hideAppBarSetting = findViewById(R.id.hideAppBarSetting)
        fullscreenSwitch = findViewById(R.id.fullScreenSwitch)
        hideAppBarSwitch = findViewById(R.id.hideAppBarSwitch)
        turfConfettiSwitch = findViewById(R.id.turfConfettiSwitch)
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
        huisRekeningSwitch.isChecked = prefs.getBoolean(PREFS_HUISREKENING_ID, false)
        depositSwitch.isChecked = prefs.getBoolean(PREFS_DEPOSIT_ID, false)
        fullscreenSwitch.isChecked = prefs.getBoolean(PREFS_FULLSCREEN_ID, false)
        hideAppBarSwitch.isChecked = prefs.getBoolean(PREFS_HIDE_APPBAR_ID, false)
        turfConfettiSwitch.isChecked = prefs.getBoolean(PREFS_TURF_CONFETTI_ID,false)
    }

    private fun saveChanges() {
        db.setHuisRekeningActive(huisRekeningSwitch.isChecked)

        prefs.edit {
            it.putBoolean(PREFS_DEPOSIT_ID, depositSwitch.isChecked)
            it.putBoolean(PREFS_FULLSCREEN_ID, fullscreenSwitch.isChecked)
            it.putBoolean(PREFS_HIDE_APPBAR_ID, hideAppBarSwitch.isChecked)
            it.putBoolean(PREFS_TURF_CONFETTI_ID, turfConfettiSwitch.isChecked)
            it.putBoolean(PREFS_HUISREKENING_ID, huisRekeningSwitch.isChecked)
        }
    }


    override fun onPause() {
        super.onPause()
        saveChanges()
    }


}