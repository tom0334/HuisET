package com.tobo.huiset.utils

import com.tobo.huiset.realmModels.Person
import com.tobo.huiset.realmModels.Product
import com.tobo.huiset.realmModels.Transaction
import com.tobo.huiset.utils.extensions.executeSafe
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort

class HuisETDB (private val realm: Realm){


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

    fun getSelectedProduct():Product?{
        return realm.where(Product::class.java)
            .equalTo("deleted", false)
            .equalTo("selected", true)
            .sort("row", Sort.ASCENDING)
            .findFirst()

    }

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


    fun undoTransaction(doneTransaction: Transaction?, person: Person) {
        realm.executeSafe {
            person.undoTransaction(doneTransaction)
            doneTransaction?.deleteFromRealm()
        }
    }

     fun getSelectedPersonInHistory(): Person? {
        return realm.where(Person::class.java).equalTo("selectedInHistoryView", true).findFirst()
    }


    fun getProductWithId(productId: String): Product? {
        return realm.where(Product::class.java)
            .equalTo("id", productId)
            .findFirst()
    }

    fun findAllCurrentProducts(excludeHidden:Boolean = false): RealmResults<Product> {
        val query = realm.where(Product::class.java)
            .equalTo("deleted", false)
        if (excludeHidden){
            query.equalTo("show",true)
        }
        return query.sort("row", Sort.ASCENDING).findAll()
    }

    fun findAllCurrentPersons(excludeHidden:Boolean = false): RealmResults<Person> {
        val query = realm.where(Person::class.java)
            .equalTo("deleted", false)
        if (excludeHidden){
            query.equalTo("show",true)
        }
        return query.sort("row", Sort.ASCENDING).findAll()
    }

    fun getPersonWithId(pickedPersonId: String?): Person? {
        if(pickedPersonId == null) return null
        return realm.where(Person::class.java)
            .equalTo("deleted", false)
            .equalTo("id", pickedPersonId)
            .findFirst()
    }

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