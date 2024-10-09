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
import kotlin.math.E

class MainActivity : AppCompatActivity() {

    private val db by lazy{ Room.databaseBuilder(
        applicationContext,
        FinTrackDataBase::class.java,"database-fin-track"
     ).build()
    }

    private val categoryDao by lazy {
        db.getCategoryDao()
    }

    private val expensesDao by lazy {
        db.getExpensesDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val rv_List_Category = findViewById<RecyclerView>(R.id.rv_list_category)
        val rv_List_Expense = findViewById<RecyclerView>(R.id.rv_list_expense)

        val categoryAdapter = CategoryListAdapter()
        val expensesAdapter = ExpensesListAdapter()

        categoryAdapter.setOnClickListener { selected ->
//            val categoryTemp = categories.map { item ->
//                when {
//                    item.name == selected.name && !item.isSelected -> item.copy(isSelected = true)
//                    item.name == selected.name && item.isSelected-> item.copy(isSelected = false)
//                    else -> item
//                }
//            }
//            val expensesTemp =
//                if (selected.name != "ALL") {
//                    expenses.filter { it.category == selected.name }
//                } else {
//                    expenses
//                }
//            expensesAdapter.submitList(expensesTemp)
//            categoryAdapter.submitList(categoryTemp)
        }
        rv_List_Category.adapter = categoryAdapter
        getCategoriesFromDataBase(categoryAdapter)

        rv_List_Expense.adapter = expensesAdapter
        getExpensesFromDataBase(expensesAdapter)

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

    private fun getExpensesFromDataBase(adapter: ExpensesListAdapter){
        GlobalScope.launch(Dispatchers.IO){
            val expensesFromDb : List<ExpensesEntity> = expensesDao.getAll()
            val expensesUiData = expensesFromDb.map {
                ExpensesUiData(

                    category = it.category,
                    name = it.name
                )
            }

            adapter.submitList(expensesUiData)
        }
    }
}



