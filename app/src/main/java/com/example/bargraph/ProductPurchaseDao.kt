package com.example.bargraph
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface ProductPurchaseDao {

    // function to perform data insertion
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchase(purchase: ProductPurchase)

    // Query to get the data from the database and aligning them according to the date
    @Query("SELECT date, SUM(quantity) as quantity FROM product_purchases GROUP BY date")
    fun getWeekly(): Flow<List<WeeklyPurchase>>
}

// Created another data class to return the data without the id
data class WeeklyPurchase(
    val date: LocalDate,
    val quantity: Int
)