package com.example.fintrack

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var categories = listOf<CategoryUiData>()
    private var expenses = listOf<ExpensesUiData>()
    private val categoryAdapter = CategoryListAdapter()
    private val expensesAdapter by lazy {
        ExpensesListAdapter()
    }


    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            FinTrackDataBase::class.java, "database-fin-track-v2"
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


        val rvListCategory = findViewById<RecyclerView>(R.id.rv_list_category)
        val rvListExpense = findViewById<RecyclerView>(R.id.rv_list_expense)
        val fabCreateExpenses = findViewById<FloatingActionButton>(R.id.fab_create_expense)

        fabCreateExpenses.setOnClickListener {
            createExpensesUpdateBottomSheet()
        }

        expensesAdapter.setOnClickListener { expenses ->
            createExpensesUpdateBottomSheet(expenses)
        }

        categoryAdapter.setOnLongClickListener { categoryToBeDelete ->

            if (categoryToBeDelete.name != "+") {
                val title: String = getString(R.string.title_info)
                val description: String = getString(R.string.info_description)
                val btnText: String = getString(R.string.delete)

                showInfoDialog(
                    title,
                    description,
                    btnText
                ) {
                    val categoryEntityToBeDelete = CategoryEntity(
                        categoryToBeDelete.name,
                        categoryToBeDelete.isSelected
                    )
                    deleteCategory(categoryEntityToBeDelete)
                }
            }
        }

        categoryAdapter.setOnClickListener { selected ->
            if (selected.name == "+") {
                val createCategoryBottomSheet = CreateCategoryBottomSheet { categoryName ->
                    val categoryEntity = CategoryEntity(
                        name = categoryName,
                        isSelected = false
                    )
                    insertCategory(categoryEntity)
                }
                createCategoryBottomSheet.show(supportFragmentManager, "createCategoryBottomSheet")

            } else {
                val categoryTemp = categories.map { item ->
                    when {
                        item.name == selected.name && !item.isSelected -> item.copy(
                            isSelected = true
                        )

                        item.name == selected.name && item.isSelected -> item.copy(isSelected = false)
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
        }
        rvListCategory.adapter = categoryAdapter
        GlobalScope.launch(Dispatchers.IO) {
            getCategoriesFromDataBase()
        }

        rvListExpense.adapter = expensesAdapter


        GlobalScope.launch(Dispatchers.IO) {
            getExpensesFromDataBase()
        }

    }


    private fun showInfoDialog(
        title: String,
        description: String,
        btnText: String,
        onClick: () -> Unit
    ) {
        val infoBottomSheet = InfoBottomSheet(
            title = title,
            description = description,
            btnText = btnText,
            onClick = onClick
        )
        infoBottomSheet.show(supportFragmentManager, "infoBottomSheet")

    }

    private fun getCategoriesFromDataBase() {

        val categoriesFromDb: List<CategoryEntity> = categoryDao.getAll()
        val categoriesUiData = categoriesFromDb.map {
            CategoryUiData(
                name = it.name,
                isSelected = it.isSelected
            )

        }
            .toMutableList()

        categoriesUiData.add(
            CategoryUiData(
                name = "+",
                isSelected = false
            )
        )
        GlobalScope.launch(Dispatchers.IO) {

            categories = categoriesUiData
            categoryAdapter.submitList(categoriesUiData)
        }


    }

    private fun getExpensesFromDataBase() {
        val expensesFromDb: List<ExpensesEntity> = expensesDao.getAll()
        val expensesUiData = expensesFromDb.map {
            ExpensesUiData(
                id = it.id,
                category = it.category,
                name = it.name
            )
        }
        GlobalScope.launch(Dispatchers.IO) {
            expenses = expensesUiData
            expensesAdapter.submitList(expensesUiData)
        }
    }


    private fun insertCategory(categoryEntity: CategoryEntity) {

        GlobalScope.launch(Dispatchers.IO) {
            categoryDao.inset(categoryEntity)
            getCategoriesFromDataBase()
        }
    }

    private fun insertExpenses(expensesEntity: ExpensesEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            expensesDao.insertOrUpdate(expensesEntity)
            getExpensesFromDataBase()
        }
    }

    private fun deleteExpenses(expensesEntity: ExpensesEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            expensesDao.delete(expensesEntity)
            getExpensesFromDataBase()
        }
    }

    private fun deleteCategory(categoryEntity: CategoryEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            categoryDao.delete(categoryEntity)
            getCategoriesFromDataBase()
        }
    }

    private fun createExpensesUpdateBottomSheet(expensesUiData: ExpensesUiData? = null) {
        val createExpensesBottomSheet = CreateOrUpdateExpensesBottomSheet(
            expenses = expensesUiData,
            categoryList = categories,
            onCreateClicked = { expensesToBeCreate ->
                val ExpensesToBeInsert = ExpensesEntity(
                    name = expensesToBeCreate.name,
                    category = expensesToBeCreate.category
                )
                insertExpenses(ExpensesToBeInsert)
            },
            onUpdateClicked = { expensesToBeUpdate ->
                val ExpensesToBeUpdateInsert = ExpensesEntity(
                    id = expensesToBeUpdate.id,
                    name = expensesToBeUpdate.name,
                    category = expensesToBeUpdate.category
                )
                insertExpenses(ExpensesToBeUpdateInsert)

            }, onDeleteClicked = { expensesToBeDelete ->
                val ExpensesToBeUpdateDelete = ExpensesEntity(
                    id = expensesToBeDelete.id,
                    name = expensesToBeDelete.name,
                    category = expensesToBeDelete.category
                )
                deleteExpenses(ExpensesToBeUpdateDelete)

            }

        )
        createExpensesBottomSheet.show(supportFragmentManager, "createExpensesBottomSheet")
    }
}



