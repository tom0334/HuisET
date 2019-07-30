package com.tobo.huiset.Achievements

import com.tobo.huiset.realmModels.Person

object AchievementManager {

    fun getAchievements(): List<BaseAchievement>{
        return listOf(
            PilsBaas(),
            Nice(),
            CollegeWinnaar()
        )
    }

    fun updateForPerson(p:Person) = getAchievements().forEach { it.update(p) }


}