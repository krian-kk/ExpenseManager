package com.expensemanager.data.local

import com.expensemanager.data.local.entity.ExpenseModel

interface DatabaseHelper {

    suspend fun getExpense(): List<ExpenseModel>

    suspend fun getExpense(
        minTime: Long,
        maxTime: Long
    ): List<ExpenseModel>

    suspend fun insert(users: ExpenseModel)

    suspend fun delete(expenseModel: ExpenseModel)

    suspend fun edit(expenseModel: ExpenseModel)

    suspend fun getFilteredExpense(filterResult: String): List<ExpenseModel>

    suspend fun getFilteredExpenseWithTime(
        filterResult: String,
        minTime: Long,
        maxTime: Long
    ): List<ExpenseModel>
}