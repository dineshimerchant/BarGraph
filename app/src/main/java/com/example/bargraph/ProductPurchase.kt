package com.example.bargraph

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "product_purchases")
data class ProductPurchase(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: LocalDate,
    val quantity: Int
)
