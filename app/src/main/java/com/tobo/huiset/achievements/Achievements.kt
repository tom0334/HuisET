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
const val A_MVP = 3
const val A_GROTE_BOODSCHAP = 4
const val A_REPARATIE_PILSJE = 5
const val A_COLLEGE_WINNAAR = 6

const val A_DOE_HET_VOOR_DE_KONING = 9


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
    override val description = "Drink 69 bier."
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
    override val id = A_COLLEGE_WINNAAR
    override val name = "Collegewinnaar"
    override val description = "Drink een biertje op een doordeweekse dag voor 8:45. Telt vanaf 6 uur s'ochtends."

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

class MVP: BaseAchievement() {
    override val id = A_MVP
    override val name = "MVP (Most Valuable Pilser"
    override val description = "Drink het meeste bier van de avond. Avond eindigt om 6 uur s'ochtends, daarna wordt pas de MVP bepaald. Minstens 5 bier, anders verdien je het niet."

    override fun isAchievedNow(person: Person): Boolean {
        val realm = person.realm

        val perDay = realm.where(Transaction::class.java)
            .findAll()
            .filter { it.toboTime.zuipDayHasEnded() }
            .groupBy { it.toboTime.getZuipDay() }


        for ((day, transactionsOnDay) in perDay) {
            val amountOftransactionsOnDay = transactionsOnDay
                .groupBy { it.personId }
                .mapValues { it.value.count() }

            val pair = amountOftransactionsOnDay.maxBy { it.value }!!

            val mvpID = pair.key
            val amount = pair.value


            //someone else is the mvp
            if (mvpID != person.id) continue
            //its a tie! There are multiple people that drank the same amount. No winner then.
            if(amountOftransactionsOnDay.values.count { it == amount} > 1 ) continue

            if (amount > 5) return true
        }
        return false
    }
}

class GroteBoodschap: BaseAchievement(){
    override val id = A_GROTE_BOODSCHAP
    override val name = "Grote boodschap"
    override val description ="Koop 2 kratjes in een keer in. Haha nummer 2."

    override fun isAchievedNow(person: Person): Boolean {
        val realm = person.realm

        val crateBuys = realm.where(Transaction::class.java)
            .equalTo("personId", person.id)
            .equalTo("buy",true)
            .findAll()
            .filter { it.getProduct(realm).isCrate }


        //200 IQ groupBy right here. It splits all transactions up in 60 second windows.
        return  crateBuys.groupBy { it.time / 60000 }
            .values.find { it.size >= 2 } != null
    }

}

class ReparatieBiertje :BaseAchievement(){
    override val id = A_REPARATIE_PILSJE
    override val name = "Reparatie Biertje"
    override val description = "Drink een biertje voor 12 uur s'ochtends, als je de vorige avond minimaal 10 bier hebt gedronken.\n"

    override fun isAchievedNow(person: Person): Boolean {
        val realm = person.realm

        val allBeerTransactions = realm.where(Transaction::class.java)
            .equalTo("personId", person.id)
            .findAll()
            .filter { it.getProduct(person.realm).isBeer  }

        val drankEnoughDays = allBeerTransactions.groupBy { it.toboTime.getZuipDay() }
            .filter { entry -> entry.value.size > 10 }
            .map{ entry -> entry.value[0].toboTime}


        val morningBeers = allBeerTransactions.filter { it.toboTime.hour < 12 }

        for (drinkDay in drankEnoughDays){
            val repair =morningBeers.find { mb ->  mb.toboTime.is1DayLaterThan(drinkDay)  }
            if(repair != null) return true
        }
        return false
    }

}

class DoeHetVoorDeKoning : BaseAchievement() {
    override val id = A_DOE_HET_VOOR_DE_KONING
    override val name = "Doe het voor de koning"
    override val description = "Drink een biertje op koningsdag. Op Prins Pils!"

    override fun isAchievedNow(person: Person): Boolean {
        val realm = person.realm
        val kingsDayBeer =  realm.where(Transaction::class.java)
            .equalTo("personId", person.id)
            .findAll()
            .find { it.toboTime.dayOfMonth == 27 && it.toboTime.month == Calendar.APRIL}

        return kingsDayBeer != null
    }

}

object AchievementManager {

    fun getAchievements(): List<BaseAchievement>{
        return listOf(
            PilsBaas(),
            ReparatieBiertje(),
            Nice(),
            CollegeWinnaar(),
            MVP(),
            GroteBoodschap(),
            DoeHetVoorDeKoning()
        )
    }

    fun updateForPerson(p:Person) = getAchievements().forEach { it.update(p) }
}