package com.tobo.huiset.gui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
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

        val ed = view.findViewById<EditText>(R.id.customTurfPriceED)
        HandyFunctions.addPriceTextLimiter(ed)

        view.findViewById<Button>(R.id.customTurfOkButton).setOnClickListener {
            val price = ed.text.toString()
            val priceOk = HandyFunctions.priceValidate(ed.text.toString(),ed)
            if(!priceOk){return@setOnClickListener}


            val title = "PlaceHolder name"
            val selectedPersons = adapter.selectedPersonIds.map {  db.getPersonWithId(it)}.filterNotNull()
            val payingPerson = db.getPersonWithId(pickedPersonId)!!
            db.doCustomTurf(price.toFloat(),title,selectedPersons,payingPerson)
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

    override fun handleSingleTurf(person: Person) {
//        val selectedPersons = adapter.selectedPersonIds.size
//
//        val amountPerPerson = 0
//        db.doTransactionWithSelectedProduct(person,amountPerPerson.toFloat())
//
//        val changed = AchievementManager.updateAchievementsAfterTurf(person)
//        (activity as MainActivity).showAchievements(changed)
//
//
//        db.selectFirstTurfProduct()
//
//        //amountAdapter.resetAmountToFirst()
//
//        //scroll to the top, because the item is added at the top
//        //transActionRec.scrollToPosition(0)
//        //mergeTransactionsHandler.postDelayed(mergeTransactionsRunnable, 30 * 1000)

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
