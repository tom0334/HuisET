package com.tobo.huiset.gui.activities

import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tobo.huiset.R
import com.tobo.huiset.extendables.HuisEtActivity
import com.tobo.huiset.gui.adapters.TransferPersonRecAdapter

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
        val pickUserRec = findViewById<RecyclerView>(R.id.MTpickUserRec)
        pickUserRec.addItemDecoration(DividerItemDecoration(pickUserRec.context, DividerItemDecoration.VERTICAL))

        val profiles = db.findAllCurrentPersons(true)

        pickUserRec.adapter = TransferPersonRecAdapter(this, this, realm, profiles, true)
        pickUserRec.layoutManager = LinearLayoutManager(this)

        amountOfPersonsSelected = 0
    }

    fun increaseCounter(b: Boolean) {
        if (b) amountOfPersonsSelected++
        else amountOfPersonsSelected--
    }

}