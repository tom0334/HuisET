package com.tobo.huiset.gui.fragments.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.tobo.huiset.R
import com.tobo.huiset.gui.fragments.intro.SlideFactory.ARG_DEFAULT_CHOICE
import com.tobo.huiset.gui.fragments.intro.SlideFactory.ARG_DESCRIPTION
import com.tobo.huiset.gui.fragments.intro.SlideFactory.ARG_NO_TEXT
import com.tobo.huiset.gui.fragments.intro.SlideFactory.ARG_TITLE
import com.tobo.huiset.gui.fragments.intro.SlideFactory.ARG_YES_TEXT
import com.tobo.huiset.utils.HuisETDB
import io.realm.Realm


class YesNoIntroFragment : Fragment(), SlideDismissListener {
    private lateinit var title: String
    private lateinit var description: String
    private lateinit var yesText: String
    private lateinit var noText: String

    private var selection: Boolean = false

    override fun onSlideDismissed() {
        val realm = Realm.getDefaultInstance()
        val db = HuisETDB(realm)
        db.setHuisRekeningActive(selection)
        realm.close()
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
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
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