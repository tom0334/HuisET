package com.tobo.huiset.gui.fragments.intro

import com.tobo.huiset.utils.extensions.toNumberDecimal

class PickCratePriceSlide : AbstractPickPriceSlide() {
    override fun getInitialPrice(): String {
        val crate = db.getCrateIfExists()
        if (crate != null) {
            return crate.price.toNumberDecimal()
        }
        return "10.50"
    }

    override fun processPrice(price: Int) {
        db.createDemoCrateOrSetPrice(price)
    }
}