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
import com.tobo.huiset.realmModels.Product
import com.tobo.huiset.realmModels.Transaction


class MainActivity : HuisEtActivity() {

    lateinit var fragments: List<Fragment>
    lateinit var activeFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
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

        fragments = listOf(
            FragmentMain(),
            FragmentET(),
            FragmentProfiles()
        )

        for(i in fragments.indices){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.main_container, fragments[i], i.toString())
            if(i != 0) transaction.hide(fragments[i])
            transaction.commit()
        }
        activeFragment = fragments[0]

        bottomView.setOnNavigationItemSelectedListener {

            if(it.itemId == R.id.action_main) addTransaction()

            val fragToShow = when(it.itemId){
                R.id.action_main -> fragments[0]
                R.id.action_ET -> fragments[1]
                R.id.action_profiles -> fragments[2]
                else -> {
                    Log.e("Mainactivity", "Unknown action id")
                    fragments[0]
                }
            }
            showFragment(fragToShow)
            return@setOnNavigationItemSelectedListener true
        }
    }

    private fun addTransaction() {
        //todo do this with the correct person
        val aPerson = realm.where(Person::class.java).findFirst()


        realm.executeTransaction {
            val t = Transaction.create(aPerson!!, realm.getBeerProduct())
            realm.copyToRealm(t)
        }
    }

    private fun showFragment(newFrag: Fragment){
        supportFragmentManager.beginTransaction().hide(activeFragment).show(newFrag).commit()
        activeFragment = newFrag
    }

}
