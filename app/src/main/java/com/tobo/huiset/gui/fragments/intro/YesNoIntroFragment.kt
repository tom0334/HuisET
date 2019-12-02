package com.tobo.huiset.gui.fragments.intro

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.tobo.huiset.R
import com.tobo.huiset.gui.activities.PREFS_DEPOSIT_ID
import com.tobo.huiset.gui.activities.PREFS_TURF_CONFETTI_ID
import com.tobo.huiset.gui.fragments.intro.SlideFactory.ARG_DEFAULT_CHOICE
import com.tobo.huiset.gui.fragments.intro.SlideFactory.ARG_DESCRIPTION
import com.tobo.huiset.gui.fragments.intro.SlideFactory.ARG_HUISREKENING_OR_DEPOSIT
import com.tobo.huiset.gui.fragments.intro.SlideFactory.ARG_NO_TEXT
import com.tobo.huiset.gui.fragments.intro.SlideFactory.ARG_TITLE
import com.tobo.huiset.gui.fragments.intro.SlideFactory.ARG_YES_TEXT
import com.tobo.huiset.utils.HuisETDB
import io.realm.Realm
import kotlin.properties.Delegates


class YesNoIntroFragment : Fragment(), SlideDismissListener {
    private lateinit var title: String
    private lateinit var description: String
    private lateinit var yesText: String
    private lateinit var noText: String
    private var huisrekeningOrDeposit by Delegates.notNull<Boolean>()

    private var selection: Boolean = false

    override fun onSlideDismissed(){

        if (huisrekeningOrDeposit) {
            val realm = Realm.getDefaultInstance()
            val db = HuisETDB(realm)
            db.setHuisRekeningActive(selection)
            realm.close()
        }
        else {  // if it is a deposit slide
            if (selection) {
                PreferenceManager.getDefaultSharedPreferences(this.context).edit()
                    .putBoolean(PREFS_DEPOSIT_ID, selection).apply()
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            val args = arguments!!

            title = args.getString(ARG_TITLE)!!
            description = args.getString(ARG_DESCRIPTION)!!
            noText = args.getString(ARG_NO_TEXT)!!
            yesText = args.getString(ARG_YES_TEXT)!!
            selection = args.getBoolean(ARG_DEFAULT_CHOICE)
            huisrekeningOrDeposit = args.getBoolean(ARG_HUISREKENING_OR_DEPOSIT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.intro_slide_yes_no, container, false)
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
}