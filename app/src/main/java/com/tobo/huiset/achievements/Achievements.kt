package com.tobo.huiset.achievements

import com.tobo.huiset.realmModels.Person
import com.tobo.huiset.realmModels.Product
import com.tobo.huiset.realmModels.Transaction
import com.tobo.huiset.utils.ToboTime
import java.text.SimpleDateFormat
import java.util.*


/**
 * To Add a new achievement, 3 things need to be done:
 * 1-> create UNIQUE const int to be used as ID in database
 * 2-> make implementation of achievement class
 * 3-> add it to the list of all achievements in the achievement manager
 *
 */
//Achievements are numbered to be saved in the database.
const val A_PILSBAAS = 1
const val A_NICE = 2
const val A_MVP = 3
const val A_GROTE_BOODSCHAP = 4
//const val A_REPARATIE_PILSJE = 5
const val A_COLLEGE_WINNAAR = 6

const val A_DOE_HET_VOOR_DE_KONING = 9

const val A_OKTOBERFEST = 11
const val A_INHAALSLAG = 12
const val A_BEGINNENDE_DRINKER = 13



/**
 * Finds the amount of products turfed in a list of transactions, even when the amount of products in a transaction
 * is greater than 1.
 */
fun List<Transaction>.amountOfProducts(): Int {
    return this.map {it.amount }.sum()
}


class PilsBaas : BaseAchievement() {
    override val id = A_PILSBAAS
    override val name = "Pilsbaas"
    override val description = "Drink 10 of meer pils op een dag"
    override fun isAchievedNow(person: Person, helpData: AchievementUpdateHelpData): Boolean {

        val maxBeerOnADay = helpData.beerTurfTransactions
            .groupBy {
                SimpleDateFormat("yyyy-MM-dd").format(Date(it.time))
            }
            .values.map { it.amountOfProducts() }.max()

        return maxBeerOnADay != null && maxBeerOnADay > 8

    }
}

class Nice : BaseAchievement() {
    override val id = A_NICE
    override val name = "Nice"
    override val description = "Drink totaal 69 bier."
    override fun isAchievedNow(person: Person, helpData: AchievementUpdateHelpData): Boolean {
        val totalBeer = helpData.beerTurfTransactions.amountOfProducts()
        return totalBeer >= 69
    }
}

class CollegeWinnaar : BaseAchievement(){
    override val id = A_COLLEGE_WINNAAR
    override val name = "Collegewinnaar"
    override val description = "Drink een biertje op een doordeweekse dag voor 8:45. Telt vanaf 6 uur 's ochtends."

    override fun isAchievedNow(person: Person, helpData: AchievementUpdateHelpData): Boolean {
        val sixOClock = ToboTime(6, 0, 0)
        val collegeStartTime = ToboTime(8, 45, 0)

        val collegeBeers = helpData.beerTurfTransactions
            .filter { it.toboTime.isWeekDay() && it.toboTime.timeOfDayBetween(sixOClock,collegeStartTime)}

        return collegeBeers.isNotEmpty()
    }

}

class MVP: BaseAchievement() {
    override val id = A_MVP
    override val name = "MVP (Most Valuable Pilser)"
    override val description = "Drink het meeste bier van de avond. Avond eindigt om 6 uur s'ochtends, daarna wordt de MVP pas bepaald. Minstens 5 bier, anders verdien je het niet."

    override fun isAchievedNow(person: Person,helpData: AchievementUpdateHelpData): Boolean {

        val perDay = helpData.beerTurfTransactions
            .filter { it.toboTime.zuipDayHasEnded() } // this achievement can only be decided if the day has ended
            .groupBy { it.toboTime.getZuipDay() }


        for ((day, transactionsOnDay) in perDay) {
            val amountOfBeersOnDay = transactionsOnDay
                .groupBy { it.personId }
                //entry.value is a list of transactions
                .mapValues { entry -> entry.value.amountOfProducts() }

            val pair = amountOfBeersOnDay.maxBy { it.value }!!

            val mvpID = pair.key
            val amount = pair.value


            //someone else is the mvp
            if (mvpID != person.id) continue
            //its a tie! There are multiple people that drank the same amount. No winner then.
            if(amountOfBeersOnDay.values.count { it == amount} > 1 ) continue


            if (amount > 5) return true
        }
        return false
    }
}

class GroteBoodschap: BaseAchievement(){
    override val id = A_GROTE_BOODSCHAP
    override val name = "Grote boodschap"
    override val description ="Koop 2 kratjes in een keer in. Haha nummer 2."

    override fun isAchievedNow(person: Person, helpData: AchievementUpdateHelpData): Boolean {
        val realm = person.realm

        val crateBuys = realm.where(Transaction::class.java)
            .equalTo("personId", person.id)
            .equalTo("buy",true)
            .findAll()
            .filter { it.getProduct(realm).species == Product.CRATEPRODUCT }

        return crateBuys.find { buy -> buy.amount >= 2} != null

//        //200 IQ groupBy right here. It splits all transactions up in 60 second windows.
//        return  crateBuys.groupBy { it.time / 60000 }
//            .values.find { it.size >= 2 } != null
    }

}

//class ReparatieBiertje :BaseAchievement(){
//    override val id = A_REPARATIE_PILSJE
//    override val name = "Reparatie Biertje"
//    override val description = "Drink een biertje voor 12 uur s'ochtends, als je de vorige avond minimaal 10 bier hebt gedronken."
//
//    override fun isAchievedNow(person: Person,helpData: AchievementUpdateHelpData): Boolean {
//
//        val drankEnoughDays = helpData.beerTurfTransactions.groupBy { it.toboTime.getZuipDay() }
//            .filter { entry -> entry.value.size > 10 }
//            .map{ entry -> entry.value[0].toboTime}
//
//        val morningBeers = helpData.beerTurfTransactions.filter { it.toboTime.hour < 12 }
//
//        for (drinkDay in drankEnoughDays){
//            val repair= morningBeers.find { mb ->  mb.toboTime.is1DayLaterThan(drinkDay)  }
//            if(repair != null) return true
//        }
//        return false
//    }
//
//}

class DoeHetVoorDeKoning : BaseAchievement() {
    override val id = A_DOE_HET_VOOR_DE_KONING
    override val name = "Doe het voor de koning"
    override val description = "Drink een biertje op koningsdag. Op Prins Pils!"

    override fun isAchievedNow(person: Person, helpData: AchievementUpdateHelpData): Boolean {
        val kingsDayBeer = helpData.beerTurfTransactions
            .find { it.toboTime.dayOfMonth == 27 && it.toboTime.month == Calendar.APRIL}

        return kingsDayBeer != null
    }

}

class Oktoberfest: BaseAchievement() {
    override val id: Int
        get() = A_OKTOBERFEST
    override val name: String
        get() = "Oktoberfest"
    override val description: String
        get() = "Drink een biertje in oktober. Bonuspunten als je een lederhose/dirndl draagt."

    override fun isAchievedNow(person: Person, helpData: AchievementUpdateHelpData): Boolean {
        val octoberBeer = helpData.beerTurfTransactions
            .find { it.toboTime.month == Calendar.OCTOBER }

        return octoberBeer != null
    }
}

class Inhaalslag: BaseAchievement() {
    override val id: Int
        get() = A_INHAALSLAG
    override val name: String
        get() = "Inhaalslag"
    override val description: String
        get() = "turf 5 bier in 1 keer."

    override fun isAchievedNow(person: Person, helpData: AchievementUpdateHelpData): Boolean {
        val fiveOrMoreBeer = helpData.beerTurfTransactions
            .find { it.amount >= 5 }

        return fiveOrMoreBeer != null
    }
}

class BeginnendeDrinker: BaseAchievement() {
    override val id: Int
        get() = A_BEGINNENDE_DRINKER
    override val name: String
        get() = "Beginnende Drinker"
    override val description: String
        get() = "Turf je eerste biertje."

    override fun isAchievedNow(person: Person, helpData: AchievementUpdateHelpData): Boolean {
        return helpData.beerTurfTransactions.isNotEmpty()
    }
}



object AchievementManager {

    fun getAchievements(): List<BaseAchievement>{
        return listOf(
            BeginnendeDrinker(),
            PilsBaas(),
            Inhaalslag(),
            MVP(),
            Nice(),
            CollegeWinnaar(),
//            ReparatieBiertje(),
            GroteBoodschap(),
            DoeHetVoorDeKoning(),
            Oktoberfest()
        )
    }

    fun updateForPerson(person:Person){

        for (a in getAchievements()) {
            val turfTrans = person.realm.where(Transaction::class.java)
                .equalTo("buy",false)
                .equalTo("personId", person.id)
                .findAll()

            val beerTransactions = turfTrans
                .filter { it.getProduct(person.realm).species == Product.BEERPRODUCT }

            val helpData = AchievementUpdateHelpData(turfTrans,beerTransactions)

            a.update(person,helpData)
        }
    }
}

data class AchievementUpdateHelpData(
    val turfTransactions:List<Transaction>,
    val beerTurfTransactions:List<Transaction>
)

