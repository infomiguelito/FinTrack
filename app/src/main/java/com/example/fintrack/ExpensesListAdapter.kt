package com.example.fintrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ExpensesListAdapter :
    ListAdapter<ExpensesUiData, ExpensesListAdapter.ExpenseViewHolder>(ExpensesListAdapter) {
    private lateinit var callback: (ExpensesUiData) -> Unit

    fun setOnClickListener(onClick: (ExpensesUiData) -> Unit) {
        callback = onClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_list_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val category = getItem(position)
        holder.bind(category, callback)
    }

    class ExpenseViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val tvCategory = view.findViewById<TextView>(R.id.tv_category_name)
        private val tvTask = view.findViewById<TextView>(R.id.tv_task_name)

        fun bind(expenses: ExpensesUiData, callback: (ExpensesUiData) -> Unit) {
            tvCategory.text = expenses.category
            tvTask.text = expenses.number

            view.setOnClickListener {
                callback.invoke(expenses)
            }
        }
    }


    companion object : DiffUtil.ItemCallback<ExpensesUiData>() {
        override fun areItemsTheSame(oldItem: ExpensesUiData, newItem: ExpensesUiData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ExpensesUiData, newItem: ExpensesUiData): Boolean {
            return oldItem.number == newItem.number
        }
    }
}
