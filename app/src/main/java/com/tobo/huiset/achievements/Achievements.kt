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
const val A_SNACK_KONING = 7
const val A_BEGINNENDE_SNACKER = 8
const val A_DOE_HET_VOOR_DE_KONING = 9
const val A_BEGINNENDE_DRINKER = 10
const val A_OKTOBERFEST = 11
const val A_INHAALSLAG = 12


/**
 * Finds the amount of products turfed in a list of transactions, even when the amount of products in a transaction
 * is greater than 1.
 */
fun List<Transaction>.amountOfProducts(): Float {
    return this.map { it.amount }.sum()
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
            .groupBy { it.toboTime.getZuipDay() }

        // a map entry for each day. Find one that meets our needs
        val goodDayEntry = maxBeerOnADay.entries.find { it.value.amountOfProducts() >= 10 }

        if (goodDayEntry != null) return goodDayEntry.value.last().time
        return null
    }
}

class Nice : BaseAchievement() {
    override val id = A_NICE
    override val name = "Nice"
    override val description = "Drink 69 bier in totaal."

    override val updateOnTurf = true
    override val updateOnBuy = false
    override val updateOnLaunch = false

    override fun checkIfAchieved(person: Person, helpData: AchievementUpdateHelpData): Long? {
        var count = 0.0
        for (t in helpData.beerTurfTransactionsByPerson) {
            count += t.amount
            if (count >= 69) return t.time
        }
        return null
    }
}

class CollegeWinnaar : BaseAchievement() {
    override val id = A_COLLEGE_WINNAAR
    override val name = "Collegewinnaar"
    override val description =
        "Drink een biertje op een doordeweekse dag voor 8:45. Telt vanaf 6 uur 's ochtends."

    override val updateOnTurf = true
    override val updateOnBuy = false
    override val updateOnLaunch = false

    override fun checkIfAchieved(person: Person, helpData: AchievementUpdateHelpData): Long? {
        val sixOClock = ToboTime(6, 0, 0)
        val collegeStartTime = ToboTime(8, 45, 0)

        val collegeBeer = helpData.beerTurfTransactionsByPerson
            .find {
                it.toboTime.isWeekDay() && it.toboTime.timeOfDayBetween(
                    sixOClock,
                    collegeStartTime
                )
            }

        if (collegeBeer != null) return collegeBeer.time
        return null
    }
}

class MVP : BaseAchievement() {
    override val id = A_MVP
    override val name = "MVP (Most Valuable Pilser)"
    override val description =
        "Drink het meeste bier van de avond. De avond eindigt om 6 uur 's ochtends, daarna wordt de MVP bepaald. tussen 5-10 bier is er 1 winnaar, bij 10+ bier kan het winnaarsschap gedeeld worden."

    override val updateOnTurf = false
    override val updateOnBuy = false
    override val updateOnLaunch = true

    override fun checkIfAchieved(person: Person, helpData: AchievementUpdateHelpData): Long? {

        val perDay = helpData.allBeerTurfTrans
            .filter { it.toboTime.zuipDayHasEnded() } // this achievement can only be decided if the day has ended
            .groupBy { it.toboTime.getZuipDay() }


        for ((_, transactionsOnDay) in perDay) {
            val amountOfBeersOnDay = transactionsOnDay
                .groupBy { it.personId }
                //entry.value is a list of transactions
                .mapValues { entry -> entry.value.amountOfProducts() }

            val ownAmount = amountOfBeersOnDay[person.id] ?: continue
            val mostBeers = amountOfBeersOnDay.maxBy { it.value }!!.value

            // this person has not drunk the most beers last night
            if (amountOfBeersOnDay[person.id] != mostBeers) continue

            // too little beer drunk
            if (ownAmount < 5)
                continue
            else if (ownAmount < 10) {
                // Multiple people drank the same amount under 10 beers. No winner then.
                if (amountOfBeersOnDay.values.count { it == ownAmount } > 1) continue
            }

            //return
            return transactionsOnDay.last().time
        }
        return null
    }
}

class GroteBoodschap : BaseAchievement() {
    override val id = A_GROTE_BOODSCHAP
    override val name = "Grote boodschap"
    override val description = "Koop 2 dezelfde kratjes in een keer in."

    override val updateOnTurf = false
    override val updateOnBuy = true
    override val updateOnLaunch = false

    override fun checkIfAchieved(person: Person, helpData: AchievementUpdateHelpData): Long? {
        val crateBuys = person.getDb().getTransactions(personId = person.id, buy = true)
            .filter { it.product?.species == Product.SPECIES_BEER }
            .filter { it.product?.buyPerAmount == 24 }

        val doubleCreateBuy = crateBuys.find { it.amount >= 48 }

        if (doubleCreateBuy != null) return doubleCreateBuy.time
        return null
    }

}

class ReparatieBiertje : BaseAchievement() {
    override val id = A_REPARATIE_PILSJE
    override val name = "Reparatie Biertje"
    override val description =
        "Drink een biertje voor 12 uur 's ochtends, als je de vorige avond minimaal 10 bier hebt gedronken.\n"

    override val updateOnTurf = true
    override val updateOnBuy = false
    override val updateOnLaunch = false

    override fun checkIfAchieved(person: Person, helpData: AchievementUpdateHelpData): Long? {

        val drankEnoughDays =
            helpData.beerTurfTransactionsByPerson.groupBy { it.toboTime.getZuipDay() }
                .filter { entry -> entry.value.amountOfProducts() > 10 }
                .map { entry -> entry.value[0].toboTime }

        val morningBeers = helpData.beerTurfTransactionsByPerson.filter { it.toboTime.hour in 7..11 }

        for (drinkDay in drankEnoughDays) {
            val repair = morningBeers.find { mb -> mb.toboTime.is1DayLaterThan(drinkDay) }
            if (repair != null) return repair.time
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
            .find { it.toboTime.dayOfMonth == 27 && it.toboTime.month == Calendar.APRIL }

        if (kingsDayBeer != null) return kingsDayBeer.time
        return null
    }

}


class SnackKoning : BaseAchievement() {
    override val id = A_SNACK_KONING
    override val updateOnTurf = true
    override val updateOnBuy = false
    override val updateOnLaunch = false
    override val name = "SnackKoning"
    override val description = "Turf minstens 5 snacks op 1 dag."
    override fun checkIfAchieved(person: Person, helpData: AchievementUpdateHelpData): Long? {
        val snacksForPerson =
            helpData.allTurfTrans.filter { it.product?.species == Product.SPECIES_SNACK && it.personId == person.id }

        val moreThan3OnADay =
            snacksForPerson.groupBy { it.toboTime.getZuipDay() }.values.find { it.amountOfProducts() > 5 }
                ?: return null

        return moreThan3OnADay.last().time

    }

}

class BeginnendeDrinker : BaseAchievement() {
    override val id = A_BEGINNENDE_DRINKER
    override val updateOnTurf = true
    override val updateOnBuy = false
    override val updateOnLaunch = false
    override val name = "Beginnende drinker"
    override val description = "Turf je eerste biertje!"
    override fun checkIfAchieved(person: Person, helpData: AchievementUpdateHelpData): Long? {
        val firstBeer = helpData.allBeerTurfTrans.find { it.personId == person.id } ?: return null

        return firstBeer.time
    }
}


class BeginnendeSnacker : BaseAchievement() {
    override val id = A_BEGINNENDE_SNACKER
    override val updateOnTurf = true
    override val updateOnBuy = false
    override val updateOnLaunch = false
    override val name = "Beginnende snacker"
    override val description = "Turf je eerste snack!"
    override fun checkIfAchieved(person: Person, helpData: AchievementUpdateHelpData): Long? {
        val firstSnack = helpData.allTurfTrans
            .find { it.product?.species == Product.SPECIES_SNACK && it.personId == person.id }
            ?: return null

        return firstSnack.time
    }

}

class Oktoberfest : BaseAchievement() {
    override val updateOnTurf: Boolean = true
    override val updateOnBuy: Boolean = false
    override val updateOnLaunch: Boolean = false
    override val id: Int = A_OKTOBERFEST
    override val name: String = "Oktoberfest"
    override val description: String =
        "Drink een biertje in oktober. Bonuspunten als je een lederhose/dirndl draagt."

    override fun checkIfAchieved(person: Person, helpData: AchievementUpdateHelpData): Long? {
        val octoberBeer = helpData.beerTurfTransactionsByPerson
            .find { it.toboTime.month == Calendar.OCTOBER }
        return octoberBeer?.time
    }
}

class Inhaalslag : BaseAchievement() {
    override val updateOnTurf: Boolean = true
    override val updateOnBuy: Boolean = false
    override val updateOnLaunch: Boolean = false

    override val id: Int = A_INHAALSLAG

    override val name: String = "Inhaalslag"
    override val description: String = "turf 5 bier in 1 keer."

    override fun checkIfAchieved(person: Person, helpData: AchievementUpdateHelpData): Long? {
        //200 IQ groupBy right here. It splits all transactions up in 60 second windows.
        val groupOfFiveOrMoreBeer =
            helpData.beerTurfTransactionsByPerson.groupBy { it.time / 60000 }
                .values.find { it.amountOfProducts() >= 5 }

        return groupOfFiveOrMoreBeer?.firstOrNull()?.time
    }

}


object AchievementManager {

    fun getAllAchievements(): List<BaseAchievement> {
        return listOf(
            BeginnendeDrinker(),
            BeginnendeSnacker(),
            Inhaalslag(),
            PilsBaas(),
            SnackKoning(),
            ReparatieBiertje(),
            Nice(),
            CollegeWinnaar(),
            MVP(),
            GroteBoodschap(),
            Oktoberfest(),
            DoeHetVoorDeKoning()
        )
    }

    /**
     * Updates all achievements for a perseon. Returns a list of completions if new ones exist
     */
    fun updateAllForPerson(person: Person): List<AchievementCompletion> {
        return updateForPerson(getAllAchievements(), person)
    }

    private fun updateForPerson(
        achievementsToUpdate: List<BaseAchievement>,
        person: Person
    ): List<AchievementCompletion> {
        if (person.isHuisRekening) {
            return emptyList()
        }
        val completions = mutableListOf<AchievementCompletion>()

        val helpData = AchievementUpdateHelpData(person)

        for (a in achievementsToUpdate) {
            val completion = a.update(person, helpData)
            if (completion != null) {
                completions.add(completion)
            }
        }
        return completions.toList()
    }

    fun updateAchievementsAfterLaunch(person: Person): List<AchievementCompletion> {
        return updateForPerson(getAllAchievements().filter { it.updateOnLaunch }, person)
    }

    fun updateAchievementsAfterTurf(person: Person): List<AchievementCompletion> {
        return updateForPerson(getAllAchievements().filter { it.updateOnTurf }, person)
    }

    fun updateAchievementsAfterBuy(person: Person): List<AchievementCompletion> {
        return updateForPerson(getAllAchievements().filter { it.updateOnBuy }, person)
    }

    fun getAchievementForCompletion(completion: AchievementCompletion): BaseAchievement {
        return getAllAchievements().find { completion.achievement == it.id }!!
    }

    fun checkAgainForPerson(person: Person): List<AchievementCompletion> {
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

data class AchievementUpdateHelpData(private val person: Person) {

    val allTurfTrans by lazy {
        person.getDb().getTransactions(buy = false)
    }

    val allBeerTurfTrans by lazy {
        allTurfTrans.filter { it.product?.species == Product.SPECIES_BEER }
    }

    val turfTransactionsByPerson by lazy {
        allTurfTrans.filter { it.personId == person.id }
    }

    val beerTurfTransactionsByPerson by lazy {
        allBeerTurfTrans.filter { it.personId == person.id }
    }

}

