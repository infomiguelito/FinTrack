package com.example.fintrack

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val db by lazy{ Room.databaseBuilder(
        applicationContext,
        FinTrackDataBase::class.java,"database-fin-track"
     ).build()
    }

    private val categoryDao by lazy {
        db.getCategoryDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        insertDefaultCategory()

        val rv_List_Category = findViewById<RecyclerView>(R.id.rv_list_category)
        val rv_List_Expense = findViewById<RecyclerView>(R.id.rv_list_expense)

        val categoryAdapter = CategoryListAdapter()
        val expensesAdapter = ExpensesListAdapter()

        categoryAdapter.setOnClickListener { selected ->
            val categoryTemp = categories.map { item ->
                when {
                    item.name == selected.name && !item.isSelected -> item.copy(isSelected = true)
                    item.name == selected.name && item.isSelected-> item.copy(isSelected = false)
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
        getCategoriesFromDataBase(categoryAdapter)

        rv_List_Expense.adapter = expensesAdapter
        expensesAdapter.submitList(expenses)


    }

    private fun insertDefaultCategory(){
        val categoriesEntity = categories.map {
            CategoryEntity(
                name = it.name,
                isSelected = it.isSelected
            )
        }

        GlobalScope.launch (Dispatchers.IO){
            categoryDao.insertAll(categoriesEntity)
        }
    }

    private fun getCategoriesFromDataBase(adapter: CategoryListAdapter){
        GlobalScope.launch (Dispatchers.IO){
            val categoriesFromDb: List<CategoryEntity> = categoryDao.getAll()
            val categoriesUiData = categoriesFromDb.map {
                CategoryUiData(
                    name = it.name,
                    isSelected = it.isSelected
                )

            }
            adapter.submitList(categoriesUiData)
        }

    }
}

val categories = listOf(
    CategoryUiData(
        "ALL",
        isSelected = false
    ),
    CategoryUiData(
        "KEY",
        isSelected = false
    ),
    CategoryUiData(
        "FAMILY-CLOTHES",
        isSelected = false
    ),
    CategoryUiData(
        "INTERNET",
        isSelected = false
    ),
    CategoryUiData(
        "WATER",
        isSelected = false
    ),
    CategoryUiData(
        "LIGHT",
        isSelected = false
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