package com.tobo.huiset.gui.fragments.intro

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.github.paolorotolo.appintro.ISlidePolicy
import com.google.android.material.textfield.TextInputEditText
import com.tobo.huiset.R
import com.tobo.huiset.gui.activities.IntroActivity
import com.tobo.huiset.gui.fragments.intro.SlideFactory.ARG_BUTTON_TEXT
import com.tobo.huiset.gui.fragments.intro.SlideFactory.ARG_HINT
import com.tobo.huiset.realmModels.Person
import com.tobo.huiset.utils.HandyFunctions


class CreatePersonSlide : AbstractCustomIntroSlide(), ISlidePolicy, SlideDismissListener, SlideShowListener {

    private lateinit var buttonText:String
    private lateinit var hint:String

    override fun isPolicyRespected(): Boolean {
        val editText = view!!.findViewById<TextInputEditText>(R.id.intro_name)
        return HandyFunctions.nameValidate(editText.text.toString(), editText, db, true, 0)
    }

    override fun onUserIllegallyRequestedNextPage() {
        Toast.makeText(this.context,"Voer een naam in.",Toast.LENGTH_SHORT).show()
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

        if(HandyFunctions.nameValidate(name, editText, db, true, 0)) {
            (this.activity as IntroActivity).createPerson(name)
            editText.setText("")
        }
        else{
            Toast.makeText(this.context,"Er is nog geen naam ingevoerd.", Toast.LENGTH_SHORT).show()
        }
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