package com.expensemanager.data.api

import com.expensemanager.data.local.entity.ExpenseModel

interface ApiHelper {

    suspend fun getExpense(): List<ExpenseModel>

}