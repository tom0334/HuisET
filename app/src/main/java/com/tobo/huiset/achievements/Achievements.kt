package com.tobo.huiset.achievements

import com.tobo.huiset.realmModels.AchievementCompletion
import com.tobo.huiset.realmModels.Person
import com.tobo.huiset.realmModels.Product
import com.tobo.huiset.realmModels.Transaction
import com.tobo.huiset.utils.ToboTime
import io.realm.Realm
import java.util.*


/**
 * To Add a new achievement, 3 things need to be done:
 * 1-> craete UNIQUE const int to be used as ID in database
 * 2-> make implementation of achievement class
 * 3-> add it to the list of all achievements in the achievement manager
 *
 */
//Achievements are numbered to be saved in the database.
const val A_PILSBAAS = 1
const val A_NICE = 2
const val A_MVP = 3
const val A_GROTE_BOODSCHAP = 4
const val A_REPARATIE_PILSJE = 5
const val A_COLLEGE_WINNAAR = 6

const val A_DOE_HET_VOOR_DE_KONING = 9


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
    override fun checkIfAchieved(person: Person, helpData: AchievementUpdateHelpData): Long? {

        val maxBeerOnADay = helpData.beerTurfTransactionsByPerson
            .groupBy {it.toboTime.toboDay}

        // a map entry for each day. Find one that meets our needs
        val goodDayEntry = maxBeerOnADay.entries.find { it.value.size >= 10 }

        if(goodDayEntry != null) return goodDayEntry.value.last().time
        return null
    }
}

class Nice : BaseAchievement() {
    override val id = A_NICE
    override val name = "Nice"
    override val description = "Drink 69 bier."
    override fun checkIfAchieved(person: Person, helpData: AchievementUpdateHelpData): Long? {
        var count = 0
        for (t in helpData.beerTurfTransactionsByPerson){
            count+= t.amount
            if(count>= 69 ) return t.time
        }
        return null
    }
}

class CollegeWinnaar : BaseAchievement(){
    override val id = A_COLLEGE_WINNAAR
    override val name = "Collegewinnaar"
    override val description = "Drink een biertje op een doordeweekse dag voor 8:45. Telt vanaf 6 uur s'ochtends."

    override fun checkIfAchieved(person: Person, helpData: AchievementUpdateHelpData): Long? {
        val sixOClock = ToboTime(6, 0, 0)
        val collegeStartTime = ToboTime(8, 45, 0)

        val collegeBeer = helpData.beerTurfTransactionsByPerson
            .find{ it.toboTime.isWeekDay() && it.toboTime.timeOfDayBetween(sixOClock,collegeStartTime)}

        if(collegeBeer!= null) return collegeBeer.time
        return null
    }
}

class MVP: BaseAchievement() {
    override val id = A_MVP
    override val name = "MVP (Most Valuable Pilser"
    override val description = "Drink het meeste bier van de avond. Avond eindigt om 6 uur s'ochtends, daarna wordt pas de MVP bepaald. Minstens 5 bier, anders verdien je het niet."

    override fun checkIfAchieved(person: Person, helpData: AchievementUpdateHelpData): Long? {

        val perDay = helpData.AllBeerTurfTransactions
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

            //return the last
            if (amount > 5) return transactionsOnDay.last().time
        }
        return null
    }
}

class GroteBoodschap: BaseAchievement(){
    override val id = A_GROTE_BOODSCHAP
    override val name = "Grote boodschap"
    override val description ="Koop 2 kratjes in een keer in. Haha nummer 2."

    override fun checkIfAchieved(person: Person, helpData: AchievementUpdateHelpData): Long? {
        val realm = person.realm

        val crateBuys = realm.where(Transaction::class.java)
            .equalTo("personId", person.id)
            .equalTo("buy",true)
            .findAll()
            .filter { it.getProduct(realm).species == Product.CRATEPRODUCT }

        val doubleCreateBuy = crateBuys.find { it.amount >= 2 }

        if( doubleCreateBuy != null) return  doubleCreateBuy.time
        return null

//        //200 IQ groupBy right here. It splits all transactions up in 60 second windows.
//        return  crateBuys.groupBy { it.time / 60000 }
//            .values.find { it.size >= 2 } != null
    }

}

class ReparatieBiertje :BaseAchievement(){
    override val id = A_REPARATIE_PILSJE
    override val name = "Reparatie Biertje"
    override val description = "Drink een biertje voor 12 uur s'ochtends, als je de vorige avond minimaal 10 bier hebt gedronken.\n"

    override fun checkIfAchieved(person: Person, helpData: AchievementUpdateHelpData): Long? {

        val drankEnoughDays = helpData.beerTurfTransactionsByPerson.groupBy { it.toboTime.getZuipDay() }
            .filter { entry -> entry.value.size > 10 }
            .map{ entry -> entry.value[0].toboTime}

        val morningBeers = helpData.beerTurfTransactionsByPerson.filter { it.toboTime.hour < 12 }

        for (drinkDay in drankEnoughDays){
            val repair =morningBeers.find { mb ->  mb.toboTime.is1DayLaterThan(drinkDay)  }
            if(repair != null) return repair.time
        }
        return null
    }

}

class DoeHetVoorDeKoning : BaseAchievement() {
    override val id = A_DOE_HET_VOOR_DE_KONING
    override val name = "Doe het voor de koning"
    override val description = "Drink een biertje op koningsdag. Op Prins Pils!"

    override fun checkIfAchieved(person: Person, helpData: AchievementUpdateHelpData): Long? {
        val kingsDayBeer = helpData.beerTurfTransactionsByPerson
            .find { it.toboTime.dayOfMonth == 27 && it.toboTime.month == Calendar.APRIL}

        if(kingsDayBeer != null) return kingsDayBeer.time
        return null
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

    /**
     * Updates all achievements for a perseon. Returns a list of completions if new ones exist
     */
    fun updateForPerson(person:Person):List<AchievementCompletion>{

        val completions = mutableListOf<AchievementCompletion>()
        for (a in getAchievements()) {
            val allTurfTrans = person.realm.where(Transaction::class.java)
                .equalTo("buy",false)
                .findAll()

            val allBeerTrans = allTurfTrans
                .filter { it.getProduct(person.realm).species == Product.BEERPRODUCT }

            val helpData = AchievementUpdateHelpData(
                allTurfTransactions = allTurfTrans,
                AllBeerTurfTransactions = allBeerTrans,
                turfTransactionsByPerson = allTurfTrans.filter { it.personId == person.id },
                beerTurfTransactionsByPerson = allBeerTrans.filter { it.personId == person.id }
            )

            val completion = a.update(person,helpData)
            if(completion!= null){
                completions.add(completion)
            }

        }
        return completions.toList()
    }

    fun getAchievementForCompletion(completion: AchievementCompletion): BaseAchievement {
        return getAchievements().find {completion.achievement == it.id}!!
    }

    fun checkAgainForPerson(person:Person): List<AchievementCompletion> {
        val realm = person.realm
        val before = person.completions.map { realm.copyFromRealm(it)  }

        realm.executeTransaction {
            //removes all completions completely
            person.completions.createSnapshot().forEach {
                it.deleteFromRealm()
            }
            person.completions.clear()
        }

        //finds them back
        val after = updateForPerson(person)

        return after.minus(before)
    }

}

data class AchievementUpdateHelpData(
    val allTurfTransactions:List<Transaction>,
    val AllBeerTurfTransactions:List<Transaction>,
    val turfTransactionsByPerson:List<Transaction>,
    val beerTurfTransactionsByPerson:List<Transaction>
)

