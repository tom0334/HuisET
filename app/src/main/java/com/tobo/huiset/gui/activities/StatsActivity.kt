package com.tobo.huiset.gui.activities

import android.os.Bundle
import com.tobo.huiset.R
import com.tobo.huiset.extendables.HuisEtActivity


class StatsActivity : HuisEtActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)
    }
}