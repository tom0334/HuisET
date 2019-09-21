package com.tobo.huiset.gui.fragments.intro

import com.tobo.huiset.realmModels.Product
import com.tobo.huiset.utils.extensions.toNumberDecimal

class PickBeerSlide :AbstractPickPriceSlide(){
    override fun getInitialPrice(): String {
        val crate = db.getCrateIfExists()

        if(crate!=null){
            return (crate.price / 24).toNumberDecimal()
        }else{
            return Product.STANDARD_PRICE_BEER.toNumberDecimal()
        }
    }

    override fun processPrice(price: Int) {
        db.createDemoBeerOrSetPrice(price)
    }
}