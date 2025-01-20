package com.example.daily_expense_tracker_app.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: String,
    val category: String,
    val date: String,
    val description: String,
    val imagePath: String?
)