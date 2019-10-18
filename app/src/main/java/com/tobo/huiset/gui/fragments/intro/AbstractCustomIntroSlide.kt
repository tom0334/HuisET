package com.tobo.huiset.gui.fragments.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.tobo.huiset.R
import com.tobo.huiset.gui.fragments.intro.SlideFactory.ARG_DESCRIPTION
import com.tobo.huiset.gui.fragments.intro.SlideFactory.ARG_TITLE
import com.tobo.huiset.utils.HuisETDB
import io.realm.Realm


abstract class AbstractCustomIntroSlide : Fragment() {

    private lateinit var title: String
    private lateinit var description:String

    lateinit var db:HuisETDB

    abstract fun getLayoutResId():Int


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = HuisETDB(Realm.getDefaultInstance())

        if (arguments != null) {
            val args = arguments!!

            title = args.getString(ARG_TITLE)!!
            description = args.getString(ARG_DESCRIPTION)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate( getLayoutResId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.title).text = title
        view.findViewById<TextView>(R.id.description).text = description

    }

    override fun onDestroy() {
        super.onDestroy()
        db.close()
    }
}