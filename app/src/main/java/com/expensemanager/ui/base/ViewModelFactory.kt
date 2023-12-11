package com.expensemanager.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.expensemanager.ui.viewmodels.ExpenseManagerViewModel
import com.expensemanager.data.local.DatabaseHelper

class ViewModelFactory(private val dbHelper: DatabaseHelper) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpenseManagerViewModel::class.java)) {
            return ExpenseManagerViewModel(dbHelper) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}