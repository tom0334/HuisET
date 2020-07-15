package com.tobo.huiset.gui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tobo.huiset.R
import com.tobo.huiset.gui.adapters.TurfRecAdapter
import com.tobo.huiset.realmModels.Person
import com.tobo.huiset.utils.HuisETDB
import io.realm.Realm

class CustomTurfDialogFragment : DialogFragment(), TurfRecAdapter.TurfHandler {

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.custom_turf_dialog_frag, container,false)
    }

    lateinit var db: HuisETDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.db = HuisETDB(Realm.getDefaultInstance())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rec = view.findViewById<RecyclerView>(R.id.customturfRec)

        val adapter = TurfRecAdapter(this.context!!,db.findAllCurrentPersons(true),false,db.realm,this)
        adapter.selecting = true
        rec.adapter = adapter
        rec.layoutManager = GridLayoutManager(this.context,2)
    }

    override fun handleSingleTurf(person: Person) {
    }

    override fun onSelectionChanged(selecting: Boolean) {

    }

}
