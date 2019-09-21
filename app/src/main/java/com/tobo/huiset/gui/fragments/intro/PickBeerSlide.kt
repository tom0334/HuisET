package com.tobo.huiset.gui.fragments.intro

import android.widget.Toast
import com.tobo.huiset.realmModels.Product

class PickBeerSlide :AbstractPickPriceSlide(){
    override fun getInitialPrice(): String {
        val crate = db.getCrateIfExists()

        val beerPrice = if(crate!=null){
            crate.price.toFloat() / 24f
        }else{
            Product.STANDARD_PRICE_BEER
        }
        return  "%.2f".format(beerPrice)
    }

    override fun processPrice(price: Int) {
        db.createDemoBeer(price)
    }
}