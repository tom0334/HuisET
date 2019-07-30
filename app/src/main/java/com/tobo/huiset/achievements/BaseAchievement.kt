package com.tobo.huiset.achievements

import com.tobo.huiset.realmModels.AchievementCompletion
import com.tobo.huiset.realmModels.Person

abstract class BaseAchievement {
    abstract val id:Int
    abstract val name:String
    abstract val description:String
    abstract fun isAchievedNow(person: Person):Boolean //returns if achieved now

    fun update(person: Person){
        if(wasAchieved(person)) return
        if(isAchievedNow(person)){
            person.realm.executeTransaction {
                val comp = AchievementCompletion.create(this.id,System.currentTimeMillis(),person.id)
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
