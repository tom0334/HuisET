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
import com.tobo.huiset.gui.adapters.TransferCalcPersonRecAdapter
import com.tobo.huiset.gui.adapters.TransferPersonRecAdapter
import com.tobo.huiset.realmModels.Person
import com.tobo.huiset.realmModels.Product
import com.tobo.huiset.realmModels.Transaction
import com.tobo.huiset.utils.extensions.toCurrencyString

class TransferMoneyActivity : HuisEtActivity() {

    private var amountOfPersonsSelected: Int = 0
        set(value) {
            field = value
            findViewById<TextView>(R.id.MTselectedPersonsCounter)?.text = "Personen geselecteerd: $value"
        }

    private var amountOfPersonsPaid: Int = 0
        set(value) {
            field = value
            findViewById<TextView>(R.id.MThasPaidPersonsCounter)?.text = "Aantal personen: $value"
        }

    private var amountOfMoneyPaid: Int = 0
        set(value) {
            field = value
            findViewById<TextView>(R.id.MTmoneyPaidCounter)?.text = "Hebben in totaal overgemaakt: ${value.toCurrencyString()}"
        }

    private var transactionMap: MutableMap<Person, Transaction> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfermoney)
        initProfileRec()
    }

    private fun initProfileRec() {
        val selectPersonsRec = findViewById<RecyclerView>(R.id.MTpickUserRec)
        selectPersonsRec.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        val profiles = db.findAllCurrentPersonsWithBalanceNegative()

        val selectPersonRecAdapter = TransferPersonRecAdapter(this, this, realm, profiles, true)
        selectPersonsRec.adapter = selectPersonRecAdapter
        selectPersonsRec.layoutManager = LinearLayoutManager(this)

        amountOfPersonsSelected = 0
        amountOfPersonsPaid = 0
        amountOfMoneyPaid = 0

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

        findViewById<MaterialButton>(R.id.MThasPaidPersonsSaveButton).setOnClickListener {
            if (amountOfPersonsPaid == 0) {
                Toast.makeText(this, "Niemand heeft geld overgemaakt", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this, "Totaal ${amountOfMoneyPaid.toCurrencyString()} overgemaakt door $amountOfPersonsPaid personen", Toast.LENGTH_SHORT).show()
            }
            this.finish()
        }

    }

    private fun calculateTransfersAndShow(recAdapt: TransferPersonRecAdapter) {
        val calculatedPersonsRec = findViewById<RecyclerView>(R.id.MTcalculatedPersonsRec)
        calculatedPersonsRec.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        val realmResultsSelected = db.findPersonsWithIDInArray(recAdapt.chosenMap.toTypedArray())
        calculatedPersonsRec.adapter = TransferCalcPersonRecAdapter(this, this, realm, realmResultsSelected, true)
        calculatedPersonsRec.layoutManager = LinearLayoutManager(this)


    }

    fun increaseSelectedPersonsCounter(bool: Boolean) {
        if (bool) amountOfPersonsSelected++
        else amountOfPersonsSelected--
    }

    fun someonePaidSomeone(payer: Person, receiver: Person, money: Int, undo: Boolean) {
        val transactionProduct = Product.create("Overgemaakt", money, Product.BOTH_TURF_AND_BUY, 13, Product.OTHERPRODUCT)
        realm.executeTransaction {
            realm.copyToRealm(transactionProduct)
        }

        if (!undo) {
            val transaction = db.createAndSaveTransfer(payer, receiver, money)
            transactionMap[payer] = transaction

            amountOfPersonsPaid++
            amountOfMoneyPaid -= money
        }
        else {
            db.deleteTransaction(transactionMap[payer]!!, payer)
            amountOfPersonsPaid--
            amountOfMoneyPaid += money
        }

        db.removeProduct(transactionProduct)
    }
}