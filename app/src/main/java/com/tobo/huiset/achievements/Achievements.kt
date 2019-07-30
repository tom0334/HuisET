package com.tobo.huiset.achievements

import com.tobo.huiset.realmModels.Person
import com.tobo.huiset.realmModels.Transaction
import com.tobo.huiset.utils.ToboTime
import java.text.SimpleDateFormat
import java.util.*


/**
 * To Add a new achievement, 3 things need to be done:
 * 1-> craete UNIQUE const int to be used as ID in database
 * 2-> make implementation of achievement class
 * 3-> add it to the list of all achievements in the achievement manager
 *
 */

const val A_PILSBAAS = 1
const val A_NICE = 2
const val COLLEGE_WINNAAR = 5

class PilsBaas : BaseAchievement() {
    override val id = A_PILSBAAS
    override val name = "Pilsbaas"
    override val description = "Drink 10 of meer pils op een dag"
    override fun isAchievedNow(person: Person): Boolean {
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

class Nice : BaseAchievement() {
    override val id = A_NICE
    override val name = "Nice"
    override val description = "Drink 69 bier"
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

class CollegeWinnaar : BaseAchievement(){
    override val id = COLLEGE_WINNAAR
    override val name = "Collegewinnaar"
    override val description = "Drink een biertje op een doordeweekse dag voor 8:45.Telt vanaf 6 uur s'ochtends."

    override fun isAchievedNow(person: Person): Boolean {
        val realm = person.realm

        val transactions = realm.where(Transaction::class.java)
            .equalTo("personId", person.id)
            .findAll()

        val fiveOClock = ToboTime(6, 0, 0)
        val collegeStartTime = ToboTime(8, 45, 0)

        val collegeBeers = transactions
            .filter { it.getProduct(person.realm).isBeer  }
            .filter { it.toboTime.isWeekDay() && it.toboTime.timeOfDayBetween(fiveOClock,collegeStartTime)}

        return collegeBeers.size > 0
    }

}

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