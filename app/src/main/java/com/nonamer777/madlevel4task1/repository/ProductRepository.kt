package com.nonamer777.madlevel4task1.repository

import android.content.Context
import com.nonamer777.madlevel4task1.dao.ProductDao
import com.nonamer777.madlevel4task1.database.ShoppingListDatabase
import com.nonamer777.madlevel4task1.model.Product

class ProductRepository(context: Context) {

    private val productDao: ProductDao

    init {
        val database = ShoppingListDatabase.getDatabase(context)

        productDao = database!!.productDao()
    }

    suspend fun getAllProducts(): List<Product> = productDao.getAllProducts()

    suspend fun saveProduct(product: Product) = productDao.saveProduct(product)

    suspend fun updateProduct(product: Product) = productDao.saveProduct(product)

    suspend fun deleteProduct(product: Product) = productDao.deleteProduct(product)

    suspend fun deleteAllProducts() = productDao.deleteAllProducts()
}
