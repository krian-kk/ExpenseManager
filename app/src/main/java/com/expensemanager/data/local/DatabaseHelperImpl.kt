package com.expensemanager.data.local

import com.expensemanager.data.local.entity.ExpenseModel

class DatabaseHelperImpl(private val appDatabase: AppDatabase) : DatabaseHelper {

    override suspend fun getExpense(): List<ExpenseModel> = appDatabase.expenseDao().getAll()
    override suspend fun getExpense(
        minTime: Long, maxTime: Long
    ): List<ExpenseModel> = appDatabase.expenseDao().getAll(minTime, maxTime)

    override suspend fun insert(expense: ExpenseModel) = appDatabase.expenseDao().insert(expense)

    override suspend fun delete(expenseModel: ExpenseModel) =
        appDatabase.expenseDao().delete(expenseModel)

    override suspend fun edit(expenseModel: ExpenseModel) =
        appDatabase.expenseDao().edit(expenseModel)

    override suspend fun getFilteredExpense(filterResult: String): List<ExpenseModel> =
        appDatabase.expenseDao().getFilteredExpense(filterResult)

    override suspend fun getFilteredExpenseWithTime(
        filterResult: String, minTime: Long, maxTime: Long
    ): List<ExpenseModel> =
        appDatabase.expenseDao().getFilteredExpense(filterResult, minTime, maxTime)
}