package com.tobo.huiset.gui.fragments.intro

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.tobo.huiset.R
import com.tobo.huiset.gui.activities.IntroActivity
import com.tobo.huiset.utils.HuisETDB
import io.realm.Realm


class TextFieldSlide : Fragment() {
    private lateinit var title: String
    private lateinit var description:String
    private lateinit var buttonText:String
    private lateinit var hint:String




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            val args = arguments!!

            title = args.getString(ARG_TITLE)!!
            description = args.getString(ARG_DESCRIPTION)!!
            hint = args.getString(ARG_BUTTON_TEXT)!!
            buttonText = args.getString(ARG_HINT)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.intro_slide_text_field, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.title).text = title
        view.findViewById<TextView>(R.id.description).text = description

        val createButton = view.findViewById<MaterialButton>(R.id.button_intro_textfield_create)
        val editText = view.findViewById<TextInputEditText>(R.id.intro_name)


        editText.hint = hint
        e
        createButton.setOnClickListener {
            (this.activity as IntroActivity).createPerson(editText.text.toString())
            editText.setText(charArrayOf(),0,0)
        }
    }

    companion object {
        private val ARG_TITLE = "slide_title"
        private val ARG_DESCRIPTION = "slide_description"
        private val ARG_HINT ="slide_hint"
        private val ARG_BUTTON_TEXT = "slide_createButtonText"

        fun newInstance(
            title: String,
            description: String,
            buttonText:String,
            hint:String
        ): TextFieldSlide {
            val sampleSlide = TextFieldSlide()

            val args = Bundle()

            args.putString(ARG_TITLE, title)
            args.putString(ARG_DESCRIPTION,description)
            args.putString(ARG_BUTTON_TEXT,buttonText)
            args.putString(ARG_HINT,hint)

            sampleSlide.arguments = args
            return sampleSlide
        }
    }
}