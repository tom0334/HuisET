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
    fun getFirstProduct(): Product? {
        return this.realm.where(Product::class.java)
            .equalTo("deleted", false)
            .equalTo("show", true)
            .sort("row", Sort.ASCENDING)
            .findFirst()
    }

    /**
     * Selects the highest priority product
     */
    fun selectFirstProduct() = selectProduct(getFirstProduct())

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
    fun doTransactionWithSelectedProduct(person: Person) {
        realm.executeSafe {
            val selectedProduct = getSelectedProduct()
            val t = Transaction.create(person, selectedProduct, false)
            selectedProduct?.isSelected = false

            realm.copyToRealmOrUpdate(t)
            person.addTransaction(t)
        }
    }

    /**
     * Undos and then deletes a transaction from realm
     */
    fun undoTransaction(doneTransaction: Transaction?, person: Person) {
        realm.executeSafe {
            person.undoTransaction(doneTransaction)
            doneTransaction?.deleteFromRealm()
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
    fun findAllCurrentProducts(includeHidden:Boolean = false): RealmResults<Product> {
        val query = realm.where(Product::class.java)
            .equalTo("deleted", false)
        if (! includeHidden){
            query.equalTo("show",true)
        }
        return query.sort("row", Sort.ASCENDING).findAll()
    }

    /**
     * Finds all persons that are not deleted
     */
    fun findAllCurrentPersons(includeHidden:Boolean = false): RealmResults<Person> {
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
    fun createAndSaveTransaction(person: Person, product: Product, buy: Boolean):Transaction{
        var savedTrans:Transaction? = null
        realm.executeTransaction{
            val trans = Transaction.create(person, product, buy)
            person.addTransaction(trans)
            savedTrans = realm.copyToRealmOrUpdate(trans)
        }
        return savedTrans!!
    }


}