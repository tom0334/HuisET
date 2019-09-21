package com.tobo.huiset.gui.fragments.intro

import android.widget.Toast

class PickCratePriceSlide :AbstractPickPriceSlide(){
    override fun getInitialPrice(): String {
        return "11.00"
    }

    override fun processPrice(price: Int) {
        db.createDemoCrate(price)
    }
}