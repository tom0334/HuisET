package com.tobo.huiset

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView

import FragmentMain
import FragmentET
import FragmentStats
import android.util.Log

import androidx.fragment.app.Fragment


class MainActivity : AppCompatActivity() {


    lateinit var fragments: List<Fragment>
    lateinit var activeFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupBottomTabs()
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

        fragments = listOf(
            FragmentMain(),
            FragmentET(),
            FragmentStats()
        )

        for(i in fragments.indices){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.main_container, fragments[i], i.toString())
            if(i != 0) transaction.hide(fragments[i])
            transaction.commit()
        }
        activeFragment = fragments[0]

        bottomView.setOnNavigationItemSelectedListener {
            val fragToShow = when(it.itemId){
                R.id.action_beer -> fragments[0]
                R.id.action_ET -> fragments[1]
                R.id.action_stats -> fragments[2]
                else -> {
                    Log.e("Mainactivity", "Unknown action id")
                    fragments[0]
                }
            }
            showFragment(fragToShow)
            return@setOnNavigationItemSelectedListener true
        }
    }

    private fun showFragment(newFrag: Fragment){
        supportFragmentManager.beginTransaction().hide(activeFragment).show(newFrag).commit()
        activeFragment = newFrag
    }

}
