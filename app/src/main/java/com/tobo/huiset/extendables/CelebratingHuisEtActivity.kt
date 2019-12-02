package com.tobo.huiset.extendables

import android.graphics.Color
import android.widget.Toast
import com.tobo.huiset.R
import com.tobo.huiset.achievements.AchievementManager
import com.tobo.huiset.realmModels.AchievementCompletion
import com.tobo.huiset.utils.extensions.getDisplayHeight
import com.tobo.huiset.utils.extensions.getDisplayWith
import nl.dionsegijn.konfetti.KonfettiView
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size

abstract class CelebratingHuisEtActivity: HuisEtActivity(){

    fun showAchievements(changed: List<AchievementCompletion>) {
        changed.forEach {
            val a = AchievementManager.getAchievementForCompletion(it)
            Toast.makeText(this, "Achievement unlocked door ${it.getPerson(realm).name}:\n\"${a.name}\"", Toast.LENGTH_LONG).show()
        }
        if (changed.size > 0) {

            //this is more aggressive confetti than the other one
            val viewKonfetti = findViewById<KonfettiView>(R.id.viewKonfetti)
            viewKonfetti.build()
                .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                .setDirection(0.0, 359.0)
                .setSpeed(5f, 10f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.CIRCLE)
                .addSizes(Size(12))
                .setPosition(getDisplayWith() / 2f, getDisplayHeight()/ 2f)
                .burst(300)
        }
    }
}