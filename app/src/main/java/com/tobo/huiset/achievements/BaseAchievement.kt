package com.tobo.huiset.achievements

import com.tobo.huiset.realmModels.AchievementCompletion
import com.tobo.huiset.realmModels.Person

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

    fun getNewCompletionIfCompleted(person: Person, helpData: AchievementUpdateHelpData): AchievementCompletion? {
        val completionTimeStamp = checkIfAchieved(person, helpData) ?: return null

        //NOT saved in the database yet
        return AchievementCompletion.create(id, completionTimeStamp, person.id)
    }

    fun wasAchieved(person: Person): Boolean {
        return getAchievemoment(person) != null
    }

    fun getAchievemoment(person: Person): AchievementCompletion? {
        return person.completions.find { it.achievement == this.id }
    }
}
