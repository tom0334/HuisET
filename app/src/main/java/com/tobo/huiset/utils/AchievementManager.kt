package com.tobo.huiset.utils

import com.tobo.huiset.realmModels.AchievementCompletion
import com.tobo.huiset.realmModels.Person
import com.tobo.huiset.realmModels.Transaction
import java.text.SimpleDateFormat
import java.util.*


public val A_PILSBAAS = 1
public val A_NICE = 2



object AchievementManager {

    public fun getAchvievementsForPerson(p: Person): List<Achievement>{
        return listOf(
            PilsBaas(p)
        )
    }

    public fun update(p:Person) = getAchvievementsForPerson(p).forEach { it.update() }


    abstract class Achievement(val person: Person){
        abstract val id:Int
        abstract val name:String
        abstract val description:String
        abstract fun isAchievedNow():Boolean //returns if achieved now

        fun update(){
            if(wasAchieved()) return
            if(isAchievedNow()){
                person.realm.executeTransaction {
                    val comp = AchievementCompletion.create(this.id,System.currentTimeMillis(),person.id)
                    it.copyToRealm(comp)
                    person.addAchievement(comp)
                }
            }
        }

        fun wasAchieved():Boolean{
            return person.completions.find { it.achievement == this.id } != null
        }
    }


    class PilsBaas(person: Person) : Achievement(person) {
        override val id = A_PILSBAAS
        override val name = "Pilsbaas"
        override val description = "Drink 10 of meer pils op een dag"
        override fun isAchievedNow(): Boolean {
            val realm = person.realm

            val transactions = realm.where(Transaction::class.java)
                .equalTo("personId", person.id)
                .findAll()

            val maxBeerOnADay = transactions
                .filter { it.getProduct(person.realm).isBeer  }
                .groupBy { SimpleDateFormat("yyyy-MM-dd")
                    .format(Date(it.time)) }
                .values.maxBy { it.size }?.size

            return maxBeerOnADay != null && maxBeerOnADay > 8

        }
    }



}