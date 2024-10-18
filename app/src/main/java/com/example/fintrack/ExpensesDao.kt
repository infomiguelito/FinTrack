package com.example.fintrack

import androidx.room.Dao
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
    fun insetAll(expensesEntity: ExpensesEntity)

}