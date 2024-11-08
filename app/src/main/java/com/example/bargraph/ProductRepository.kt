package com.example.bargraph

import kotlinx.coroutines.flow.Flow

class ProductRepository(private val dao: ProductPurchaseDao) {

    suspend fun insertPurchase(purchase: ProductPurchase) {
        dao.insertPurchase(purchase)
    }

    fun getWeeklyPurchases(): Flow<List<WeeklyPurchase>> {
        return dao.getWeekly()
    }


}