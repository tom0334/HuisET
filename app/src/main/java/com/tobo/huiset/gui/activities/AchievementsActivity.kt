package com.tobo.huiset.gui.activities

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tobo.huiset.R
import com.tobo.huiset.extendables.HuisEtActivity
import com.tobo.huiset.gui.adapters.AchievementsAdapter
import com.tobo.huiset.utils.AchievementManager

class AchievementsActivity : HuisEtActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievements)
        setupRec()
    }


    private fun setupRec(){
        val rec = findViewById<RecyclerView>(R.id.acievementsRec)
        rec.adapter = AchievementsAdapter(AchievementManager.getAchvievements(), this)
        rec.layoutManager = GridLayoutManager(this,2)
    }
}