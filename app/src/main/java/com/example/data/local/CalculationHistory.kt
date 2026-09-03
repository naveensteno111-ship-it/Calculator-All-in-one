package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calculation_history")
data class CalculationHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val calculatorId: String,
    val calculatorName: String,
    val category: String,
    val inputSummary: String,
    val resultSummary: String,
    val detailedText: String,
    val timestamp: Long = System.currentTimeMillis()
)
