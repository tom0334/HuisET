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


    lateinit var mainFrag: FragmentMain
    lateinit var etFrag:FragmentET
    lateinit var statsFrag:FragmentStats

    lateinit var activeFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupBottomTabs()
    }

    private fun setupBottomTabs(){
        val bottomView  = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomView.inflateMenu(R.menu.menu_bottom_navigation)

        mainFrag = FragmentMain()
        etFrag = FragmentET()
        statsFrag = FragmentStats()

        activeFragment = mainFrag

        supportFragmentManager.beginTransaction().add(R.id.main_container, statsFrag, "3").hide(statsFrag).commit()
        supportFragmentManager.beginTransaction().add(R.id.main_container, etFrag, "2").hide(etFrag).commit()
        supportFragmentManager.beginTransaction().add(R.id.main_container,mainFrag, "1").commit()



        fun showFragment(newFrag: Fragment){
            supportFragmentManager.beginTransaction().hide(activeFragment).show(newFrag).commit()
            activeFragment = newFrag
        }


        bottomView.setOnNavigationItemSelectedListener {
            println(it.itemId)
            val fragToShow = when(it.itemId){
                R.id.action_beer -> mainFrag
                R.id.action_ET -> etFrag
                R.id.action_stats -> statsFrag
                else -> {
                    Log.e("Mainactivity", "Unknown action id")
                    mainFrag
                }
            }
            showFragment(fragToShow)
            return@setOnNavigationItemSelectedListener true
        }
    }




}