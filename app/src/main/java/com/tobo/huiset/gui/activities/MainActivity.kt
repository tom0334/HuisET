package com.tobo.huiset.gui.activities

import FragmentHistory
import FragmentMain
import FragmentProducts
import FragmentProfiles
import FragmentPurchases
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tobo.huiset.R
import com.tobo.huiset.achievements.AchievementManager
import com.tobo.huiset.extendables.CelebratingHuisEtActivity
import com.tobo.huiset.extendables.HuisEtFragment
import com.tobo.huiset.realmModels.AchievementCompletion
import nl.dionsegijn.konfetti.KonfettiView
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size

private const val NUM_FRAGMENTS = 5
private const val OUTSTATE_CURRENTFRAGINDEX = "currentFragmentIndex"
private const val MAINACTIVITY_REQUESTCODE_SETTINGS = 1

class MainActivity : CelebratingHuisEtActivity() {


    val PRODUCTS_TAB = 2
    val PROFILES_TAB = 4

    private lateinit var fragments: List<HuisEtFragment>
    private var currentFragmentIndex = 0

    private val fullScreenMode get() = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(PREFS_FULLSCREEN_ID,false)

    private val hideAppBar get() = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(PREFS_HIDEAPPBAR_ID,false)

    // for hiding the appbar and navbar in fullscreen mode
    private val systemUIHandler = Handler()
    private val hideSysRunnable = Runnable { hideSystemUI() }


    private fun getFragTagFromIndex(index: Int) = "MAIN_ACTIVITY_FRAG_$index"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupFragments(savedInstanceState)
        setupBottomTabs()

        if (fullScreenMode) {
            hideSystemUI()
            setSystemUIListener()
        } else {
            systemUIHandler.removeCallbacks(null)
        }
    }

    override fun onPause() {
        super.onPause()
        systemUIHandler.removeCallbacks(null)
    }

    override fun onResume() {
        super.onResume()

        val changes = mutableListOf<AchievementCompletion>()
        db.findAllCurrentPersons(true).forEach {
            val new = AchievementManager.updateAchievementsAfterLaunch(it)
            changes.addAll(new)
        }
        showAchievements(changes)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == MAINACTIVITY_REQUESTCODE_SETTINGS){
            val intent = Intent(this, MainActivity::class.java)
            this.startActivity(intent)
            this.finish()
        }
    }


    // create an action bar button
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // handle button activities
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.stats) {
            val intent = Intent(this, StatsActivity::class.java)
            startActivity(intent)
        }

        if (id == R.id.achievements) {
            val intent = Intent(this, AchievementsActivity::class.java)
            startActivity(intent)
        }

        if (id == R.id.settings) {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivityForResult(intent, MAINACTIVITY_REQUESTCODE_SETTINGS)
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setupBottomTabs() {
        val bottomView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomView.inflateMenu(R.menu.menu_bottom_navigation)

        bottomView.setOnNavigationItemSelectedListener {

            val fragToShow = when (it.itemId) {
                R.id.action_main -> 0
                R.id.action_purchases -> 1
                R.id.action_products -> PRODUCTS_TAB
                R.id.action_history -> 3
                R.id.action_profiles -> PROFILES_TAB
                else -> {
                    Log.e("Mainactivity", "Unknown action id")
                    0
                }
            }
            showFragment(fragToShow)
            return@setOnNavigationItemSelectedListener true
        }
    }

    /**
     * This function restores or creates the needed fragments. It needs to be called BEFORE the setupBottomTabs
     * method!
     */
    private fun setupFragments(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            //create new fragments
            fragments = listOf(
                FragmentMain(),
                FragmentPurchases(),
                FragmentProducts(),
                FragmentHistory(),
                FragmentProfiles()
            )
            //currentFragIndex is 0 by default
        } else {
            //restore them by finding them by tag
            fragments = (0 until NUM_FRAGMENTS)
                .map { supportFragmentManager.findFragmentByTag(getFragTagFromIndex(it))!! as HuisEtFragment }
            currentFragmentIndex = savedInstanceState.getInt(OUTSTATE_CURRENTFRAGINDEX)
        }

        for (i in fragments.indices) {
            val transaction = supportFragmentManager.beginTransaction()

            // add them only if they weren't added yet.
            if (savedInstanceState == null) transaction.add(R.id.main_container, fragments[i], getFragTagFromIndex(i))

            //hide all but the current one
            if (i != currentFragmentIndex) transaction.hide(fragments[i])
            transaction.commit()
        }
    }

    fun showFragment(newFragIndex: Int) {
        supportFragmentManager.beginTransaction()
            .hide(fragments[currentFragmentIndex])
            .show(fragments[newFragIndex])
            .commit()
        currentFragmentIndex = newFragIndex
        // tell the fragment that is is shown again
        fragments[newFragIndex].onTabReactivated()

    }


    override fun onBackPressed() {
        val handled = fragments[currentFragmentIndex].onBackButtonPressed()
        if(handled) return
        super.onBackPressed()

    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putInt(OUTSTATE_CURRENTFRAGINDEX, currentFragmentIndex)
    }


    private fun setSystemUIListener() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        val decorView = window.decorView
        decorView.setOnSystemUiVisibilityChangeListener {
            // Note that system bars will only be "visible" if none of the
            // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are setting.

            supportActionBar?.show()
            systemUIHandler.removeCallbacks(hideSysRunnable)
            systemUIHandler.postDelayed(hideSysRunnable, 2000)
        }

        decorView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN,

                MotionEvent.EDGE_TOP -> {
                    supportActionBar!!.show()
                    systemUIHandler.removeCallbacks(hideSysRunnable)
                    systemUIHandler.postDelayed(hideSysRunnable, 2000)
                }
            }
            false
        }

    }

    private fun hideSystemUI() {
        val decorView = window.decorView
        if(hideAppBar){
            supportActionBar!!.hide()
            decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar

                            or View.SYSTEM_UI_FLAG_FULLSCREEN// hide status bar
                            or View.SYSTEM_UI_FLAG_IMMERSIVE
                    )
        }else{
            //same but without stable
            decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            or View.SYSTEM_UI_FLAG_FULLSCREEN// hide status bar
                            or View.SYSTEM_UI_FLAG_IMMERSIVE
                    )
        }
    }


    fun showTurfConfetti(){
        val viewKonfetti = findViewById<KonfettiView>(R.id.viewKonfetti)
        viewKonfetti.build()
            .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
            .setDirection(0.0, 359.0)
            .setSpeed(1f, 5f)
            .setFadeOutEnabled(true)
            .setTimeToLive(2000L)
            .addShapes( Shape.CIRCLE)
            .addSizes(Size(12))
            .setPosition(-50f, viewKonfetti.width + 50f, 0f, -50f)
            .burst(300)
    }



    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (fullScreenMode) {
            if(hideAppBar) supportActionBar!!.hide() else supportActionBar!!.show()
            systemUIHandler.removeCallbacks(hideSysRunnable)
            systemUIHandler.postDelayed(hideSysRunnable, 1000)
        }
    }

    /**
     * Snackbars must appear directly above the bottom bar
     */
    override fun getSnackbarBottomMargin(): Int {
        return this.findViewById<View>(R.id.bottomNavigation).height
    }




}
