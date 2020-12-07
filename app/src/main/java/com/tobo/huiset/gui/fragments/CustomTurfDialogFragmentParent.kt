package com.tobo.huiset.gui.fragments

/**
 * Implemented by activies or fragments that show the customTurfFragment.
 * Used to make the purchasefragment reset to the default view where it asks who purchased
 * instead of the screen that comes after that.
 */
interface CustomTurfDialogFragmentParent {
    fun onCustomTurfDone(personId: String, price: Int)
}