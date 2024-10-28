package com.example.fintrack

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BroadcastChannel
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
                val title: String = this.getString(R.string.title_info)
                val description: String = this.getString(R.string.info_description)
                val btnText: String = this.getString(R.string.delete)

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
                        item.name == selected.name && item.isSelected -> item.copy(
                            isSelected = true
                        )
                        item.name == selected.name && !item.isSelected -> item.copy(isSelected = true)
                        item.name != selected.name && item.isSelected -> item.copy(isSelected = false)
                        else -> item
                    }
                }
                    if (selected.name != "All") {
                       filterExpenseByCategoryName(selected.name)
                    } else {
                        GlobalScope.launch(Dispatchers.IO){
                            getExpensesFromDataBase()
                        }
                    }
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
            onClick
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
        val categoryListTemp = mutableListOf(
            CategoryUiData(
                name = "All",
                isSelected = true
            )
        )

        categoryListTemp.addAll(categoriesUiData)

        GlobalScope.launch(Dispatchers.IO) {

            categories = categoryListTemp
            categoryAdapter.submitList(categories)
        }


    }

    private fun getExpensesFromDataBase() {
        val expensesFromDb: List<ExpensesEntity> = expensesDao.getAll()
        val expensesUiData : List<ExpensesUiData> = expensesFromDb.map {
            ExpensesUiData(
                id = it.id,
                category = it.category,
                name = it.name
            )
        }
        GlobalScope.launch(Dispatchers.Main) {
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
            val expensesToBeDelete = expensesDao.getAllByCategoryName(categoryEntity.name)
            expensesDao.deleteAll(expensesToBeDelete)
            categoryDao.delete(categoryEntity)
            getCategoriesFromDataBase()
            getExpensesFromDataBase()
        }
    }

    private fun filterExpenseByCategoryName(category : String){
        GlobalScope.launch(Dispatchers.IO){
            val expensesFromDb: List<ExpensesEntity> = expensesDao.getAllByCategoryName(category)
            val expensesUiData : List<ExpensesUiData> = expensesFromDb.map {
                ExpensesUiData(
                    id = it.id,
                    category = it.category,
                    name = it.name
                )
            }
            GlobalScope.launch(Dispatchers.Main) {
                expenses = expensesUiData
                expensesAdapter.submitList(expensesUiData)
            }
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



