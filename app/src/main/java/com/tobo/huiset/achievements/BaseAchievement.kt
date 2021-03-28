package com.tobo.huiset.achievements

import com.tobo.huiset.realmModels.AchievementCompletion
import com.tobo.huiset.realmModels.Person
import com.tobo.huiset.utils.extensions.getDb

abstract class BaseAchievement {
    abstract val id: Int

    abstract val updateOnTurf: Boolean
    abstract val updateOnBuy: Boolean
    abstract val updateOnLaunch: Boolean

    abstract val name: String
    abstract val description: String

    //should check if
    abstract fun checkIfAchieved(
        person: Person,
        helpData: AchievementUpdateHelpData
    ): Long?

    fun update(person: Person, helpData: AchievementUpdateHelpData): AchievementCompletion? {
        //is already previously achieved
        if (wasAchieved(person)) return null

        val completionTimeStamp = checkIfAchieved(person, helpData) ?: return null

        return person.getDb().createAndSaveAchievementCompletion(this, completionTimeStamp, person)

    }

    fun wasAchieved(person: Person): Boolean {
        return getAchievemoment(person) != null
    }

    fun getAchievemoment(person: Person): AchievementCompletion? {
        return person.completions.find { it.achievement == this.id }
    }
}
