package com.example.daily_expense_tracker_app.repository

import androidx.lifecycle.LiveData
import com.example.daily_expense_tracker_app.database.Expense
import com.example.daily_expense_tracker_app.database.ExpenseDao

class ExpenseRepository(private val expenseDao: ExpenseDao) {
    val allExpenses: LiveData<List<Expense>> = expenseDao.getAllExpenses()

    suspend fun insert(expense: Expense) {
        expenseDao.insert(expense)
    }

    suspend fun update(expense: Expense) {
        expenseDao.update(expense)
    }

    suspend fun delete(expense: Expense) {
        expenseDao.delete(expense)
    }
}