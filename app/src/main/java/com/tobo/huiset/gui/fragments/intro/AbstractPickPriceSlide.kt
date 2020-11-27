package com.tobo.huiset.gui.fragments.intro

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.github.paolorotolo.appintro.ISlidePolicy
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.tobo.huiset.R
import com.tobo.huiset.gui.fragments.intro.SlideFactory.ARG_HINT
import com.tobo.huiset.utils.HandyFunctions
import com.tobo.huiset.utils.extensions.euroToCent
import java.lang.Exception


abstract class AbstractPickPriceSlide : AbstractCustomIntroSlide(), ISlidePolicy, SlideDismissListener, SlideShowListener {

    abstract fun processPrice(price:Int)
    abstract fun getInitialPrice():String

    private lateinit var hint: String

    override fun onSlideDismissed() {
        val editText = view!!.findViewById<TextInputEditText>(R.id.intro_name)
        val price = editText.text.toString().replace(',','.')

        if(HandyFunctions.priceValidate(price, editText)){
            this.processPrice(price.euroToCent())
        }
    }

    override fun onSlideShown() {
        val editText = view!!.findViewById<TextInputEditText>(R.id.intro_name)
        val text = getInitialPrice().toCharArray()
        editText.setText(text,0,text.size)
        editText.setRawInputType(InputType.TYPE_CLASS_NUMBER)
    }

    override fun isPolicyRespected(): Boolean {
        val editText = view!!.findViewById<TextInputEditText>(R.id.intro_name)
        return HandyFunctions.priceValidate(editText.text.toString(), editText)
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
            hint = args.getString(ARG_HINT)!!
        }
    }

}