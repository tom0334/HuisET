package com.tobo.huiset

import android.os.Bundle
import android.widget.Toast
import com.tobo.huiset.realmModels.Person
import android.view.View
import android.widget.EditText


class EditProfileActivity : HuisEtActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile)
    }

    fun fabClicked(view: View){
        val editText= findViewById<EditText>(R.id.name)
        val name = editText.text.toString()
        Toast.makeText(this, "done button clicked $name", Toast.LENGTH_SHORT).show()
        if (name == "") {
            editText.error = "vul een naam in"
        } else {
            realm.executeTransaction {
                val person = Person.create(name, "#0000ff")
                realm.copyToRealm(person)
            }
            this.finish()
        }
        
    }

}