package com.tobo.huiset

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupBottomTabs()
    }

    private fun setupBottomTabs(){
        val bottomView  = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomView.inflateMenu(R.menu.menu_bottom_navigation)
    }
}