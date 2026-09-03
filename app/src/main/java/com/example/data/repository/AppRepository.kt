package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.CalculationHistory
import com.example.data.local.CalculationHistoryDao
import com.example.data.local.UserPreferencesManager
import com.example.data.remote.CurrencyApiService
import com.example.domain.CalculatorRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

data class CurrencyState(
    val base: String = "USD",
    val rates: Map<String, Double> = defaultRates,
    val lastUpdated: String = "Offline Cached Rates",
    val isLive: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    companion object {
        val defaultRates = mapOf(
            "USD" to 1.0,
            "INR" to 86.85,
            "EUR" to 0.95,
            "GBP" to 0.79,
            "AED" to 3.67,
            "CAD" to 1.42,
            "AUD" to 1.58,
            "JPY" to 152.3,
            "CNY" to 7.28,
            "SAR" to 3.75,
            "SGD" to 1.34,
            "CHF" to 0.90,
            "NZD" to 1.74,
            "KWD" to 0.31,
            "BHD" to 0.38,
            "OMR" to 0.38,
            "QAR" to 3.64,
            "THB" to 33.9,
            "MYR" to 4.43,
            "KRW" to 1435.0,
            "ZAR" to 18.2,
            "BRL" to 5.82,
            "RUB" to 97.5,
            "TRY" to 36.1,
            "SEK" to 10.7,
            "NOK" to 11.1,
            "DKK" to 7.1,
            "HKD" to 7.78,
            "MXN" to 20.4,
            "IDR" to 16200.0,
            "PKR" to 279.0,
            "BDT" to 121.5,
            "LKR" to 295.0,
            "NPR" to 139.0
        )
    }
}

class AppRepository(context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val historyDao: CalculationHistoryDao = db.calculationHistoryDao()
    val preferencesManager = UserPreferencesManager(context)
    private val currencyApi = CurrencyApiService.create()

    val allHistory: Flow<List<CalculationHistory>> = historyDao.getAllHistory()

    suspend fun saveHistory(
        calculatorId: String,
        inputSummary: String,
        resultSummary: String,
        detailedText: String
    ) = withContext(Dispatchers.IO) {
        val itemMeta = CalculatorRegistry.getById(calculatorId)
        val name = itemMeta?.name ?: calculatorId
        val category = itemMeta?.category?.title ?: "General"
        historyDao.insertHistory(
            CalculationHistory(
                calculatorId = calculatorId,
                calculatorName = name,
                category = category,
                inputSummary = inputSummary,
                resultSummary = resultSummary,
                detailedText = detailedText
            )
        )
    }

    suspend fun deleteHistory(id: Long) = withContext(Dispatchers.IO) {
        historyDao.deleteHistoryById(id)
    }

    suspend fun clearHistory() = withContext(Dispatchers.IO) {
        historyDao.clearAllHistory()
    }

    suspend fun fetchExchangeRates(base: String = "USD"): Result<CurrencyState> = withContext(Dispatchers.IO) {
        try {
            val response = currencyApi.getLatestRates(base)
            if (response.result == "success" && !response.rates.isNullOrEmpty()) {
                Result.success(
                    CurrencyState(
                        base = base,
                        rates = response.rates,
                        lastUpdated = response.timeLastUpdateUtc ?: "Live API",
                        isLive = true,
                        isLoading = false,
                        error = null
                    )
                )
            } else {
                Result.failure(Exception("API returned invalid rates"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
