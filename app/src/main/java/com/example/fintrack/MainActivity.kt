package com.example.fintrack

import android.graphics.Color
import android.graphics.Color.TRANSPARENT
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private var categories = listOf<CategoryUiData>()
    private var categoriesEntity = listOf<CategoryEntity>()
    private var expenses = listOf<ExpensesUiData>()

    private lateinit var rvListCategory: RecyclerView
    private lateinit var ctnEmptyView: LinearLayout
    private lateinit var fabCreateExpenses: FloatingActionButton
    private lateinit var tvCategoryExpenses: TextView
    private lateinit var tvExpensesTotals: TextView
    private lateinit var tvValueExpenses: TextView

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.TRANSPARENT
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }

        setContentView(R.layout.activity_main)

        rvListCategory = findViewById(R.id.rv_list_category)
        ctnEmptyView = findViewById(R.id.ll_empty_fin_track)
        fabCreateExpenses = findViewById(R.id.fab_create_expense)
        tvCategoryExpenses = findViewById(R.id.tv_category_expense)
        tvExpensesTotals = findViewById(R.id.tv_title_expenses_totals)
        tvValueExpenses = findViewById(R.id.value_expenses)
        val rvListExpense = findViewById<RecyclerView>(R.id.rv_list_expense)
        val btnAddCategory = findViewById<Button>(R.id.btn_add_empty_category)

        updateTotalValue()

        btnAddCategory.setOnClickListener {
            showCreateCategoryBottomSheet()
        }

        fabCreateExpenses.setOnClickListener {
            createExpensesUpdateBottomSheet()
        }

        expensesAdapter.setOnClickListener { expenses ->
            createExpensesUpdateBottomSheet(expenses)

        }

        categoryAdapter.setOnLongClickListener { categoryToBeDelete ->

            if (categoryToBeDelete.name != "+" && categoryToBeDelete.name != "All") {
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
                        categoryToBeDelete.isSelected,
                    )
                    deleteCategory(categoryEntityToBeDelete)
                }
            }
        }

        categoryAdapter.setOnClickListener { selected ->
            if (selected.name == "+") {
                showCreateCategoryBottomSheet()
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
                    GlobalScope.launch(Dispatchers.IO) {
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
        categoriesEntity = categoriesFromDb

        GlobalScope.launch(Dispatchers.Main) {
            if (categoriesEntity.isEmpty()) {
                rvListCategory.isVisible = false
                fabCreateExpenses.isVisible = false
                tvValueExpenses.isVisible = false
                tvCategoryExpenses.isVisible = false
                tvExpensesTotals.isVisible = false
                ctnEmptyView.isVisible = true
            } else {
                rvListCategory.isVisible = true
                fabCreateExpenses.isVisible = true
                tvValueExpenses.isVisible = true
                tvCategoryExpenses.isVisible = true
                tvExpensesTotals.isVisible = true
                ctnEmptyView.isVisible = false

            }
        }

        val categoriesUiData = categoriesFromDb.map {
            CategoryUiData(
                name = it.name,
                isSelected = it.isSelected,
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
        val expensesUiData: List<ExpensesUiData> = expensesFromDb.map {
            ExpensesUiData(
                id = it.id,
                category = it.category,
                number = it.number
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
            updateTotalValue()
            getExpensesFromDataBase()
        }
    }

    private fun deleteExpenses(expensesEntity: ExpensesEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            expensesDao.delete(expensesEntity)
            updateTotalValue()
            getExpensesFromDataBase()
        }
    }

    private fun deleteCategory(categoryEntity: CategoryEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            val expensesToBeDelete = expensesDao.getAllByCategoryName(categoryEntity.name)
            expensesDao.deleteAll(expensesToBeDelete)
            categoryDao.delete(categoryEntity)
            updateTotalValue()
            getCategoriesFromDataBase()
            getExpensesFromDataBase()
        }
    }

    private fun filterExpenseByCategoryName(category: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val expensesFromDb: List<ExpensesEntity> = expensesDao.getAllByCategoryName(category)
            val expensesUiData: List<ExpensesUiData> = expensesFromDb.map {
                ExpensesUiData(
                    id = it.id,
                    category = it.category,
                    number = it.number
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
            categoryList = categoriesEntity,
            onCreateClicked = { expensesToBeCreate ->
                val ExpensesToBeInsert = ExpensesEntity(
                    number = expensesToBeCreate.number,
                    category = expensesToBeCreate.category
                )
                insertExpenses(ExpensesToBeInsert)
            },
            onUpdateClicked = { expensesToBeUpdate ->
                val ExpensesToBeUpdateInsert = ExpensesEntity(
                    id = expensesToBeUpdate.id,
                    number = expensesToBeUpdate.number,
                    category = expensesToBeUpdate.category
                )
                insertExpenses(ExpensesToBeUpdateInsert)
            },
            onDeleteClicked = { expensesToBeDelete ->
                val ExpensesToBeUpdateDelete = ExpensesEntity(
                    id = expensesToBeDelete.id,
                    number = expensesToBeDelete.number,
                    category = expensesToBeDelete.category
                )
                deleteExpenses(ExpensesToBeUpdateDelete)
            }
        )
        createExpensesBottomSheet.show(supportFragmentManager, "createExpensesBottomSheet")
    }

    private fun showCreateCategoryBottomSheet() {
        val createCategoryBottomSheet = CreateCategoryBottomSheet { categoryName ->
            val categoryEntity = CategoryEntity(
                name = categoryName,
                isSelected = false
            )
            insertCategory(categoryEntity)
        }
        createCategoryBottomSheet.show(supportFragmentManager, "createCategoryBottomSheet")
    }

    private fun updateTotalValue() {
        GlobalScope.launch(Dispatchers.Main) {
            val totalValue = withContext(Dispatchers.IO) {
                expensesDao.getTotalValor()
            }
            val numberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
            val formattedValue = numberFormat.format(totalValue)
            tvValueExpenses.text = "$formattedValue"
        }
    }
}




