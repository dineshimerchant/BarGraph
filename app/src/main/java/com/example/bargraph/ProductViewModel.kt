package com.example.bargraph

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import java.time.LocalDate

class ProductViewModel(private val repository: ProductRepository): ViewModel() {

    private val _weeklyPurchases = MutableStateFlow<List<WeeklyPurchase>>(emptyList())
    val weeklyPurchases: StateFlow<List<WeeklyPurchase>> get() = _weeklyPurchases

    init {
        loadWeeklyData()
    }



    fun loadWeeklyData() {
        viewModelScope.launch {
            repository.getWeeklyPurchases().collect { weeklyData ->
                _weeklyPurchases.value = weeklyData
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addPurchaseData() {
        viewModelScope.launch {

            // Adding the data to the database
            val purchaseData = listOf(
                ProductPurchase(quantity = 10, date = LocalDate.now().minusDays(5)),
                ProductPurchase(quantity = 23, date = LocalDate.now().minusDays(4)),
                ProductPurchase(quantity = 34, date = LocalDate.now().minusDays(3)),
                ProductPurchase(quantity = 10, date = LocalDate.now().minusDays(2)),
                ProductPurchase(quantity = 23, date = LocalDate.now().minusDays(1)),
                ProductPurchase(quantity = 45, date = LocalDate.now()),
                ProductPurchase(quantity = 0, date = LocalDate.now().plusDays(1)),
            )
            /*
                using for each loop to add each item in the list one by one as it is accepting
                an object of product purchase to add rather than the list
            */
            purchaseData.forEach{repository.insertPurchase(it)}
        }
    }
}

class ProductViewModelFactory(private val repository: ProductRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}