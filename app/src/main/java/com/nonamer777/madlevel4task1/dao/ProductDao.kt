package com.nonamer777.madlevel4task1.dao

import androidx.room.*
import com.nonamer777.madlevel4task1.model.Product

@Dao
interface ProductDao {

    @Query("select * from product_table")
    suspend fun getAllProducts(): List<Product>

    @Insert
    suspend fun saveProduct(product: Product)

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("delete from product_table")
    suspend fun deleteAllProducts()
}
