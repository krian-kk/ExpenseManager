package com.expensemanager.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.expensemanager.data.local.dao.UserDao
import com.expensemanager.data.local.entity.ExpenseModel

@Database(entities = [ExpenseModel::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun expenseDao(): UserDao

}