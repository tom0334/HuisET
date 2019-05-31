package com.tobo.huiset

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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