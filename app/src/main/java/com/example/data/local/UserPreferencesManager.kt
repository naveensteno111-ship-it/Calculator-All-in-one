package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.util.AppLanguage
import com.example.util.NumberFormatType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "smartcalc_user_prefs")

enum class AppThemeMode {
    SYSTEM, LIGHT, DARK
}

class UserPreferencesManager(private val context: Context) {

    companion object {
        private val THEME_KEY = stringPreferencesKey("pref_theme_mode")
        private val LANGUAGE_KEY = stringPreferencesKey("pref_language")
        private val NUMBER_FORMAT_KEY = stringPreferencesKey("pref_number_format")
        private val DEFAULT_CURRENCY_KEY = stringPreferencesKey("pref_currency")
        private val FAVORITES_KEY = stringSetPreferencesKey("pref_favorites")
        private val IS_PRO_ACTIVE_KEY = booleanPreferencesKey("pref_is_pro_active")
        private val PRO_EXPIRY_TIMESTAMP_KEY = longPreferencesKey("pref_pro_expiry_timestamp")
        private val PRO_ACTIVATION_TIMESTAMP_KEY = longPreferencesKey("pref_pro_activation_timestamp")
    }

    val isProActiveFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        val isActive = prefs[IS_PRO_ACTIVE_KEY] ?: false
        val expiry = prefs[PRO_EXPIRY_TIMESTAMP_KEY] ?: 0L
        isActive && (expiry > System.currentTimeMillis())
    }

    val proExpiryTimestampFlow: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[PRO_EXPIRY_TIMESTAMP_KEY] ?: 0L
    }

    val proActivationTimestampFlow: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[PRO_ACTIVATION_TIMESTAMP_KEY] ?: 0L
    }

    val themeModeFlow: Flow<AppThemeMode> = context.dataStore.data.map { prefs ->
        val raw = prefs[THEME_KEY] ?: AppThemeMode.SYSTEM.name
        try {
            AppThemeMode.valueOf(raw)
        } catch (_: Exception) {
            AppThemeMode.SYSTEM
        }
    }

    val languageFlow: Flow<AppLanguage> = context.dataStore.data.map { prefs ->
        val raw = prefs[LANGUAGE_KEY] ?: AppLanguage.ENGLISH.name
        try {
            AppLanguage.valueOf(raw)
        } catch (_: Exception) {
            AppLanguage.ENGLISH
        }
    }

    val numberFormatFlow: Flow<NumberFormatType> = context.dataStore.data.map { prefs ->
        val raw = prefs[NUMBER_FORMAT_KEY] ?: NumberFormatType.INDIAN.name
        try {
            NumberFormatType.valueOf(raw)
        } catch (_: Exception) {
            NumberFormatType.INDIAN
        }
    }

    val defaultCurrencyFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[DEFAULT_CURRENCY_KEY] ?: "INR"
    }

    val favoriteCalculatorsFlow: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[FAVORITES_KEY] ?: setOf(
            "emi", "sip", "gst", "currency", "loan", "scientific"
        )
    }

    suspend fun setThemeMode(mode: AppThemeMode) {
        context.dataStore.edit { it[THEME_KEY] = mode.name }
    }

    suspend fun setLanguage(lang: AppLanguage) {
        context.dataStore.edit { it[LANGUAGE_KEY] = lang.name }
    }

    suspend fun setNumberFormat(format: NumberFormatType) {
        context.dataStore.edit { it[NUMBER_FORMAT_KEY] = format.name }
    }

    suspend fun setDefaultCurrency(curr: String) {
        context.dataStore.edit { it[DEFAULT_CURRENCY_KEY] = curr }
    }

    suspend fun toggleFavorite(calcId: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[FAVORITES_KEY]?.toMutableSet() ?: mutableSetOf(
                "emi", "sip", "gst", "currency", "loan", "scientific"
            )
            if (current.contains(calcId)) {
                current.remove(calcId)
            } else {
                current.add(calcId)
            }
            prefs[FAVORITES_KEY] = current
        }
    }

    suspend fun activateSubscription(durationDays: Long = 365) {
        val now = System.currentTimeMillis()
        val expiryTime = now + (durationDays * 24L * 60L * 60L * 1000L)
        context.dataStore.edit { prefs ->
            prefs[IS_PRO_ACTIVE_KEY] = true
            prefs[PRO_ACTIVATION_TIMESTAMP_KEY] = now
            prefs[PRO_EXPIRY_TIMESTAMP_KEY] = expiryTime
        }
    }

    suspend fun cancelSubscription() {
        context.dataStore.edit { prefs ->
            prefs[IS_PRO_ACTIVE_KEY] = false
            prefs[PRO_ACTIVATION_TIMESTAMP_KEY] = 0L
            prefs[PRO_EXPIRY_TIMESTAMP_KEY] = 0L
        }
    }
}
