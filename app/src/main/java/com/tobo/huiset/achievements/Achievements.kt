package com.tobo.huiset.achievements

import com.tobo.huiset.realmModels.AchievementCompletion
import com.tobo.huiset.realmModels.Person
import com.tobo.huiset.realmModels.Product
import com.tobo.huiset.realmModels.Transaction
import com.tobo.huiset.utils.ToboTime
import com.tobo.huiset.utils.extensions.getDb
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

    override val updateOnTurf = true
    override val updateOnBuy = false
    override val updateOnLaunch = false

    override fun checkIfAchieved(person: Person, helpData: AchievementUpdateHelpData): Long? {

        val maxBeerOnADay = helpData.beerTurfTransactionsByPerson
            .groupBy {it.toboTime.toboDay}

        // a map entry for each day. Find one that meets our needs
        val goodDayEntry = maxBeerOnADay.entries.find { it.value.amountOfProducts() >= 10}

        if(goodDayEntry != null) return goodDayEntry.value.last().time
        return null
    }
}

class Nice : BaseAchievement() {
    override val id = A_NICE
    override val name = "Nice"
    override val description = "Drink 69 bier."

    override val updateOnTurf = true
    override val updateOnBuy = false
    override val updateOnLaunch = false

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

    override val updateOnTurf = true
    override val updateOnBuy = false
    override val updateOnLaunch = false

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

    override val updateOnTurf = false
    override val updateOnBuy = false
    override val updateOnLaunch = true

    override fun checkIfAchieved(person: Person, helpData: AchievementUpdateHelpData): Long? {

        val perDay = helpData.allBeerTurfTrans
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

    override val updateOnTurf = false
    override val updateOnBuy = true
    override val updateOnLaunch = false

    override fun checkIfAchieved(person: Person, helpData: AchievementUpdateHelpData): Long? {
        val crateBuys = person.getDb().getTransactions(personId = person.id, buy = true)
            .filter { it.product.species == Product.CRATEPRODUCT }

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

    override val updateOnTurf = true
    override val updateOnBuy = false
    override val updateOnLaunch = false

    override fun checkIfAchieved(person: Person, helpData: AchievementUpdateHelpData): Long? {

        val drankEnoughDays = helpData.beerTurfTransactionsByPerson.groupBy { it.toboTime.getZuipDay() }
            .filter { entry -> entry.value.amountOfProducts() > 10 }
            .map{ entry -> entry.value[0].toboTime}

        val morningBeers = helpData.beerTurfTransactionsByPerson.filter { it.toboTime.hour < 12 }

        for (drinkDay in drankEnoughDays){
            val repair = morningBeers.find { mb ->  mb.toboTime.is1DayLaterThan(drinkDay)  }
            if(repair != null) return repair.time
        }
        return null
    }

}

class DoeHetVoorDeKoning : BaseAchievement() {
    override val id = A_DOE_HET_VOOR_DE_KONING
    override val name = "Doe het voor de koning"
    override val description = "Drink een biertje op koningsdag. Op Prins Pils!"

    override val updateOnTurf = true
    override val updateOnBuy = false
    override val updateOnLaunch = false

    override fun checkIfAchieved(person: Person, helpData: AchievementUpdateHelpData): Long? {
        val kingsDayBeer = helpData.beerTurfTransactionsByPerson
            .find { it.toboTime.dayOfMonth == 27 && it.toboTime.month == Calendar.APRIL}

        if(kingsDayBeer != null) return kingsDayBeer.time
        return null
    }

}

object AchievementManager {

    fun getAllAchievements(): List<BaseAchievement>{
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
    fun updateAllForPerson(person:Person):List<AchievementCompletion>{
        return updateForPerson(getAllAchievements(), person)
    }

    private fun updateForPerson(achievementsToUpdate:List<BaseAchievement>, person:Person): List<AchievementCompletion> {
        val completions = mutableListOf<AchievementCompletion>()

        val helpData = AchievementUpdateHelpData(person)

        for (a in achievementsToUpdate) {
            val completion = a.update(person,helpData)
            if(completion!= null){
                completions.add(completion)
            }
        }
        return completions.toList()
    }

    fun updateAchievementsAfterLaunch(person: Person): List<AchievementCompletion> {
        return updateForPerson(getAllAchievements().filter { it.updateOnLaunch},person)
    }

    fun updateAchievementsAfterTurf(person: Person): List<AchievementCompletion> {
        return updateForPerson(getAllAchievements().filter { it.updateOnTurf},person)
    }

    fun updateAchievementsAfterBuy(person: Person): List<AchievementCompletion> {
       return updateForPerson(getAllAchievements().filter { it.updateOnBuy },person)
    }

    fun getAchievementForCompletion(completion: AchievementCompletion): BaseAchievement {
        return getAllAchievements().find {completion.achievement == it.id}!!
    }

    fun checkAgainForPerson(person:Person): List<AchievementCompletion> {
        val beforeIds = person.completions.map { it.achievement }

        person.getDb().removeAllAchievementCompletionsForPerson(person)

        //finds them back
        val after = updateAllForPerson(person)
        val afterIds = after.map { it.achievement }

        //Minus does not work as expected on a list of achivementcompletions. Has something to do with equals i think.
        //This works around that, just look for equal ids and then find the completion object back
        val newIds = afterIds.minus(beforeIds)

        return after.filter { newIds.contains(it.achievement) }
    }

}

data class AchievementUpdateHelpData(private val person:Person){

    val allTurfTrans by lazy {
        person.getDb().getTransactions(buy = false)
    }

    val allBeerTurfTrans by lazy {
        allTurfTrans.filter { it.product != null && it.product.species == Product.BEERPRODUCT }
    }

    val turfTransactionsByPerson by lazy {
        allTurfTrans.filter { it.personId == person.id }
    }

    val beerTurfTransactionsByPerson by lazy {
        allBeerTurfTrans.filter { it.personId == person.id }
    }

}

