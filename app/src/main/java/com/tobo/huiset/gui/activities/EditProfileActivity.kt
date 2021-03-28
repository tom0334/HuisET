package com.tobo.huiset.gui.activities

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.tobo.huiset.R
import com.tobo.huiset.extendables.HuisEtActivity
import com.tobo.huiset.gui.adapters.PersonAchievementRecAdapter
import com.tobo.huiset.realmModels.Person
import com.tobo.huiset.utils.HandyFunctions
import com.tobo.huiset.utils.ProfileColors

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
            oldProfile = db.getPersonWithId(oldId)

            val nameEditText = findViewById<EditText>(R.id.name)
            nameEditText.setText(oldProfile!!.name)
            nameEditText.requestFocus()

            val guestText = findViewById<TextView>(R.id.radiogroup_guest_text)
            val guestRadioGroup = findViewById<RadioGroup>(R.id.radiogroup_guest)

            if (oldProfile!!.isHuisRekening) {
                guestText.visibility = View.GONE
                guestRadioGroup.visibility = View.GONE
            } else {
                guestText.visibility = View.VISIBLE
                guestRadioGroup.visibility = View.VISIBLE
            }

            if (oldProfile!!.isGuest) {
                guestRadioGroup.check(R.id.radioGuest)
            } else {
                guestRadioGroup.check(R.id.radioRoommate)
            }

            val showRadioGroup = findViewById<RadioGroup>(R.id.radiogroup_showPerson)
            if (oldProfile!!.show) {
                showRadioGroup.check(R.id.radioShowPerson)
            } else {
                showRadioGroup.check(R.id.radioHidePerson)
            }

            val allAchievements = db.findPersonalAchievementsFor(oldProfile!!)
            val rec = findViewById<RecyclerView>(R.id.personalAchievementsRec)
            rec.adapter = PersonAchievementRecAdapter(this, db, allAchievements, true)
            rec.layoutManager = LinearLayoutManager(this)

            new = false
        } else {
            showSoftKeyboard(findViewById(R.id.name))
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
        when {
            new -> {
                this.finish()
                return
            }
            oldProfile!!.balance != 0 -> {
                val view = findViewById<View>(R.id.editProfileView)
                hideKeyboard(view)
                Snackbar.make(
                    view,
                    "Dit persoon moet eerst afrekenen voordat hij verwijderd kan worden",
                    Snackbar.LENGTH_LONG
                ).setAction("Afrekenen", View.OnClickListener {
                    val intent = Intent(this, TransferMoneyActivity::class.java)
                    startActivity(intent)
                }).show()
            }
            oldProfile!!.isHuisRekening -> {
                val view = findViewById<View>(R.id.editProfileView)
                hideKeyboard(view)
                Snackbar.make(
                    view,
                    "De huisrekening kan alleen uitgezet worden via de instellingen",
                    Snackbar.LENGTH_LONG
                ).setAction("Instellingen", View.OnClickListener {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                }).show()
            }
            else -> {
                // if profile isn't new, then ask "are you sure?"
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Weet je zeker dat je ${oldProfile!!.name} wil verwijderen?")
                    .setPositiveButton("verwijderen") { _, _ ->
                        Toast.makeText(
                            this,
                            "Profiel ${oldProfile!!.name} is verwijderd",
                            Toast.LENGTH_SHORT
                        ).show()
                        db.removeProfile(oldProfile!!)
                        db.updateProfileRows()
                        this.finish()
                    }
                    .setNegativeButton("annuleren") { _, _ ->
                        // User cancelled the dialog, do nothing
                    }
                // Create the AlertDialog object and return it
                builder.create().show()
            }
        }
    }

    private fun doneClicked() {
        // profile edit/add done
        val nameEditText = findViewById<EditText>(R.id.name)
        val newName = nameEditText.text.toString()

        val isHuisRekening = if (oldProfile != null) oldProfile!!.isHuisRekening else false
        if (!HandyFunctions.nameValidate(newName, nameEditText, db, new, 0, isHuisRekening)) {
            return
        }

        val radioGuestGroup = findViewById<RadioGroup>(R.id.radiogroup_guest).checkedRadioButtonId
        val guestBool = (radioGuestGroup == R.id.radioGuest)

        val radioShowGroup =
            findViewById<RadioGroup>(R.id.radiogroup_showPerson).checkedRadioButtonId
        val showBool = (radioShowGroup == R.id.radioShowPerson)

        db.updateProfileRows()
        val row = db.findAllCurrentPersons(true).size

        realm.executeTransaction {
            if (new) {
                val newColorString = ProfileColors.getNextColor(db)
                val person = Person.create(newName, newColorString, guestBool, showBool, row, false)
                realm.copyToRealm(person)
            } else {
                oldProfile!!.name = newName
                oldProfile!!.isGuest = guestBool
                oldProfile!!.show = showBool
            }
        }

        Toast.makeText(this, "Profiel $newName toegevoegd/aangepast", Toast.LENGTH_SHORT).show()

        this.finish()
    }

    // automatically opens keyboard on startup
    private fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    /**
     * Hides keyboard when something else is clicked
     * param view is needed
     */
    fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}