package com.tobo.huiset.extendables


import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm

/**
 * Base class for activities. All activities 'must' extend this class, so we can add general functionality to this
 * class and have it easily available in all activities.
 *
 * There is a similar thing for fragments.
 *
 * Currently has a realm field that can be accessed, without ever having to close it, since this class manages that.
 */

abstract class HuisEtActivity : AppCompatActivity() {

    //by lazy only creates it when needed, then keeps the same object
    val realm: Realm by lazy { Realm.getDefaultInstance() }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    /**
     * Override this to add a custom margin to snackbars.
     */
    open fun getSnackbarBottomMargin(): Int{
        return 0
    }

}