package com.tobo.huiset.gui.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import FragmentMain
import FragmentHistory
import FragmentPurchases
import FragmentProducts
import FragmentProfiles
import android.util.Log

import com.tobo.huiset.R
import com.tobo.huiset.extendables.HuisEtActivity
import com.tobo.huiset.extendables.HuisEtFragment

private const val NUM_FRAGMENTS = 5
private const val OUTSTATE_CURRENTFRAGINDEX = "currentFragmentIndex"

class MainActivity : HuisEtActivity() {


    private lateinit var fragments: List<HuisEtFragment>
    private var currentFragmentIndex = 0

    private fun getFragTagFromIndex(index: Int) = "MAIN_ACTIVITY_FRAG_$index"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setupFragments(savedInstanceState)
        setupBottomTabs()
    }


    // create an action bar button
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu)
    }

    // handle button activities
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.stats) {
            Toast.makeText(this, "stats clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, StatsActivity::class.java)
            startActivity(intent)
        }

        if (id == R.id.achievements) {
            Toast.makeText(this, "achievements clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, AchievementsActivity::class.java)
            startActivity(intent)
        }

        if (id == R.id.settings) {
            Toast.makeText(this, "settings clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
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
                R.id.action_products -> 2
                R.id.action_history -> 3
                R.id.action_profiles -> 4
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

    private fun showFragment(newFragIndex: Int) {
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

}
