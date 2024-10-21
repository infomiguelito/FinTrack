package com.example.fintrack

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ExpensesEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Long = 0,
    val category: String,
    val name:String
)
