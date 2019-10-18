package com.tobo.huiset.gui.fragments.intro

import android.os.Bundle
import android.view.View
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

    private lateinit var hint:String

    override fun onSlideDismissed() {

        val editText = view!!.findViewById<TextInputEditText>(R.id.intro_name)
        val price = getPrice()
        if(HandyFunctions.priceValidate(editText.text.toString(), editText)){
            this.processPrice(price!!)
            db.createDemoBeerOrSetPrice(price)
        }
        else{
            Toast.makeText(this.context,"Prijs input klopt niet.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSlideShown() {
//        view!!.findViewById<MaterialButton>(R.id.button_intro_textfield_create).visibility = View.GONE

        val editText = view!!.findViewById<TextInputEditText>(R.id.intro_name)
        val text = getInitialPrice().toCharArray()
        editText.setText(text,0,text.size)
    }


    override fun isPolicyRespected(): Boolean {
        return getPrice() != null
    }

    override fun onUserIllegallyRequestedNextPage() {
        Toast.makeText(this.context,"Prijs input klopt niet!",Toast.LENGTH_SHORT).show()
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

    private fun getPrice(): Int? {
        val editText = view!!.findViewById<TextInputEditText>(R.id.intro_name)
        return parsePrice(editText.text.toString())
    }

    private fun parsePrice(toString: String): Int? {
        return try{
            toString.euroToCent()
        }catch (e:Exception){
            return null
        }
    }
}