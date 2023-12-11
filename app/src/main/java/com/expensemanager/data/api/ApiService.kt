package com.expensemanager.data.api

import com.expensemanager.data.local.entity.ExpenseModel
import retrofit2.http.GET

interface ApiService {

    @GET("expense")
    suspend fun getExpense(): List<ExpenseModel>
}