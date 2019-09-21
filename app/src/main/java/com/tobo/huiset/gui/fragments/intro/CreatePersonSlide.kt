package com.tobo.huiset.gui.fragments.intro

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.github.paolorotolo.appintro.ISlidePolicy
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.tobo.huiset.R
import com.tobo.huiset.gui.activities.IntroActivity
import com.tobo.huiset.gui.fragments.intro.SlideFactory.ARG_BUTTON_TEXT
import com.tobo.huiset.gui.fragments.intro.SlideFactory.ARG_HINT


class CreatePersonSlide : AbstractCustomIntroSlide() , ISlidePolicy{

    private lateinit var buttonText:String
    private lateinit var hint:String


    override fun isPolicyRespected(): Boolean {
        return db.hasAtLeastOnePerson()
    }

    override fun onUserIllegallyRequestedNextPage() {
        Toast.makeText(this.context,"Maak minstens 1 profiel!",Toast.LENGTH_SHORT).show()
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val createButton = view.findViewById<MaterialButton>(R.id.button_intro_textfield_create)
        val editText = view.findViewById<TextInputEditText>(R.id.intro_name)

        editText.hint = hint

        //todo Check input of user here. Same as used in editprofile
        createButton.setOnClickListener {
            (this.activity as IntroActivity).createPerson(editText.text.toString())
            editText.setText(charArrayOf(),0,0)
        }
    }
}