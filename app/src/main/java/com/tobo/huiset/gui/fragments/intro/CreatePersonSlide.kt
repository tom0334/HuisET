package com.tobo.huiset.gui.fragments.intro

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.github.paolorotolo.appintro.ISlidePolicy
import com.google.android.material.textfield.TextInputEditText
import com.tobo.huiset.R
import com.tobo.huiset.gui.fragments.intro.SlideFactory.ARG_BUTTON_TEXT
import com.tobo.huiset.gui.fragments.intro.SlideFactory.ARG_HINT
import com.tobo.huiset.utils.HandyFunctions
import com.tobo.huiset.utils.HuisETDB
import io.realm.Realm


class CreatePersonSlide : AbstractCustomIntroSlide(), ISlidePolicy, SlideDismissListener,
    SlideShowListener {

    private lateinit var buttonText: String
    private lateinit var hint: String

    override fun isPolicyRespected(): Boolean {
        val editText = view!!.findViewById<TextInputEditText>(R.id.intro_name)
        return HandyFunctions.nameValidate(editText.text.toString(), editText, db, true, 0)
    }

    override fun onUserIllegallyRequestedNextPage() {
        val editText = view!!.findViewById<TextInputEditText>(R.id.intro_name)
        Toast.makeText(this.context, editText.error, Toast.LENGTH_SHORT).show()
    }


    override fun getLayoutResId(): Int {
        return R.layout.intro_slide_text_field
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            val args = arguments!!
            buttonText = args.getString(ARG_BUTTON_TEXT)!!
            hint = args.getString(ARG_HINT)!!
        }
    }

    override fun onSlideDismissed() {

        val editText = view!!.findViewById<TextInputEditText>(R.id.intro_name)
        val name = editText.text.toString()

        // name validation is done in isPolicyRespected()
        val realm = Realm.getDefaultInstance()
        val db = HuisETDB(realm)
        db.createOrUpdateIntroPerson(name, false, false, false, context!!)

        editText.setText("")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editText = view.findViewById<TextInputEditText>(R.id.intro_name)

        editText.hint = hint
    }

    override fun onSlideShown() {
        val editText = view!!.findViewById<TextInputEditText>(R.id.intro_name)
        editText.requestFocus()
    }

}