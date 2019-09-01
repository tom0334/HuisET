package com.tobo.huiset.utils

import com.tobo.huiset.realmModels.Person
import com.tobo.huiset.realmModels.Product
import com.tobo.huiset.realmModels.Transaction
import com.tobo.huiset.utils.extensions.executeSafe
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort

class HuisETDB(private val realm: Realm) {


    /**
     * Returns the highest priority product. Should be listed first in the mainactivity
     */
    fun getFirstTurfProduct(): Product? {
        return this.realm.where(Product::class.java)
            .equalTo("deleted", false)
            .`in`("kind", arrayOf(Product.ONLY_TURFABLE, Product.BOTH_TURF_AND_BUY))
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
    fun selectProduct(productToSelect: Product?){
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
    fun getSelectedProduct():Product?{
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
    fun doTransactionWithSelectedProduct(person: Person, amount: Int) {
        realm.executeSafe {
            val selectedProduct = getSelectedProduct()
            val t = Transaction.create(person, selectedProduct, amount, false)
            selectedProduct?.isSelected = false

            realm.copyToRealmOrUpdate(t)
            person.addTransaction(t)
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

        if (kind == Product.ONLY_BUYABLE) {
            query.`in`("kind", arrayOf(Product.ONLY_BUYABLE, Product.BOTH_TURF_AND_BUY))
        }
        else if (kind == Product.ONLY_TURFABLE) {
            query.`in`("kind", arrayOf(Product.ONLY_TURFABLE, Product.BOTH_TURF_AND_BUY))
        }

        return query.sort("row", Sort.ASCENDING).findAll()
    }

    /**
     * Finds all persons that are not deleted
     */
    fun findAllCurrentPersons(includeHidden: Boolean = false): RealmResults<Person> {
        val query = realm.where(Person::class.java)
            .equalTo("deleted", false)
        if (! includeHidden){
            query.equalTo("show",true)
        }
        return query.sort("row", Sort.ASCENDING).findAll()
    }

    /**
     * Finds all persons, even if they are hidden or deleted.
     */
    fun findPersonsIncludingDeleted(): RealmResults<Person> {
        return realm.where(Person::class.java)
            .sort("row", Sort.ASCENDING)
            .findAll()
    }

    /**
     * Find a person with given id. Returns null if argument is null
     */
    fun getPersonWithId(pickedPersonId: String?): Person? {
        if(pickedPersonId == null) return null
        return realm.where(Person::class.java)
            .equalTo("deleted", false)
            .equalTo("id", pickedPersonId)
            .findFirst()
    }

    /**
     * Creates a new transaction with the supplied arguments and saves it in the database.
     */
    fun createAndSaveTransaction(person: Person, product: Product, amount: Int, buy: Boolean):Transaction{
        var savedTrans:Transaction? = null
        realm.executeTransaction{
            val trans = Transaction.create(person, product, amount, buy)
            person.addTransaction(trans)
            savedTrans = realm.copyToRealmOrUpdate(trans)
        }
        return savedTrans!!
    }

    /**
     * Gets a list of all transactions
     * if personId is provided, it findss any with that personid
     * if buy i provided, it gives all with that buy value
     */
    fun getTransactions(personId:String? = null, buy:Boolean? = null): List<Transaction> {
        val query = realm.where(Transaction::class.java)
        if(personId != null){
            query.equalTo("personId", personId)
        }
        if (buy!= null){
            query.equalTo("buy", buy)
        }
        return query.findAll()
    }

    fun updateProductRows() {
        val products = this.findAllCurrentProducts(Product.BOTH_TURF_AND_BUY)

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
            if (realm.where(Transaction::class.java).equalTo("personId", oldProfile.id).findFirst() == null) {
                // Actually delete the product from the realm if it isn't involved in any transactions
                oldProfile.deleteFromRealm()
            } else {
                // fake delete product from the realm
                oldProfile.isDeleted = true
            }
        }
    }

    fun removeProduct(oldProduct: Product) {
        realm.executeTransaction {
            if (realm.where(Transaction::class.java).equalTo(
                    "productId",
                    oldProduct!!.id
                ).findFirst() == null
            ) {
                // Actually delete the profile from the realm if it isn't involved in any transactions
                oldProduct!!.deleteFromRealm()
            } else {
                // fake delete profile from the realm
                oldProduct!!.isDeleted = true
            }
        }
    }


}