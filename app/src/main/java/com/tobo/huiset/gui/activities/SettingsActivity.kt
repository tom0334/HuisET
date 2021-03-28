package com.tobo.huiset.gui.activities

import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.RelativeLayout
import androidx.appcompat.widget.SwitchCompat
import com.tobo.huiset.R
import com.tobo.huiset.extendables.HuisEtActivity
import com.tobo.huiset.utils.extensions.edit

const val PREFS_FULLSCREEN_ID = "FullscreenEnabled"
const val PREFS_HIDEAPPBAR_ID = "HideAppBarEnabled"
const val PREFS_TURF_CONFETTI_ID = "TurfConfettiEnabled"
const val PREFS_INTRO_SHOWN = "IntroIsShown"
const val PREFS_RECALCULATE_ACHIEVEMENTS_AFTER_REMOVE = "RecalculateAchievementsAfterRemoval"

class SettingsActivity : HuisEtActivity() {

    private val prefs by lazy { PreferenceManager.getDefaultSharedPreferences(this) }

    private lateinit var hideAppBarSetting: RelativeLayout
    private lateinit var fullscreenSwitch: SwitchCompat
    private lateinit var hideAppBarSwitch: SwitchCompat
    private lateinit var turfConfettiSwitch: SwitchCompat
    private lateinit var huisRekeningSwitch: SwitchCompat
    private lateinit var recalculateAfterRemoveSwitch: SwitchCompat

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

    private fun initViewFields() {
        hideAppBarSetting = findViewById(R.id.hideAppBarSetting)
        fullscreenSwitch = findViewById(R.id.fullScreenSwitch)
        hideAppBarSwitch = findViewById(R.id.hideAppBarSwitch)
        turfConfettiSwitch = findViewById(R.id.turfConfettiSwitch)
        huisRekeningSwitch = findViewById(R.id.huisRekeningSwitch)
        recalculateAfterRemoveSwitch = findViewById(R.id.recalculateAfterRemoveSwitch)
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
        fullscreenSwitch.isChecked = prefs.getBoolean(PREFS_FULLSCREEN_ID, false)
        hideAppBarSwitch.isChecked = prefs.getBoolean(PREFS_HIDEAPPBAR_ID, false)
        turfConfettiSwitch.isChecked = prefs.getBoolean(PREFS_TURF_CONFETTI_ID, false)
        huisRekeningSwitch.isChecked = !db.getHuisRekening().isDeleted
        recalculateAfterRemoveSwitch.isChecked = prefs.getBoolean(PREFS_RECALCULATE_ACHIEVEMENTS_AFTER_REMOVE, false)
    }

    private fun saveChanges() {
        prefs.edit {
            it.putBoolean(PREFS_FULLSCREEN_ID, fullscreenSwitch.isChecked)
            it.putBoolean(PREFS_HIDEAPPBAR_ID, hideAppBarSwitch.isChecked)
            it.putBoolean(PREFS_TURF_CONFETTI_ID, turfConfettiSwitch.isChecked)
            it.putBoolean(PREFS_RECALCULATE_ACHIEVEMENTS_AFTER_REMOVE, recalculateAfterRemoveSwitch.isChecked)
        }

        db.setHuisRekeningActive(huisRekeningSwitch.isChecked)
    }


    override fun onPause() {
        super.onPause()
        saveChanges()
    }


}