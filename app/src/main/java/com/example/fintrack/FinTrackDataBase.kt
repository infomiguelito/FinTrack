package com.example.fintrack

import androidx.room.Database
import androidx.room.RoomDatabase


@Database([CategoryEntity::class], version = 2)
abstract class FinTrackDataBase : RoomDatabase(){

    abstract fun getCategoryDao(): CategoryDao

}