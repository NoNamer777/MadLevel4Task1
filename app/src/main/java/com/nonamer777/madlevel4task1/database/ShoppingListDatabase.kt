package com.nonamer777.madlevel4task1.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nonamer777.madlevel4task1.dao.ProductDao
import com.nonamer777.madlevel4task1.model.Product

@Database(entities = [Product::class], version = 1, exportSchema = false)
abstract class ShoppingListDatabase: RoomDatabase() {

    abstract fun productDao(): ProductDao

    companion object {

        private const val DATABASE_NAME = "SHOPPING_LIST_DATABASE"

        @Volatile
        private var shoppingListDatabaseInstance: ShoppingListDatabase? = null

        fun getDatabase(context: Context): ShoppingListDatabase? {

            if (shoppingListDatabaseInstance == null) {
                synchronized(ShoppingListDatabase::class.java) {

                    if (shoppingListDatabaseInstance == null) {
                        shoppingListDatabaseInstance = Room.databaseBuilder(
                            context.applicationContext,
                            ShoppingListDatabase::class.java,
                            DATABASE_NAME
                        ).build()
                    }
                }
            }
            return shoppingListDatabaseInstance
        }
    }
}
