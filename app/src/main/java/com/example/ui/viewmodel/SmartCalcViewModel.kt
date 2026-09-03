package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppThemeMode
import com.example.data.local.CalculationHistory
import com.example.data.repository.AppRepository
import com.example.data.repository.CurrencyState
import com.example.domain.CalculatorItem
import com.example.domain.CalculatorRegistry
import com.example.util.AppLanguage
import com.example.util.NumberFormatType
import com.example.util.SubscriptionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SmartCalcViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository(application)
    val subscriptionManager = SubscriptionManager.getInstance(application)

    val allHistory: StateFlow<List<CalculationHistory>> = repository.allHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val themeMode: StateFlow<AppThemeMode> = repository.preferencesManager.themeModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppThemeMode.SYSTEM)

    val language: StateFlow<AppLanguage> = repository.preferencesManager.languageFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppLanguage.ENGLISH)

    val numberFormat: StateFlow<NumberFormatType> = repository.preferencesManager.numberFormatFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NumberFormatType.INDIAN)

    val defaultCurrency: StateFlow<String> = repository.preferencesManager.defaultCurrencyFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "INR")

    val favoriteIds: StateFlow<Set<String>> = repository.preferencesManager.favoriteCalculatorsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), setOf("emi", "sip", "gst", "currency", "loan", "scientific"))

    val isProActive: StateFlow<Boolean> = subscriptionManager.isPremiumEnabledFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val proActivationTimestamp: StateFlow<Long> = subscriptionManager.activationTimestampFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val proExpiryTimestamp: StateFlow<Long> = subscriptionManager.expiryTimestampFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    private val _currencyState = MutableStateFlow(CurrencyState())
    val currencyState: StateFlow<CurrencyState> = _currencyState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        fetchCurrencyRates("USD")
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleFavorite(calcId: String) {
        viewModelScope.launch {
            repository.preferencesManager.toggleFavorite(calcId)
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        viewModelScope.launch {
            repository.preferencesManager.setThemeMode(mode)
        }
    }

    fun toggleDarkMode() {
        val nextMode = if (themeMode.value == AppThemeMode.DARK) AppThemeMode.LIGHT else AppThemeMode.DARK
        setThemeMode(nextMode)
    }

    fun setLanguage(lang: AppLanguage) {
        viewModelScope.launch {
            repository.preferencesManager.setLanguage(lang)
        }
    }

    fun setNumberFormat(format: NumberFormatType) {
        viewModelScope.launch {
            repository.preferencesManager.setNumberFormat(format)
        }
    }

    fun setDefaultCurrency(curr: String) {
        viewModelScope.launch {
            repository.preferencesManager.setDefaultCurrency(curr)
        }
    }

    fun activateSubscription(durationDays: Long = 365) {
        viewModelScope.launch {
            subscriptionManager.activateSubscription(durationDays)
            repository.preferencesManager.activateSubscription(durationDays)
        }
    }

    fun cancelSubscription() {
        viewModelScope.launch {
            subscriptionManager.cancelSubscription()
            repository.preferencesManager.cancelSubscription()
        }
    }

    fun openRazorpayPayment(context: Context) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://rzp.io/rzp/51NuWd4")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            // Fallback
        }
    }

    fun saveCalculation(
        calculatorId: String,
        inputSummary: String,
        resultSummary: String,
        detailedText: String
    ) {
        viewModelScope.launch {
            repository.saveHistory(calculatorId, inputSummary, resultSummary, detailedText)
        }
    }

    fun deleteHistory(id: Long) {
        viewModelScope.launch {
            repository.deleteHistory(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun fetchCurrencyRates(base: String) {
        viewModelScope.launch {
            _currencyState.value = _currencyState.value.copy(isLoading = true, error = null)
            val result = repository.fetchExchangeRates(base)
            result.onSuccess { state ->
                _currencyState.value = state
            }.onFailure { error ->
                _currencyState.value = _currencyState.value.copy(
                    base = base,
                    isLoading = false,
                    isLive = false,
                    error = error.localizedMessage ?: "Offline cached rates active"
                )
            }
        }
    }
}
