package com.tobo.huiset.utils

import android.content.Context
import android.widget.Toast
import com.tobo.huiset.achievements.BaseAchievement
import com.tobo.huiset.realmModels.*
import com.tobo.huiset.utils.extensions.executeSafe
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import kotlin.math.roundToInt

class HuisETDB(val realm: Realm) {


    /**
     * Returns the highest priority product. Should be listed first in the mainactivity
     */
    fun getFirstTurfProduct(): Product? {
        return this.realm.where(Product::class.java)
            .equalTo("deleted", false)
            .`in`("kind", arrayOf(Product.KIND_TURFABLE, Product.KIND_BOTH))
            .sort("row", Sort.ASCENDING)
            .findFirst()
    }

    /**
     * Selects the highest priority product
     */
    fun selectFirstTurfProduct() = selectProduct(getFirstTurfProduct())

    /**
     * Deselects all products and changes the selection to the param supplied.
     * if null is supplied, don't select any.
     */
    fun selectProduct(productToSelect: Product?) {
        realm.executeTransaction {
            // deselect selected product
            realm.where(Product::class.java)
                .equalTo("deleted", false)
                .equalTo("selected", true)
                .sort("row", Sort.ASCENDING)
                .findAll()
                .forEach {
                    it.isSelected = false
                }
            if (productToSelect != null) {
                productToSelect.isSelected = true
            }
        }

    }

    /**
     * Gets the product that was selected in the main.
     */
    fun getSelectedProduct(): Product? {
        return realm.where(Product::class.java)
            .equalTo("deleted", false)
            .equalTo("selected", true)
            .sort("row", Sort.ASCENDING)
            .findFirst()

    }

    /**
     * Deselects all other persons in the history view, And selects the person supplied
     */
    fun selectPersonInHistory(p: Person?) {
        realm.executeTransaction {
            realm.where(Person::class.java).findAll().forEach { it.isSelectedInHistoryView = false }
            if (p != null) p.isSelectedInHistoryView = true
        }
    }

    /**
     * Adds a new transaction on the selected product
     * @param person the person to put the transaction on
     */
    fun doTransactionWithSelectedProduct(person: Person, amount: Float) {
        realm.executeSafe {
            val selectedProduct = getSelectedProduct()
            val t = Transaction.create(person, selectedProduct, amount, false)

            realm.copyToRealmOrUpdate(t)
            t.execute(realm)
        }
    }

    fun doTransactionWithMultiplePersons(persons: List<Person>, amount: Float) {
        val amountPerPerson = amount / persons.size.toFloat()
        persons.forEach {
            doTransactionWithSelectedProduct(it, amountPerPerson)
        }
    }


    /**
     * Gets the person that is selected in history
     */
    fun getSelectedPersonInHistory(): Person? {
        return realm.where(Person::class.java)
            .equalTo("selectedInHistoryView", true)
            .findFirst()
    }

    /**
     * Simply gets a product with an id
     */
    fun getProductWithId(productId: String): Product? {
        return realm.where(Product::class.java)
            .equalTo("id", productId)
            .findFirst()
    }

    /**
     * Find all products, exluding the deleted ones
     */
    fun findAllCurrentProducts(kind: Int): RealmResults<Product> {
        val query = realm.where(Product::class.java)
            .equalTo("deleted", false)

        if (kind == Product.KIND_BUYABLE) {
            query.`in`("kind", arrayOf(Product.KIND_BUYABLE, Product.KIND_BOTH))
        } else if (kind == Product.KIND_TURFABLE) {
            query.`in`("kind", arrayOf(Product.KIND_TURFABLE, Product.KIND_BOTH))
        }

        return query.sort("row", Sort.ASCENDING).findAll()
    }

    /**
     * Finds all persons that are not deleted
     */
    fun findAllCurrentPersons(includeHidden: Boolean): RealmResults<Person> {
        val query = realm.where(Person::class.java)
            .equalTo("deleted", false)
        if (!includeHidden) {
            query.equalTo("show", true)
        }
        return query.sort("row", Sort.ASCENDING).findAll()
    }

    /**
     * Finds all persons, even if they are hidden or deleted.
     */
    fun findPersonsIncludingDeletedExceptHuisrekening(): RealmResults<Person> {
        val query = realm.where(Person::class.java)
        if (this.getHuisRekening().isDeleted) {
            query.notEqualTo("huisRekening", true)
        }
        return query.sort("row", Sort.ASCENDING)
            .findAll()
    }

    /**
     * Find a person with given id. Returns null if argument is null
     */
    fun getPersonWithId(pickedPersonId: String?): Person? {
        if (pickedPersonId == null) return null
        return realm.where(Person::class.java)
            .equalTo("deleted", false)
            .equalTo("id", pickedPersonId)
            .findFirst()
    }

    /**
     * Creates a new transaction with the supplied arguments and saves it in the database.
     */
    fun createAndSaveTransaction(
        person: Person,
        product: Product,
        amount: Float,
        buy: Boolean
    ): Transaction {
        var savedTrans: Transaction? = null
        realm.executeTransaction {
            val trans = Transaction.create(person, product, amount, buy)
            savedTrans = realm.copyToRealmOrUpdate(trans)
            savedTrans!!.execute(realm)
        }
        return savedTrans!!
    }

    fun createAndSaveTransaction(transaction: Transaction): Transaction {
        var savedTrans: Transaction? = null
        realm.executeTransaction {
            savedTrans = realm.copyToRealmOrUpdate(transaction)
            transaction.execute(realm)
        }
        return savedTrans!!
    }

    /**
     * Gets a list of all transactions
     * if personId is provided, it findss any with that personid
     * if buy i provided, it gives all with that buy value
     */
    fun getTransactions(personId: String? = null, buy: Boolean? = null): List<Transaction> {
        val query = realm.where(Transaction::class.java)
        if (personId != null) {
            query.equalTo("personId", personId)
        }
        if (buy != null) {
            query.equalTo("buy", buy)
        }
        return query.findAll()
    }

    fun updateProductRows() {
        val products = this.findAllCurrentProducts(Product.KIND_BOTH)

        realm.executeTransaction {
            products.sort("row")

            var newRow = 0
            products.forEach {
                it.row = newRow++
            }
        }
    }

    fun updateProfileRows() {
        val persons = this.findAllCurrentPersons(true)
        realm.executeTransaction {
            persons.sort("row")

            var newRow = 0
            persons.forEach {
                it.row = newRow++
            }
        }
    }

    fun removeProfile(oldProfile: Person) {
        realm.executeTransaction {
            if (realm.where(Transaction::class.java)
                    .equalTo("personId", oldProfile.id)
                    .findFirst() == null
            ) {
                // Actually delete the product from the realm if it isn't involved in any transactions
                oldProfile.deleteFromRealm()
            } else {
                // fake delete product from the realm
                oldProfile.isDeleted = true
            }
        }
    }

    fun removeProduct(oldProduct: Product) {
        realm.executeSafe {
            if (realm.where(Transaction::class.java)
                    .equalTo("productId", oldProduct.id)
                    .findFirst() == null
            ) {
                // Actually delete the profile from the realm if it isn't involved in any transactions
                oldProduct.deleteFromRealm()
            } else {
                // fake delete profile from the realm
                oldProduct.isDeleted = true
            }
        }
    }

    fun getTransactionsBetween(timeStamp1: Long, timeStamp2: Long): RealmResults<Transaction> {
        return realm.where(Transaction::class.java)
            .between("time", timeStamp1, timeStamp2)
            .findAll()
    }

    fun mergeTransactionsIfPossible(tooRecentLimit: Long) {
        ///all transactions from the last 3 minutes, but don't merge ones that are too recent just yet. That could
        // be confusing for the user.
        val threeMinutesAgo = System.currentTimeMillis() - 3 * 60 * 1000
        val recentTransactions =
            getTransactionsBetween(threeMinutesAgo, tooRecentLimit).sort("time").toMutableList()


        var i = 0
        while ((i + 1) in recentTransactions.indices) {
            val first = recentTransactions[i]!!
            val other = recentTransactions[i + 1]!!
            //If productID is null, that means that its a custom turf that will never be merged
            if (first.productId != null && first.isBuy == other.isBuy && first.productId == other.productId && first.personId == other.personId) {
                realm.executeTransaction {
                    first.amount += other.amount
                    first.price += other.price
                    other.deleteFromRealmIncludingSideEffects()
                    recentTransactions.removeAt(i + 1)
                }
                //Either delete the item that was merged or go on to the next one.
                //Don't do both at the same time, because the merge result may need to be merged with the next one
            } else {
                i++
            }

        }

    }

    fun findPersonsWithIDInArray(arr: Array<String>): RealmResults<Person>? {
        return realm.where(Person::class.java)
            .`in`("id", arr)
            .findAll()
    }

    fun removeAllAchievementCompletionsForPerson(person: Person) {
        realm.executeTransaction {
            //removes all completions completely
            person.completions.createSnapshot().forEach {
                it.deleteFromRealm()
            }
            person.completions.clear()
        }
    }

    fun undoAndDeleteTransactionIncludingSideEffects(trans: Transaction) {
        realm.executeSafe {
            trans.undo(realm)
            trans.deleteFromRealmIncludingSideEffects()
        }
    }

    fun createAndSaveAchievementCompletion(
        achievement: BaseAchievement,
        completionTimeStamp: Long,
        person: Person
    ): AchievementCompletion {
        realm.beginTransaction()
        val comp = AchievementCompletion.create(achievement.id, completionTimeStamp, person.id)
        realm.copyToRealm(comp)
        person.addAchievement(comp)
        realm.commitTransaction()
        return comp
    }

    fun findAllPersonsAbleToTransfer(): RealmResults<Person>? {
        return realm.where(Person::class.java)
            .equalTo("deleted", false)
            .equalTo("guest", false)
            .or().not().`in`("balance", arrayOf(0))
            .sort("row", Sort.ASCENDING)
            .findAll()
    }

    fun findRoommateWithMostTheoreticalBalanceNotInArray(arr: Array<String>): Person? {
        return realm.where(Person::class.java)
            .equalTo("deleted", false)
            .equalTo("guest", false)
            .not().`in`("id", arr)
            .sort("balance", Sort.DESCENDING)
            .findFirst()
    }

    fun findAllRoommatesMinusInArray(chosenArray: Array<String>): List<Person> {
        val query = realm.where(Person::class.java)
            .equalTo("deleted", false)
            .equalTo("guest", false)
            .sort("row", Sort.ASCENDING)

        return query.findAll().minus(findPersonsWithIDInArray(chosenArray)!!)
    }

    fun getHuisRekening(): Person {
        return realm.where(Person::class.java).equalTo("huisRekening", true).findFirst()!!
    }

    fun setHuisRekeningActive(active: Boolean) {
        val huisRekening = getHuisRekening()
        realm.executeTransaction {
            huisRekening.isDeleted = !active
        }
    }

    fun createOrUpdateIntroPerson(
        name: String,
        guest: Boolean,
        isHuisRekening: Boolean,
        first: Boolean,
        context: Context
    ) {
        this.updateProfileRows()

        val row = if (first) -1 else findAllCurrentPersons(true).size

        val existingPerson = getFirstNonHuisrekeningPerson()
        if (existingPerson != null) {
            realm.executeTransaction {
                existingPerson.name = name
                Toast.makeText(context, "Profiel aangepast: $name", Toast.LENGTH_SHORT).show()
            }
        } else {
            realm.executeTransaction {
                val newPerson = Person.create(
                    name,
                    ProfileColors.getNextColor(this),
                    guest,
                    true,
                    row,
                    isHuisRekening
                )
                realm.copyToRealm(newPerson)
                Toast.makeText(context, "Profiel aangemaakt: $name", Toast.LENGTH_SHORT).show()
            }
        }

        this.updateProfileRows()
    }

    fun hasAtLeastOnePerson(): Boolean {
        return realm.where(Person::class.java).equalTo("huisRekening", false).findFirst() != null
    }

    fun close() {
        realm.close()
    }

    fun createDemoCrateOrSetPrice(price: Int) {
        val current = getCrateIfExists()

        if (current != null) {
            realm.executeTransaction {
                current.price = price
            }
        } else {
            realm.executeTransaction {
                val crate =
                    Product.create("Bier", price, Product.KIND_BOTH, 0, Product.SPECIES_BEER, 24)
                realm.copyToRealm(crate)
            }
            selectFirstTurfProduct()
        }

        realm.refresh()
    }

    fun getCrateIfExists(): Product? {
        return realm.where(Product::class.java).equalTo("species", Product.SPECIES_BEER)
            .equalTo("buyPerAmount", 24).findFirst()
    }

    fun copyFromRealm(trans: Transaction): Transaction {
        return realm.copyFromRealm(trans)
    }

    fun findDuplicatePersonName(name: String, zeroIfPerson_OneIfProduct: Int): Boolean {
        return if (zeroIfPerson_OneIfProduct == 0) {
            realm.where(Person::class.java)
                .equalTo("deleted", false)
                .findAll().map { it.name }
                .count { it.toLowerCase().trim() == name.toLowerCase().trim() } > 0
        } else {
            // so if (zeroIfPerson_OneIfProduct == 1)
            realm.where(Product::class.java)
                .equalTo("deleted", false)
                .findAll().map { it.name }
                .count { it.toLowerCase().trim() == name.toLowerCase().trim() } > 0
        }
    }


    fun doCustomTurf(
        price: Int,
        title: String,
        personsPaidFor: List<Person>,
        personThatPaid: Person
    ) {
        if (personsPaidFor.isEmpty()) throw IllegalArgumentException("PersonsPaidFor cannot be empty!")

        //First, create the side effect objects that contain the info about which persons
        //is paid for. (These may include the payer)
        val pricePerPersonInCents = (price.toFloat() / personsPaidFor.size.toFloat()).roundToInt()
        val transactionSideEffects = personsPaidFor.map {
            TransactionSideEffect.create(it.id, pricePerPersonInCents, false)
        }

        realm.executeTransaction {
            val paidPersonTrans =
                Transaction.create(personThatPaid, price, transactionSideEffects, title, true)
            //This also copies the side effects
            realm.copyToRealm(paidPersonTrans)

            //now update the balances of everyone
            paidPersonTrans.execute(realm)
        }
    }

    fun createProduct(
        newName: String,
        newPrice: Int,
        newKind: Int,
        newRow: Int,
        newSpecies: Int,
        newAmount: Int
    ) {
        realm.executeTransaction {
            val product = Product.create(newName, newPrice, newKind, newRow, newSpecies, newAmount)
            realm.copyToRealm(product)
        }
    }

    fun editProduct(
        product: Product,
        newName: String,
        newPrice: Int,
        newKind: Int,
        newSpecies: Int,
        newAmount: Int
    ) {
        realm.executeTransaction {
            product.name = newName
            product.price = newPrice
            product.kind = newKind
            // product.row should not change
            product.species = newSpecies
            product.buyPerAmount = newAmount
        }
    }

    fun getFirstNonHuisrekeningPerson(): Person? {
        return realm.where(Person::class.java)
            .equalTo("deleted", false)
            .notEqualTo("huisRekening", true)
            .findFirst()
    }

}

