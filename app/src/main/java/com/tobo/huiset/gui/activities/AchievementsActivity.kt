package com.tobo.huiset.gui.activities

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tobo.huiset.R
import com.tobo.huiset.extendables.HuisEtActivity
import com.tobo.huiset.gui.adapters.AchievementsAdapter
import com.tobo.huiset.Achievements.AchievementManager
import com.tobo.huiset.utils.extensions.toPixel
import f.tom.consistentspacingdecoration.ConsistentSpacingDecoration

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
        rec.adapter = AchievementsAdapter(AchievementManager.getAchievements(),persons, this)

        val displayMetrics = resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density

        val columns = when{
            dpWidth > 1200 -> 4 // large 10 inch tablets in landscape
            dpWidth > 900 -> 3
            dpWidth > 600 -> 2 // 7 inch tablet in portrait
            else -> 1
        }
        rec.layoutManager = GridLayoutManager(this,columns)
        val spacing = ConsistentSpacingDecoration(16.toPixel(this),16.toPixel(this),columns)
        rec.addItemDecoration(spacing)
    }
}