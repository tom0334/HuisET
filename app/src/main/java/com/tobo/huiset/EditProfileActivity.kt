package com.tobo.huiset

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.tobo.huiset.realmModels.Person
import android.widget.EditText

/**
 * Edit profile
 * accessed by profiles -> add or profile -> edit
 */
class EditProfileActivity : HuisEtActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile)
    }

    // create an action bar button
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.menu_editprofile, menu);
        return super.onCreateOptionsMenu(menu)
    }

    // handle button activities
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()

        // profile edit/add done
        if (id == R.id.profiledone) {

            val editText= findViewById<EditText>(R.id.name)
            val name = editText.text.toString()

            if (!nameValidate(name, editText)) {
                return false
            }

            realm.executeTransaction {
                val person = Person.create(name, "#0000ff")
                realm.copyToRealm(person)
            }
            Toast.makeText(this, "profile $name added", Toast.LENGTH_SHORT).show()
            this.finish()

        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * Validates the input name
     */
    private fun nameValidate(name: String, editText: EditText): Boolean {
        // empty fields are not accepted
        if (name == "") {
            editText.error = "vul een naam in"
            return false
        }
        // duplicate names are not accepted
        if (realm.where(Person::class.java).equalTo("name", name).count() > 0) {
            editText.error = "naam bestaat al"
            return false
        }
        return true
    }

}