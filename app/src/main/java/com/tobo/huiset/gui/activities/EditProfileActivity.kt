package com.tobo.huiset.gui.activities

import android.app.AlertDialog
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
import com.tobo.huiset.realmModels.Transaction

/**
 * Edit profile
 * accessed by profiles -> add or profile -> edit
 */
class EditProfileActivity : HuisEtActivity() {

    private var oldProfile: Person? = null
    private var new: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile)

        // reset old values of product is edited
        val extras = intent.extras
        if (extras != null) {
            val oldId = extras.getString("PERSON_ID")
            oldProfile = realm.where(Person::class.java).equalTo("id", oldId).findFirst()!!
            findViewById<EditText>(R.id.name).setText(oldProfile!!.name)

            val guestRadioGroup = findViewById<RadioGroup>(R.id.radiogroup_guest)
            if (oldProfile!!.isGuest) {
                guestRadioGroup.check(R.id.radioGuest)
            } else {
                guestRadioGroup.check(R.id.radioRoommate)
            }

            val showRadioGroup = findViewById<RadioGroup>(R.id.radiogroup_showPerson)
            if (oldProfile!!.isShow) {
                showRadioGroup.check(R.id.radioShowPerson)
            } else {
                showRadioGroup.check(R.id.radioHidePerson)
            }

            new = false
        }

    }

    // create an action bar button
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_editprofile, menu)

        return super.onCreateOptionsMenu(menu)
    }

    // handle button activities
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        // product edit/add done
        if (id == R.id.unitDone) {
            doneClicked()
        }
        if (id == R.id.unitDelete) {
            deleteClicked()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun deleteClicked() {
        if (new) {
            this.finish()
            return
        }
        // if profile isn't new, then ask "are you sure?
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Weet je zeker dat je ${oldProfile!!.name} wil verwijderen?")
            .setPositiveButton("verwijderen") { _, _ ->
                realm.executeTransaction {
                    if (realm.where(Transaction::class.java).equalTo("personId", oldProfile!!.id).findFirst() == null) {
                        // Actually delete the product from the realm if it isn't involved in any transactions
                        oldProfile!!.deleteFromRealm()
                    } else {
                        // fake delete product from the realm
                        oldProfile!!.isDeleted = true
                    }
                }
                this.finish()
            }
            .setNegativeButton("annuleren") { _, _ ->
                // User cancelled the dialog, do nothing
            }
        // Create the AlertDialog object and return it
        builder.create().show()
    }

    private fun doneClicked() {
        // profile edit/add done
        val nameEditText = findViewById<EditText>(R.id.name)
        val newName = nameEditText.text.toString()

        if (!nameValidate(newName, nameEditText)) {
            return
        }

        val radioGuestGroup = findViewById<RadioGroup>(R.id.radiogroup_guest).checkedRadioButtonId
        var guestBool = false
        if (radioGuestGroup == R.id.radioGuest) {
            guestBool = true
        }

        val radioShowGroup = findViewById<RadioGroup>(R.id.radiogroup_showPerson).checkedRadioButtonId
        var showBool = false
        if (radioShowGroup == R.id.radioShowPerson) {
            showBool = true
        }

        realm.executeTransaction {
            val newColorString = "#0000ff"
            if (new) {
                val person = Person.create(newName, newColorString, guestBool, showBool)
                realm.copyToRealm(person)
            } else {
                oldProfile!!.name = newName
                oldProfile!!.color = newColorString
                oldProfile!!.isGuest = guestBool
                oldProfile!!.isShow = showBool
            }
        }

        Toast.makeText(this, "Profile $newName added/edited, guest $guestBool, show $showBool", Toast.LENGTH_SHORT)
            .show()

        this.finish()
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
        // duplicate names are not accepted, except if the old person is deleted
        if (realm.where(Person::class.java)
                .equalTo("deleted", false)
                .equalTo("name", name)
                .count() > 0
        ) {
            if (new) {
                editText.error = "Naam bestaat al"
                return false
            }
        }
        // name is too long
        val maxNameLength = 12
        if (name.length > maxNameLength) {
            editText.error = "Naam mag niet langer dan $maxNameLength tekens zijn"
            return false
        }
        return true
    }

    /**
     * Hides keyboard when something else is clicked
     * param view is needed
     */
    fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }

}