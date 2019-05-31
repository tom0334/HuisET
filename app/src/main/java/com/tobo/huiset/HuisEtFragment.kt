package com.tobo.huiset

import androidx.fragment.app.Fragment

abstract class HuisEtFragment: Fragment() {

    val realm by lazy{
        if (this.activity == null) throw Exception("HuisEtFragment: Error: tried to access realm before activity was attached!")
        (this.activity  as HuisEtActivity).realm
    }
}