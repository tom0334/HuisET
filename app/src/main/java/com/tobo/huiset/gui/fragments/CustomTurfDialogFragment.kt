package com.tobo.huiset.gui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tobo.huiset.R
import com.tobo.huiset.gui.adapters.TurfRecAdapter
import com.tobo.huiset.realmModels.Person
import com.tobo.huiset.utils.HandyFunctions
import com.tobo.huiset.utils.HuisETDB
import io.realm.Realm

class CustomTurfDialogFragment : DialogFragment(), TurfRecAdapter.TurfHandler {

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.custom_turf_dialog_frag, container,false)
    }

    lateinit var db: HuisETDB
    lateinit var adapter:TurfRecAdapter
    lateinit var pickedPersonId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.db = HuisETDB(Realm.getDefaultInstance())
        try {
            this.pickedPersonId = arguments!!.getString(PARAM_PICKED_PERSON_ID)!!
        }catch (e: Exception){
            e.printStackTrace()
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val priceEd = view.findViewById<EditText>(R.id.customTurfPriceED)
        HandyFunctions.addPriceTextLimiter(priceEd)

        val nameEd = view.findViewById<EditText>(R.id.customTurfTitle)

        view.findViewById<Button>(R.id.customTurfOkButton).setOnClickListener {
            val priceOk = HandyFunctions.priceValidate(priceEd.text.toString(),priceEd)
            if(!priceOk){
                Toast.makeText(this.context,"Bedrag klopt niet",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val price = priceEd.text.toString().toFloat()

            val title = nameEd.text.toString()
            if(title.length == 0){
                Toast.makeText(this.context,"Naam uitgave mag niet leeg zijn",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val selectedPersons = adapter.selectedPersonIds.map {  db.getPersonWithId(it)}.filterNotNull()
            if(selectedPersons.isEmpty()){
                Toast.makeText(this.context,"Selecter minstens 1 persoon",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val payingPerson = db.getPersonWithId(pickedPersonId)!!
            db.doCustomTurf(price,title,selectedPersons,payingPerson)
        }

        initRec(view)
    }

    private fun initRec(view: View){
        val rec = view.findViewById<RecyclerView>(R.id.customturfRec)
        adapter = TurfRecAdapter(this.context!!,db.findAllCurrentPersons(true),false,db.realm,this)
        adapter.setAlwaysSelecting(true)
        rec.adapter = adapter
        rec.layoutManager = GridLayoutManager(this.context,2)
    }

    //Don't do anything, single turf is not possible in this dialog, because selection is always on.
    override fun handleSingleTurf(person: Person) {
    }

    override fun onSelectionChanged(selecting: Boolean) {
    }

    companion object{
        private const val PARAM_PICKED_PERSON_ID = "PICKED_PERSON_ID"

        fun newInstance(pickedPersonId: String) = CustomTurfDialogFragment().apply {
            arguments = Bundle(1).apply {
                putString(PARAM_PICKED_PERSON_ID,pickedPersonId)
            }
        }
    }

}
