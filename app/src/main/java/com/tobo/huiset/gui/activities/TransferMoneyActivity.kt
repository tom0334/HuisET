package com.tobo.huiset.gui.activities

import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tobo.huiset.R
import com.tobo.huiset.extendables.HuisEtActivity
import com.tobo.huiset.gui.adapters.TransferPersonRecAdapter

class TransferMoneyActivity : HuisEtActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfermoney)
        initProfileRec()
    }

    private fun initProfileRec() {
        val pickUserRec = findViewById<RecyclerView>(R.id.MTpickUserRec)
        pickUserRec.addItemDecoration(DividerItemDecoration(pickUserRec.context, DividerItemDecoration.VERTICAL))

        val profiles = db.findAllCurrentPersons(true)

        pickUserRec.adapter = TransferPersonRecAdapter(this, realm, profiles, true)
        pickUserRec.layoutManager = LinearLayoutManager(this)

    }

}