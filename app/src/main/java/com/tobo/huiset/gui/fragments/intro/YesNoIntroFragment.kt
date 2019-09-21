package com.tobo.huiset.gui.fragments.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.tobo.huiset.R
import com.tobo.huiset.utils.HuisETDB
import io.realm.Realm


class YesNoIntroFragment : Fragment(), SlideDismissListener {
    private var layoutResId: Int = 0
    private lateinit var title: String
    private lateinit var description:String
    private lateinit var yesText:String
    private lateinit var noText:String

    private var selection:Boolean = false

    override fun onSlideDismissed(){
        val realm = Realm.getDefaultInstance()
        val db = HuisETDB(realm)
        db.setHuisRekeningActive(selection)
        realm.close()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            val args = arguments!!

            layoutResId = args.getInt(ARG_LAYOUT_RES_ID)
            title = args.getString(ARG_TITLE)!!
            description = args.getString(ARG_DESCRIPTION)!!
            noText = args.getString(ARG_NO_TEXT)!!
            yesText = args.getString(ARG_YES_TEXT)!!
            selection = args.getBoolean(ARG_DEFAULT_CHOICE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutResId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.title).text = title
        view.findViewById<TextView>(R.id.description).text = description

        val yes = view.findViewById<RadioButton>(R.id.intro_slide_option_yes)
        val no = view.findViewById<RadioButton>(R.id.intro_slide_option_no)
        yes.text = yesText
        no.text = noText

        yes.setOnClickListener {
            selection = true
        }
        no.setOnClickListener {
            selection = false
        }

    }

    companion object {

        private val ARG_LAYOUT_RES_ID = "layoutResId"
        private val ARG_TITLE = "slide_title"
        private val ARG_DESCRIPTION = "slide_description"
        private val ARG_YES_TEXT = "yes_text"
        private val ARG_NO_TEXT = "no_text"
        private val ARG_DEFAULT_CHOICE = "def_choice"

        fun newInstance(
            layoutResId: Int,
            title: String,
            description: String,
            yesText: String,
            noText: String,
            defaultChoice: Boolean
        ): YesNoIntroFragment {
            val sampleSlide = YesNoIntroFragment()

            val args = Bundle()
            args.putInt(ARG_LAYOUT_RES_ID, layoutResId)
            args.putString(ARG_TITLE, title)
            args.putString(ARG_DESCRIPTION,description)
            args.putString(ARG_YES_TEXT,yesText)
            args.putString(ARG_NO_TEXT,noText)
            args.putBoolean(ARG_DEFAULT_CHOICE,defaultChoice)
            sampleSlide.arguments = args
            return sampleSlide
        }
    }
}