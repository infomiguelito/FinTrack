package com.example.fintrack

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val rv_List_Category = findViewById<RecyclerView>(R.id.rv_list_category)
        val rv_List_Expense = findViewById<RecyclerView>(R.id.rv_list_expense)

        val categoryAdapter = CategoryListAdapter()
        val expensesAdapter = ExpensesListAdapter()

        categoryAdapter.setOnClickListener { selected ->
            val categoryTemp = categories.map { item ->
                when {
                    item.name == selected.name && !item.isSelect -> item.copy(isSelect = true)
                    item.name == selected.name && item.isSelect -> item.copy(isSelect = false)
                    else -> item
                }
            }
            val expensesTemp =
                if (selected.name != "ALL") {
                    expenses.filter { it.category == selected.name }
                } else {
                    expenses
                }
            expensesAdapter.submitList(expensesTemp)
            categoryAdapter.submitList(categoryTemp)
        }
        rv_List_Category.adapter = categoryAdapter
        categoryAdapter.submitList(categories)
        rv_List_Expense.adapter = expensesAdapter
        expensesAdapter.submitList(expenses)


    }
}

val categories = listOf(
    CategoryUiData(
        "ALL",
        isSelect = false
    ),
    CategoryUiData(
        "KEY",
        isSelect = false
    ),
    CategoryUiData(
        "FAMILY-CLOTHES",
        isSelect = false
    ),
    CategoryUiData(
        "INTERNET",
        isSelect = false
    ),
    CategoryUiData(
        "WATER",
        isSelect = false
    ),
    CategoryUiData(
        "LIGHT",
        isSelect = false
    )
)

val expenses = listOf(
    ExpensesUiData(
        "KEY",
        "-115.56"
    ),
    ExpensesUiData(
        "FAMILY-CLOTHES",
        "-354.00"
    ),
    ExpensesUiData(
        "INTERNET",
        "-98.99"
    ),
    ExpensesUiData(
        "WATER",
        "-245.86"
    ),
    ExpensesUiData(
        "LIGHT",
        "-189.58"
    )
)