package com.tobo.huiset.gui.activities

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.tobo.huiset.R
import com.tobo.huiset.extendables.HuisEtActivity
import com.tobo.huiset.gui.adapters.TransferPersonRecAdapter
import com.tobo.huiset.realmModels.Person

class TransferMoneyActivity : HuisEtActivity() {

    private var amountOfPersonsSelected: Int = 0
        set(value) {
            field = value
            findViewById<TextView>(R.id.MTselectedPersonsCounter)?.text = "Personen geselecteerd: $value"
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfermoney)
        initProfileRec()
    }

    private fun initProfileRec() {
        val selectPersonsRec = findViewById<RecyclerView>(R.id.MTpickUserRec)
        selectPersonsRec.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        val profiles = db.findAllCurrentPersonsWithBalanceNotZero()

        val selectPersonRecAdapter = TransferPersonRecAdapter(this, this, realm, profiles, true)
        selectPersonsRec.adapter = selectPersonRecAdapter
        selectPersonsRec.layoutManager = LinearLayoutManager(this)

        amountOfPersonsSelected = 0

        findViewById<MaterialButton>(R.id.MTselectedPersonsSaveButton).setOnClickListener {
            if (amountOfPersonsSelected == 0) {
                Toast.makeText(this, "Er zijn geen personen geselecteerd", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else {
                // hide selection view and show calculation view
                findViewById<View>(R.id.MTpickUsersLayout).visibility = View.GONE
                findViewById<View>(R.id.MTcalculationLayout).visibility = View.VISIBLE

                calculateTransfersAndShow(selectPersonRecAdapter)
            }
        }
    }

    private fun calculateTransfersAndShow(recAdapt: TransferPersonRecAdapter) {
        val calculatedPersonsRec = findViewById<RecyclerView>(R.id.MTcalculatedPersonsRec)
        calculatedPersonsRec.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        val realmResultsSelected = db.findPersonsWithIDInArray(recAdapt.chosenMap.toTypedArray())
        calculatedPersonsRec.adapter = TransferPersonRecAdapter(this, this, realm, realmResultsSelected, true)
        calculatedPersonsRec.layoutManager = LinearLayoutManager(this)

    }

    fun increaseCounter(bool: Boolean) {
        if (bool) amountOfPersonsSelected++
        else amountOfPersonsSelected--
    }

}