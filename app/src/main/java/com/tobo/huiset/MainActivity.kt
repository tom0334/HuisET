package com.tobo.huiset

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tobo.huiset.realmModels.Person
import FragmentMain
import FragmentET
import FragmentProfiles
import android.util.Log

import androidx.fragment.app.Fragment


private const val OUTSTATE_CURRENTFRAGINDEX = "currentFragmentIndex"

class MainActivity : HuisEtActivity() {


    private lateinit var fragments: List<Fragment>
    private var currentFragmentIndex = 0

    private fun getFragTagFromIndex(index:Int) = "MAIN_ACTIVITY_FRAG_$index"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setupFragments(savedInstanceState)
        setupBottomTabs()

        realm.executeTransaction {
            val person = Person.create("botjo", "#ff00ff")
            realm.copyToRealm(person)
        }
        Toast.makeText(this, "aantal personen ${realm.where(Person::class.java).count()}", Toast.LENGTH_LONG).show()
    }


    // create an action bar button
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return super.onCreateOptionsMenu(menu)
    }

    // handle button activities
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()

        if (id == R.id.settings) {
            Toast.makeText(this, "settings clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        if (id == R.id.achievements) {
            Toast.makeText(this, "achievements clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, AchievementsActivity::class.java)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setupBottomTabs(){
        val bottomView  = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomView.inflateMenu(R.menu.menu_bottom_navigation)

        bottomView.setOnNavigationItemSelectedListener {
            val fragToShow = when(it.itemId){
                R.id.action_main -> 0
                R.id.action_ET -> 1
                R.id.action_profiles -> 2
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
                FragmentET(),
                FragmentProfiles()
            )
            //currentFragIndex is 0 by default
        }else{
            //restore them by finding them by tag
            fragments = listOf(
                supportFragmentManager.findFragmentByTag( getFragTagFromIndex(0))!!,
                supportFragmentManager.findFragmentByTag( getFragTagFromIndex(1))!!,
                supportFragmentManager.findFragmentByTag( getFragTagFromIndex(2))!!
            )
            currentFragmentIndex = savedInstanceState.getInt(OUTSTATE_CURRENTFRAGINDEX)
        }

        for (i in fragments.indices) {
            val transaction = supportFragmentManager.beginTransaction()

            // add them only if they weren't added yet.
            if(savedInstanceState == null) transaction.add(R.id.main_container, fragments[i], getFragTagFromIndex(i))

            //hide all but the current one
            if (i != currentFragmentIndex) transaction.hide(fragments[i])
            transaction.commit()
        }
    }



    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putInt( OUTSTATE_CURRENTFRAGINDEX, currentFragmentIndex )
    }

    private fun showFragment(newFragIndex: Int){
        supportFragmentManager.beginTransaction()
            .hide(fragments[currentFragmentIndex])
            .show(fragments[newFragIndex])
            .commit()
        currentFragmentIndex = newFragIndex
    }

}
