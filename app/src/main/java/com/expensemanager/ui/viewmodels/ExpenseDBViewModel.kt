package com.expensemanager.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expensemanager.data.local.DatabaseHelper
import com.expensemanager.data.local.entity.ExpenseModel
import com.expensemanager.ui.base.SingleLiveEvent
import com.expensemanager.ui.base.UiState
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date


class ExpenseManagerViewModel(
    private val dbHelper: DatabaseHelper
) : ViewModel() {

    private val uiState = SingleLiveEvent<UiState<List<ExpenseModel>>>()
    private val uiStateAddExpense = SingleLiveEvent<UiState<Boolean>>()
    private val uiStateDeleteExpense = SingleLiveEvent<UiState<Boolean>>()
    private val uiStateEditExpense = SingleLiveEvent<UiState<Boolean>>()
    var filterName: String? = "All(Default)"
    var durationName: String? = "All(Default)"

    fun fetchExpenses() {
        viewModelScope.launch {
            uiState.postValue(UiState.Loading)
            try {
                val usersFromDb = dbHelper.getExpense()
                uiState.postValue(UiState.Success(usersFromDb))
            } catch (e: Exception) {
                uiState.postValue(UiState.Error("Something Went Wrong"))
            }
        }
    }

    fun addExpense(expenseModel: ExpenseModel) {
        viewModelScope.launch {
            uiStateAddExpense.postValue(UiState.Loading)
            try {
                dbHelper.insert(expenseModel)
                uiStateAddExpense.postValue(UiState.Success(true))
            } catch (e: Exception) {
                uiStateAddExpense.postValue(UiState.Error("Something Went Wrong"))
            }
        }
    }

    fun editExpense(expenseModel: ExpenseModel) {
        viewModelScope.launch {
            uiStateEditExpense.postValue(UiState.Loading)
            try {
                dbHelper.edit(expenseModel)
                uiStateEditExpense.postValue(UiState.Success(true))
            } catch (e: Exception) {
                uiStateEditExpense.postValue(UiState.Error("Something Went Wrong"))
            }
        }
    }

    fun deleteExpense(expenseModel: ExpenseModel) {
        viewModelScope.launch {
            uiStateDeleteExpense.postValue(UiState.Loading)
            try {
                dbHelper.delete(expenseModel)
                uiStateDeleteExpense.postValue(UiState.Success(true))
            } catch (e: Exception) {
                uiStateDeleteExpense.postValue(UiState.Error("Something Went Wrong"))
            }
        }
    }

    fun getUiState(): LiveData<UiState<List<ExpenseModel>>> {
        return uiState
    }

    fun getUiStateAddExpense(): LiveData<UiState<Boolean>> {
        return uiStateAddExpense
    }

    fun getUiStateDeleteExpense(): LiveData<UiState<Boolean>> {
        return uiStateDeleteExpense
    }

    fun getUiStateEditExpense(): LiveData<UiState<Boolean>> {
        return uiStateEditExpense
    }

    fun setFilter(filterResult: String?) {
        this.filterName = filterResult
        viewModelScope.launch {
            uiState.postValue(UiState.Loading)
            try {
                if (filterName == "All(Default)") {
                    val usersFromDb = dbHelper.getExpense()
                    uiState.postValue(UiState.Success(usersFromDb))
                } else {
                    val usersFromDb = dbHelper.getFilteredExpenseWithTime(
                        filterResult = filterName.toString(),
                        minTime = 0L,
                        maxTime = System.currentTimeMillis()
                    )
                    uiState.postValue(UiState.Success(usersFromDb))
                }
            } catch (e: Exception) {
                uiState.postValue(UiState.Error("Something Went Wrong"))
            }
        }
    }

    private fun getMinTime(name: String): Long {
        val referenceDate = Date()
        val c: Calendar = Calendar.getInstance()
        c.time = referenceDate
        when (name) {
            "Last 1 month" -> {
                c.add(Calendar.MONTH, -1)
                return c.time.time
            }

            "Last 3 month" -> {
                c.add(Calendar.MONTH, -3)
                return c.time.time
            }

            "Last 6 month" -> {
                c.add(Calendar.MONTH, -6)
                return c.time.time
            }

            else -> return 0L
        }
    }

    fun setDurationFilter(durationResult: String?) {
        this.durationName = durationResult
        viewModelScope.launch {
            uiState.postValue(UiState.Loading)
            try {
                if (durationName == "All(Default)") {
                    setFilter(filterName)
                } else {
                    println(getMinTime(durationName.toString()))
                    val usersFromDb = dbHelper.getFilteredExpenseWithTime(
                        filterResult = filterName.toString(),
                        minTime = getMinTime(durationName.toString()),
                        maxTime = System.currentTimeMillis()
                    )
                    uiState.postValue(UiState.Success(usersFromDb))
                }
            } catch (e: Exception) {
                uiState.postValue(UiState.Error("Something Went Wrong"))
            }
        }
    }
}