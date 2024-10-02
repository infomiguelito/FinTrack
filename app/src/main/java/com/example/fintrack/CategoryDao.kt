package com.example.fintrack

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface CategoryDao {
    @Query("Select * From categoryentity" )
    fun getAll() : List<CategoryEntity>

    @Insert()
    fun insertAll(vararg categoryEntity : List<CategoryEntity>)
}