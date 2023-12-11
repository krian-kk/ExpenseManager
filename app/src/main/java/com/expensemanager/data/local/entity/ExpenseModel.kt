package com.expensemanager.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense")
data class ExpenseModel(
    @PrimaryKey(autoGenerate = false) val id: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "date") val date: Long?,
    @ColumnInfo(name = "category") var category: String?,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "amount") val amount: Int?
)