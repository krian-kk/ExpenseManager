package com.expensemanager.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.expensemanager.data.local.entity.ExpenseModel

@Dao
interface UserDao {

    @Query("SELECT * FROM expense")
    suspend fun getAll(): List<ExpenseModel>

    @Query("SELECT * FROM expense WHERE date BETWEEN :minTime AND :maxTime")
    suspend fun getAll(
        minTime: Long, maxTime: Long
    ): List<ExpenseModel>

    @Query("SELECT * FROM expense WHERE category LIKE :filterName")
    suspend fun getFilteredExpense(filterName: String): List<ExpenseModel>

    @Query("SELECT * FROM expense WHERE category LIKE :filterName AND date BETWEEN :minTime AND :maxTime")
    suspend fun getFilteredExpense(
        filterName: String, minTime: Long, maxTime: Long
    ): List<ExpenseModel>

    @Insert
    suspend fun insert(users: ExpenseModel)

    @Delete
    suspend fun delete(user: ExpenseModel)

    @Update
    suspend fun edit(expenseModel: ExpenseModel)
}