package com.example.daily_expense_tracker_app.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.daily_expense_tracker_app.R
import com.example.daily_expense_tracker_app.database.Expense

class ExpenseAdapter(
    private val onItemClick: (Expense, position: Int) -> Unit,
    private val onItemClickDelete: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    private var expenses = emptyList<Expense>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]
        holder.bind(expense)

        holder.itemView.findViewById<View>(R.id.deleteButton).setOnClickListener {
            onItemClickDelete(expense)
        }

        holder.itemView.findViewById<View>(R.id.updateButton).setOnClickListener {
            onItemClick(expense, position)
        }
    }

    override fun getItemCount() = expenses.size

    fun setExpenses(newExpenses: List<Expense>) {
        expenses = newExpenses
        notifyDataSetChanged()
    }

    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val expenseAmountTXT = itemView.findViewById<TextView>(R.id.expenseAmount)
        private val expenseCategoryTXT = itemView.findViewById<TextView>(R.id.expenseCategory)
        private val expenseDateTXT = itemView.findViewById<TextView>(R.id.expenseDate)
        private val expenseDescriptionTXT = itemView.findViewById<TextView>(R.id.expenseDescription)
        private val img = itemView.findViewById<ImageView>(R.id.img)

        fun bind(expense: Expense) {
            expenseAmountTXT.text = "₹"+ expense.amount
            expenseCategoryTXT.text = "${expense.category}"
            expenseDateTXT.text = expense.date
            expenseDescriptionTXT.text = expense.description

            if (expense.category.equals("Food")){
                img.setImageResource(R.drawable.burger)
            }else if (expense.category.equals("Shopping")){
                img.setImageResource(R.drawable.shopping)
            }else if (expense.category.equals("Vegetable")){
                img.setImageResource(R.drawable.vegetable)
            }else if (expense.category.equals("Bills")){
                img.setImageResource(R.drawable.water_bill)
            }else if (expense.category.equals("Rent")){
                img.setImageResource(R.drawable.house)
            }else if (expense.category.equals("Travel")){
                img.setImageResource(R.drawable.travel)
            }
        }
    }
}
