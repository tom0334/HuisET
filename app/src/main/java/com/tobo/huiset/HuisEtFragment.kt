package com.tobo.huiset

import androidx.fragment.app.Fragment


/**
 * Base class for Fragments. All fragments 'must' extend this class, so we can add general functionality to this
 * class and have it easily available in all activities.
 *
 * There is a similar thing for activies.
 *
 *
 * The realm field in here can be freely acessed. It is managed by the activity, so don't close it.
 */

abstract class HuisEtFragment: Fragment() {

    //ONLY WORKS WHEN THE FRAGMENT IS ATTACHED TO THE PARENT ACTIVITY!
    val realm by lazy{
        if (this.activity == null) throw Exception("HuisEtFragment: Error: tried to access realm before activity was attached!")
        (this.activity  as HuisEtActivity).realm
    }
}