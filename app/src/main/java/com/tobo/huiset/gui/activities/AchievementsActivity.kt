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

        db.findAllCurrentPersons(true).forEach { AchievementManager.updateForPerson(it) }
        setupRec()
    }


    private fun setupRec(){
        val persons = db.findAllCurrentPersons(includeHidden = false)

        val rec = findViewById<RecyclerView>(R.id.acievementsRec)
        rec.adapter = AchievementsAdapter(AchievementManager.getAchvievements(),persons, this)
        rec.layoutManager = GridLayoutManager(this,2)
    }
}