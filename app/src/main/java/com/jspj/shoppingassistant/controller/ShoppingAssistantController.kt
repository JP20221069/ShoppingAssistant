package com.jspj.shoppingassistant.controller

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.jspj.shoppingassistant.model.Price
import com.jspj.shoppingassistant.model.Product
import com.jspj.shoppingassistant.model.ShoppingItem
import com.jspj.shoppingassistant.model.ShoppingList
import com.jspj.shoppingassistant.model.Store
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class ShoppingAssistantController()
{
    val DBinstance = FirebaseDatabase.getInstance();

    suspend fun getProducts(): List<Product> {
        return try {
            val databaseReference: DatabaseReference = DBinstance.getReference("PRODUCTS")
            val snapshot: DataSnapshot = databaseReference.get().await()
            val productList = mutableListOf<Product>()
            for (productSnapshot in snapshot.children) {
                val product = productSnapshot.getValue(Product::class.java)
                product?.let { productList.add(it) }
            }

            productList
        } catch (e: DatabaseException) {
            emptyList()
        }
    }

    suspend fun getStores(): List<Store> {
        return try {
            val databaseReference: DatabaseReference = DBinstance.getReference("STORES")
            val snapshot: DataSnapshot = databaseReference.get().await()
            val storeList = mutableListOf<Store>()
            for (storeSnapshot in snapshot.children) {
                val store = storeSnapshot.getValue(Store::class.java)
                store?.let { storeList.add(it) }
            }

            storeList
        } catch (e: DatabaseException) {
            emptyList()
        }
    }
    suspend fun getStoreById(StoreID: String): Store? {
        return try {
            val databaseReference: DatabaseReference = DBinstance.getReference("STORES")
            // Use orderByKey() and equalTo() to create a query for the specified ID
            val query = databaseReference.orderByKey().equalTo(StoreID)
            val storeSnapshot: DataSnapshot = query.get().await()

            // Check if the product with the specified ID exists
            if (storeSnapshot.exists()) {
                // Convert the snapshot to a Product object
                val store = storeSnapshot.children.first().getValue(Store::class.java)
                store
            } else {
                null
            }
        } catch (e: DatabaseException) {
            // Handle exceptions, e.g., permission denied, network issues, etc.
            null
        }
    }
    suspend fun getPriceForProduct(ProductID:String,StoreID:String): Price? {
        return try {
            val databaseReference: DatabaseReference = DBinstance.getReference("PRICES/"+StoreID+"/"+ProductID)
            val snapshot: DataSnapshot = databaseReference.get().await()
            if(snapshot.exists())
            {
                val price:Price=Price();
                price.ProductID= ProductID.toInt();
                price.StoreID=StoreID.toInt();
                price.Price=snapshot.getValue(Float::class.java)!!;
                return price
            }
            else
            {
                return null
            }
        } catch (e: DatabaseException) {
            println(e.message)
            null
        }
    }
    // Function to retrieve a specific product by ID using a query without using event listeners
    suspend fun getProductById(productId: String): Product? {
        return try {
            val databaseReference: DatabaseReference = DBinstance.getReference("PRODUCTS")
            // Use orderByKey() and equalTo() to create a query for the specified ID
            val query = databaseReference.orderByKey().equalTo(productId)
            val productSnapshot: DataSnapshot = query.get().await()

            // Check if the product with the specified ID exists
            if (productSnapshot.exists()) {
                // Convert the snapshot to a Product object
                val product = productSnapshot.children.first().getValue(Product::class.java)
                product
            } else {
                null
            }
        } catch (e: DatabaseException) {
            // Handle exceptions, e.g., permission denied, network issues, etc.
            null
        }
    }

    suspend fun getListItems(userID:String,listID:String): MutableList<ShoppingItem> {
        return try {
            val databaseReference: DatabaseReference = DBinstance.getReference("LISTS/"+userID+"/"+listID+"/Items");
            val snapshot: DataSnapshot = databaseReference.get().await()
            val itemList = mutableListOf<ShoppingItem>()
            for (itemSnapshot in snapshot.children) {
                var item:ShoppingItem = ShoppingItem()
                var prod:Product = getProductById(itemSnapshot.child("Product").getValue(Int::class.java).toString())!!
                item.Product=prod;
                item.Amount=itemSnapshot.child("Amount").getValue(Int::class.java)!!;
                item.Notes = itemSnapshot.child("Notes").getValue(String::class.java)!!;
                item.Checked = itemSnapshot.child("Checked").getValue(Boolean::class.java)!!;
                itemList.add(item);
            }
            return itemList
        } catch (e: DatabaseException) {
            println(e.message)
            mutableListOf<ShoppingItem>();
        }
    }

    suspend fun getList(userID:String,listID:String) : ShoppingList?
    {
        return try {
            val databaseReference: DatabaseReference = DBinstance.getReference("LISTS/"+userID+"/"+listID);
            val productSnapshot: DataSnapshot = databaseReference.get().await()
            var shoppingList:ShoppingList = ShoppingList();
            if (productSnapshot.exists()) {
                    var storeID = productSnapshot.child("Store").getValue(Int::class.java);
                    var store:Store = getStoreById(storeID.toString())!!;
                    var name:String = productSnapshot.child("Name").getValue(String::class.java)!!;
                    var items:MutableList<ShoppingItem> = getListItems(userID,listID);
                    shoppingList = ShoppingList(listID.toInt(),items,name,store);
                return shoppingList;

            }
            else
            {
                return null
            }
        }
        catch (e: DatabaseException)
        {
            // Handle exceptions, e.g., permission denied, network issues, etc.
            null
        }
        null
    }

    suspend fun getListsByUser(userID: String) : MutableList<ShoppingList>
    {
        return try {
            val databaseReference: DatabaseReference = DBinstance.getReference("LISTS/"+userID)
            val snapshot: DataSnapshot = databaseReference.get().await()
            val listoflists = mutableListOf<ShoppingList>()
            for (listSnapshot in snapshot.children) {
                val listid = listSnapshot.key
                listoflists.add(getList(userID,listid!!)!!);
            }

            return listoflists
        }
        catch (e: DatabaseException) {
            println(e.message)
            mutableListOf<ShoppingList>()
        }
    }

    suspend fun getLastListIDByUser(userID: String) : String
    {
        return try {
            val databaseReference: DatabaseReference = DBinstance.getReference("LISTS/"+userID)
            val snapshot: DataSnapshot = databaseReference.orderByKey().limitToLast(1).get().await()

            if(snapshot.hasChildren()) {
                return snapshot.children.first().key!!;
            }
            else
            {
                return "0";
            }

        }
        catch (e: DatabaseException) {
            println(e.message)
            return ""
        }
    }

    fun getUID() : String?
    {
        return Firebase.auth.uid;
    }

    suspend fun insertProduct(p:Product)
    {
        val ref = DBinstance.getReference().child("PRODUCTS");
        val products= mutableMapOf<String,Product>();
        products.put(p.ID.toString(),p)
        ref.setValue(products);
    }

    suspend fun insertList(l:ShoppingList)
    {
        val ref = DBinstance.getReference().child("LISTS/"+getUID())
        val itemsMapList = l.Products?.map { item ->
            mapOf(
                "Product" to item.Product?.ID,
                "Amount" to item.Amount,
                "Notes" to item.Notes,
                "Checked" to item.Checked
            )
        }

        val updatedMap: Map<String, Any?> = mapOf(
            "Items" to itemsMapList,
            "Name" to l.Name,
            "Store" to l.Store?.ID
        )

        val newid = getLastListIDByUser(getUID()!!).toInt()+1;
        val insertMap: Map<String,Any?> = mapOf(
            newid.toString() to updatedMap
        )
        ref.child(newid.toString()).setValue(updatedMap);
    }

    suspend fun updateList(l:ShoppingList) : Boolean
    {
        return try {
            val ref = DBinstance.getReference().child("LISTS/"+getUID()+"/"+l.ID)
            val itemsMapList = l.Products?.map { item ->
                mapOf(
                    "Product" to item.Product?.ID,
                    "Amount" to item.Amount,
                    "Notes" to item.Notes,
                    "Checked" to item.Checked
                )
            }

            val updatedMap: Map<String, Any?> = mapOf(
                "Items" to itemsMapList,
                "Name" to l.Name,
                "Store" to l.Store?.ID
            )

            ref.updateChildren(updatedMap).await();

            true
        } catch (e: DatabaseException) {
            // Handle exceptions, e.g., permission denied, network issues, etc.
            println(e.message)
            false
        }
    }

    suspend fun deleteList(l:ShoppingList): Boolean {
        return try {
            val ref = DBinstance.getReference().child("LISTS/"+getUID()+"/"+l.ID);

            // Use removeValue() to delete the list
            ref.removeValue().await()

            // Deletion successful
            true
        } catch (e: DatabaseException) {
            // Handle exceptions, e.g., permission denied, network issues, etc.
            println(e.message)
            false
        }
    }

    private suspend fun Task<DataSnapshot>.await(): DataSnapshot {
        return suspendCoroutine { continuation ->
            addOnCompleteListener {
                if (isSuccessful) {
                    continuation.resume(result!!)
                } else {
                    continuation.resumeWithException(exception ?: DatabaseException("Unknown error"))
                }
            }
        }
    }

}