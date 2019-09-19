package com.tobo.huiset.achievements

import com.tobo.huiset.realmModels.AchievementCompletion
import com.tobo.huiset.realmModels.Person

abstract class BaseAchievement {
    abstract val id:Int
    abstract val name:String
    abstract val description:String
    //should check if
    abstract fun checkIfAchieved(
        person: Person,
        helpData: AchievementUpdateHelpData
    ):Long?

    fun update(person: Person, helpData: AchievementUpdateHelpData){
        if(wasAchieved(person)) return

        val completionTimeStamp = checkIfAchieved(person,helpData)

        if(completionTimeStamp != null){
            person.realm.executeTransaction {
                val comp = AchievementCompletion.create(this.id,completionTimeStamp,person.id)
                it.copyToRealm(comp)
                person.addAchievement(comp)
            }
        }
    }

    fun wasAchieved(person: Person):Boolean{
        return getAchievemoment(person) != null
    }

    fun getAchievemoment(person: Person): AchievementCompletion? {
        return person.completions.find { it.achievement == this.id }
    }
}
