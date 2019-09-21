package com.tobo.huiset.gui.fragments.intro

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.github.paolorotolo.appintro.ISlidePolicy
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.tobo.huiset.R
import com.tobo.huiset.gui.fragments.intro.SlideFactory.ARG_HINT


abstract class AbstractPickPriceSlide : AbstractCustomIntroSlide() , ISlidePolicy, SlideDismissListener{

    abstract fun processPrice(price:Int)
    abstract fun getInitialPrice():String

    private lateinit var hint:String

    override fun onSlideDismissed() {
        val price = getPrice()
        if(price!= null){
            this.processPrice(price)
            db.createDemoBeer(price)
        }
        else{
            Toast.makeText(this.context,"Prijs input klopt niet!", Toast.LENGTH_SHORT).show()
        }
    }


    override fun isPolicyRespected(): Boolean {
        return getPrice() != null
    }

    override fun onUserIllegallyRequestedNextPage() {
        Toast.makeText(this.context,"Prijs is geen correct bedrag!",Toast.LENGTH_SHORT).show()
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


    internal fun getPrice(): Int? {
        val editText = view!!.findViewById<TextInputEditText>(R.id.intro_name)
        return parsePrice(editText.text.toString())
    }

    private fun parsePrice(toString: String): Int? {
        //todo safely pares price, return null if error
        return 100
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialButton>(R.id.button_intro_textfield_create).visibility = View.GONE

        val editText = view.findViewById<TextInputEditText>(R.id.intro_name)
        val text = getInitialPrice().toCharArray()
        editText.setText(text,0,text.size)
    }
}