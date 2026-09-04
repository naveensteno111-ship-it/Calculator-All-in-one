package com.example.util

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.data.local.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utility class to track the payment transaction ID, activation timestamp,
 * and expiry date (365 days from activation) using Jetpack DataStore.
 *
 * Provides [isPremiumEnabled] to strictly validate that payment has been completed
 * and current date is before expiration date.
 */
class SubscriptionManager(private val context: Context) {

    companion object {
        val KEY_ACTIVATION_TIMESTAMP = longPreferencesKey("pref_pro_activation_timestamp")
        val KEY_EXPIRY_TIMESTAMP = longPreferencesKey("pref_pro_expiry_timestamp")
        val KEY_IS_PRO_ACTIVE = booleanPreferencesKey("pref_is_pro_active")
        val KEY_PAYMENT_TRANSACTION_ID = stringPreferencesKey("pref_pro_payment_transaction_id")

        const val DEFAULT_SUBSCRIPTION_DAYS = 365L
        const val ONE_DAY_MILLIS = 24L * 60L * 60L * 1000L

        @Volatile
        private var INSTANCE: SubscriptionManager? = null

        fun getInstance(context: Context): SubscriptionManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SubscriptionManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    /**
     * Activates or extends the 365-day Pro subscription ONLY with a valid payment transaction ID.
     * Records the payment transaction ID, activation timestamp (now), and expiry date (now + 365 days).
     */
    suspend fun activateSubscriptionWithPayment(paymentId: String, durationDays: Long = DEFAULT_SUBSCRIPTION_DAYS): Boolean {
        val cleanId = paymentId.trim()
        if (cleanId.length < 6) {
            return false
        }
        val now = System.currentTimeMillis()
        val expiry = now + (durationDays * ONE_DAY_MILLIS)
        context.dataStore.edit { prefs ->
            prefs[KEY_ACTIVATION_TIMESTAMP] = now
            prefs[KEY_EXPIRY_TIMESTAMP] = expiry
            prefs[KEY_IS_PRO_ACTIVE] = true
            prefs[KEY_PAYMENT_TRANSACTION_ID] = cleanId
        }
        return true
    }

    /**
     * Validates if the premium subscription is currently enabled.
     * Strictly requires:
     * 1. Active flag is true
     * 2. Current system time < stored expiration date
     * 3. A valid payment transaction ID is stored (no free bypass)
     */
    suspend fun isPremiumEnabled(): Boolean {
        val prefs = context.dataStore.data.first()
        val isActive = prefs[KEY_IS_PRO_ACTIVE] ?: false
        val expiry = prefs[KEY_EXPIRY_TIMESTAMP] ?: 0L
        val paymentId = prefs[KEY_PAYMENT_TRANSACTION_ID]
        val now = System.currentTimeMillis()
        return isActive && (now < expiry) && !paymentId.isNullOrBlank()
    }

    /**
     * Reactive Flow validating if premium is currently enabled.
     * Requires active payment transaction ID and valid expiry date.
     */
    val isPremiumEnabledFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        val isActive = prefs[KEY_IS_PRO_ACTIVE] ?: false
        val expiry = prefs[KEY_EXPIRY_TIMESTAMP] ?: 0L
        val paymentId = prefs[KEY_PAYMENT_TRANSACTION_ID]
        val now = System.currentTimeMillis()
        isActive && (now < expiry) && !paymentId.isNullOrBlank()
    }

    /**
     * Flow of the stored payment transaction ID.
     */
    val paymentTransactionIdFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_PAYMENT_TRANSACTION_ID] ?: ""
    }

    /**
     * Flow of the stored activation timestamp.
     */
    val activationTimestampFlow: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[KEY_ACTIVATION_TIMESTAMP] ?: 0L
    }

    /**
     * Flow of the stored expiry timestamp.
     */
    val expiryTimestampFlow: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[KEY_EXPIRY_TIMESTAMP] ?: 0L
    }

    /**
     * Suspended getter for activation timestamp.
     */
    suspend fun getActivationTimestamp(): Long {
        val prefs = context.dataStore.data.first()
        return prefs[KEY_ACTIVATION_TIMESTAMP] ?: 0L
    }

    /**
     * Suspended getter for expiry timestamp.
     */
    suspend fun getExpiryTimestamp(): Long {
        val prefs = context.dataStore.data.first()
        return prefs[KEY_EXPIRY_TIMESTAMP] ?: 0L
    }

    /**
     * Suspended getter for payment transaction ID.
     */
    suspend fun getPaymentTransactionId(): String {
        val prefs = context.dataStore.data.first()
        return prefs[KEY_PAYMENT_TRANSACTION_ID] ?: ""
    }

    /**
     * Suspended calculation of remaining days until expiry.
     */
    suspend fun getRemainingDays(): Long {
        if (!isPremiumEnabled()) return 0L
        val expiry = getExpiryTimestamp()
        val diff = expiry - System.currentTimeMillis()
        return if (diff > 0) diff / ONE_DAY_MILLIS else 0L
    }

    /**
     * Flow emitting remaining days.
     */
    val remainingDaysFlow: Flow<Long> = isPremiumEnabledFlow.map { active ->
        if (!active) 0L else {
            val expiry = getExpiryTimestamp()
            val diff = expiry - System.currentTimeMillis()
            if (diff > 0) diff / ONE_DAY_MILLIS else 0L
        }
    }

    /**
     * Formatted expiry date string (e.g. "03 September 2027").
     */
    suspend fun getFormattedExpiryDate(pattern: String = "dd MMMM yyyy"): String {
        if (!isPremiumEnabled()) return "Not active"
        val expiry = getExpiryTimestamp()
        if (expiry <= 0L) return "Not active"
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return sdf.format(Date(expiry))
    }

    /**
     * Cancels / resets the subscription and clears payment transaction ID.
     */
    suspend fun cancelSubscription() {
        context.dataStore.edit { prefs ->
            prefs[KEY_IS_PRO_ACTIVE] = false
            prefs[KEY_ACTIVATION_TIMESTAMP] = 0L
            prefs[KEY_EXPIRY_TIMESTAMP] = 0L
            prefs[KEY_PAYMENT_TRANSACTION_ID] = ""
        }
    }
}
