package com.example.fintrack

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ExpensesDao {
    @Query("Select * From expensesentity")
    fun getAll() : List<ExpensesEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(expensesEntity: List<ExpensesEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(expensesEntity: ExpensesEntity)

    @Delete
    fun delete (expensesEntity: ExpensesEntity)

    @Query("Select * From expensesentity where category is :categoryName")
    fun getAllByCategoryName(categoryName : String) : List<ExpensesEntity>


    @Delete
    fun deleteAll (expensesEntity: List<ExpensesEntity>)

}