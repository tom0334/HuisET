package com.tobo.huiset.gui.activities

import android.os.Bundle
import android.preference.PreferenceManager
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

    val huisRekeningEnabled: Boolean
        get() = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(PREFS_HUISREKENING_ID, false)

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
            findViewById<TextView>(R.id.MTmoneyPaidCounter)?.text = "In totaal overgemaakt: ${value.toCurrencyString()}"
        }

    var transactionMap: MutableMap<Person, Transaction> = mutableMapOf()
    var theoreticalBalanceList: MutableList<Pair<Person, Int>> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfermoney)
        initProfileRec()
    }

    private fun initProfileRec() {
        val selectPersonsRec = findViewById<RecyclerView>(R.id.MTpickUserRec)
        selectPersonsRec.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        val selectableTransferPersons = db.findAllPersonsAbleToTransfer()
        if (selectableTransferPersons == null) {
            Toast.makeText(this, "There are no profiles with negative balance", Toast.LENGTH_SHORT).show()
        }

        val selectPersonRecAdapter = TransferPersonRecAdapter(this, this, realm, selectableTransferPersons, true)
        selectPersonsRec.adapter = selectPersonRecAdapter
        selectPersonsRec.layoutManager = LinearLayoutManager(this)

        amountOfPersonsSelected = 0
        amountOfPersonsPaid = 0
        amountOfMoneyPaid = 0


        findViewById<MaterialButton>(R.id.MTselectedPersonsSaveButton).setOnClickListener {
            when {
                amountOfPersonsSelected == 0 -> {
                    Toast.makeText(this, "Er zijn geen personen geselecteerd", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                db.findRoommateWithMostTheoreticalBalanceNotInArray(selectPersonRecAdapter.chosenMap.toTypedArray()) == null -> {
                    Toast.makeText(this, "Er moet minimaal één huisgenoot niet geselecteerd zijn", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                else -> {
                    // hide selection view and show calculation view
                    findViewById<View>(R.id.MTpickUsersLayout).visibility = View.GONE
                    findViewById<View>(R.id.MTcalculationLayout).visibility = View.VISIBLE

                    calculateTransfersAndShow(selectPersonRecAdapter)
                }
            }
        }

        findViewById<MaterialButton>(R.id.MThasPaidPersonsSaveButton).setOnClickListener {
            if (amountOfPersonsPaid == 0) {
                Toast.makeText(this, "Niemand heeft geld overgemaakt", Toast.LENGTH_SHORT).show()
            }
            else {
                transactionMap.toList().forEach {
                    db.createAndSaveTransaction(it.second, huisRekeningEnabled)
                }
                Toast.makeText(this, "Totaal ${amountOfMoneyPaid.toCurrencyString()} overgemaakt door $amountOfPersonsPaid personen", Toast.LENGTH_SHORT).show()
            }
            this.finish()
        }

    }

    private fun calculateTransfersAndShow(recAdapt: TransferPersonRecAdapter) {
        val calculatedPersonsRec = findViewById<RecyclerView>(R.id.MTcalculatedPersonsRec)
        calculatedPersonsRec.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        val chosenArray = recAdapt.chosenMap.toTypedArray()
        val realmResultsSelected = db.findPersonsWithIDInArray(chosenArray)
        calculatedPersonsRec.adapter = TransferCalcPersonRecAdapter(this, realm, realmResultsSelected, true)
        calculatedPersonsRec.layoutManager = LinearLayoutManager(this)

        db.findAllRoommatesMinusInArray(chosenArray).forEach {
            theoreticalBalanceList.add(Pair(it, it.balance))
        }
    }

    fun increaseSelectedPersonsCounter(bool: Boolean) {
        if (bool) amountOfPersonsSelected++
        else amountOfPersonsSelected--
    }

    fun someonePaidSomeone(payer: Person, receiver: Person, money: Int, undo: Boolean) {

        if (!undo) {
            val product = Product.create("Overgemaakt", money, Product.BOTH_TURF_AND_BUY, 13, Product.OTHERPRODUCT)

            val transaction: Transaction = if (money > 0) {
                Transaction.createTransfer(payer, receiver, money, product)
            } else {
                Transaction.createTransfer(receiver, payer, -money, product)
            }

            db.removeProduct(product)

            transactionMap[payer] = transaction
            amountOfPersonsPaid++
            amountOfMoneyPaid += if (money >= 0) money else -money
        }
        else {
            transactionMap.remove(payer)
            amountOfPersonsPaid--

            amountOfMoneyPaid += if (money >= 0) -money else money
        }

    }
}