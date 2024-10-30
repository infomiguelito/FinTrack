package com.example.fintrack

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
                entity = CategoryEntity::class,
                parentColumns = ["key"],
                childColumns = ["category"]
            )
    ]
)
data class ExpensesEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Long = 0,
    val category: String,
    val number:String
)
