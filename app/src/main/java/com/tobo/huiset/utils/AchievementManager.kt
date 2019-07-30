package com.tobo.huiset.utils

import com.tobo.huiset.realmModels.AchievementCompletion
import com.tobo.huiset.realmModels.Person
import com.tobo.huiset.realmModels.Transaction
import java.text.SimpleDateFormat
import java.util.*


public val A_PILSBAAS = 1
public val A_NICE = 2

abstract class Achievement(){
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


class PilsBaas : Achievement() {
    override val id = A_PILSBAAS
    override val name = "Pilsbaas"
    override val description = "Drink 10 of meer pils op een dag"
    override fun isAchievedNow(person:Person): Boolean {
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

class Nice : Achievement() {
    override val id = A_NICE
    override val name = "Nice"
    override val description = "Drink 69 of 420 bier"
    override fun isAchievedNow(person: Person): Boolean {
        val realm = person.realm

        val transactions = realm.where(Transaction::class.java)
            .equalTo("personId", person.id)
            .findAll()

        val totalBeer = transactions
            .filter { it.getProduct(person.realm).isBeer  }.size

        return totalBeer >= 69
    }
}


object AchievementManager {

    public fun getAchievements(): List<Achievement>{
        return listOf(
            PilsBaas(),
            Nice()
        )
    }

    public fun updateForPerson(p:Person) = getAchievements().forEach { it.update(p) }


}