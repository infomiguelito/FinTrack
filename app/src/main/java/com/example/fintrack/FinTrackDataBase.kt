package com.example.fintrack

import androidx.room.Database
import androidx.room.RoomDatabase


@Database([CategoryEntity::class, ExpensesEntity::class], version = 4)
abstract class FinTrackDataBase : RoomDatabase(){

    abstract fun getCategoryDao(): CategoryDao
    abstract fun getExpensesDao(): ExpensesDao

}