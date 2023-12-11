package com.expensemanager.data.api

class ApiHelperImpl(private val apiService: ApiService) : ApiHelper {
    override suspend fun getExpense() = apiService.getExpense()
}