package com.tobo.huiset.gui.activies

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.tobo.huiset.realmModels.Person
import android.widget.EditText
import android.widget.RadioGroup
import com.tobo.huiset.extendables.HuisEtActivity
import com.tobo.huiset.R

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

            val nameEditText= findViewById<EditText>(R.id.name)
            val name = nameEditText.text.toString()

            if (!nameValidate(name, nameEditText)) {
                return false
            }

            val radioGuestGroup = findViewById<RadioGroup>(R.id.radiogroup_guest).checkedRadioButtonId
            var guestBool = false
            if (radioGuestGroup == R.id.radioGuest) {
                guestBool = true
            }

            val radioShowGroup = findViewById<RadioGroup>(R.id.radiogroup_show).checkedRadioButtonId
            var showBool = false
            if (radioShowGroup == R.id.radioShow) {
                showBool = true
            }

            realm.executeTransaction {
                val person = Person.create(name, "#0000ff", guestBool, showBool)
                realm.copyToRealm(person)
            }

            Toast.makeText(this, "Profile $name added, guest $guestBool, show $showBool", Toast.LENGTH_SHORT).show()

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
            editText.error = "Vul een naam in"
            return false
        }
        // duplicate names are not accepted
        if (realm.where(Person::class.java).equalTo("name", name).count() > 0) {
            editText.error = "Naam bestaat al"
            return false
        }
        // name is too long
        val maxNameLength = 12
        if (name.length > maxNameLength) {
            editText.error = "Naam mag niet langer dan $maxNameLength tekens zijn"
            return false
        }
        return true
    }

    fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }

}