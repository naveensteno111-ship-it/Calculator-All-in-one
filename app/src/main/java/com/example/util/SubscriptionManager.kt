package com.example.util

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.data.local.AppDatabase
import com.example.data.local.SubscriptionDao
import com.example.data.local.SubscriptionEntity
import com.example.data.local.dataStore
import com.example.data.remote.SubscriptionBackendService
import com.example.domain.PremiumFeature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * SubscriptionManager handles the state of user subscriptions by verifying against
 * a remote backend verification service and persisting to the local Room database
 * and DataStore to gate access to premium calculator features.
 */
class SubscriptionManager private constructor(private val context: Context) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val database: AppDatabase = AppDatabase.getDatabase(context)
    private val subscriptionDao: SubscriptionDao = database.subscriptionDao()
    private val backendService: SubscriptionBackendService = SubscriptionBackendService()

    sealed class SubscriptionState {
        object Loading : SubscriptionState()
        data class Inactive(val reason: String = "Subscription required to access features") : SubscriptionState()
        data class Active(
            val subscription: SubscriptionEntity,
            val remainingDays: Long,
            val isBackendVerified: Boolean
        ) : SubscriptionState()
        data class Expired(val expiredOn: Long) : SubscriptionState()
    }

    private val _subscriptionState = MutableStateFlow<SubscriptionState>(SubscriptionState.Loading)
    val subscriptionState: StateFlow<SubscriptionState> = _subscriptionState.asStateFlow()

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

    init {
        scope.launch {
            refreshSubscriptionState()
        }
    }

    /**
     * Refreshes subscription state by checking against the local Room database,
     * fallback to DataStore, and validating timestamps.
     */
    suspend fun refreshSubscriptionState(userId: String = "default_user"): SubscriptionState {
        // 1. Check local Room database first
        val dbRecord = try {
            subscriptionDao.getSubscription(userId)
        } catch (_: Exception) {
            null
        }

        val now = System.currentTimeMillis()

        if (dbRecord != null && dbRecord.isValidActive) {
            val remaining = (dbRecord.expiryTimestamp - now).coerceAtLeast(0L) / ONE_DAY_MILLIS
            val state = SubscriptionState.Active(
                subscription = dbRecord,
                remainingDays = remaining,
                isBackendVerified = dbRecord.isBackendVerified
            )
            _subscriptionState.value = state
            return state
        }

        // 2. Check DataStore cache
        val prefs = context.dataStore.data.first()
        val isActive = prefs[KEY_IS_PRO_ACTIVE] ?: false
        val expiry = prefs[KEY_EXPIRY_TIMESTAMP] ?: 0L
        val paymentId = prefs[KEY_PAYMENT_TRANSACTION_ID] ?: ""

        if (isActive && (now < expiry) && paymentId.isNotBlank()) {
            val entity = SubscriptionEntity(
                userId = userId,
                paymentTransactionId = paymentId,
                status = "ACTIVE",
                activationTimestamp = prefs[KEY_ACTIVATION_TIMESTAMP] ?: now,
                expiryTimestamp = expiry,
                lastVerifiedTimestamp = now,
                isBackendVerified = true,
                verificationSource = "LOCAL_CACHE"
            )
            // Ensure synced to Room
            try {
                subscriptionDao.insertOrUpdate(entity)
            } catch (_: Exception) {}

            val remaining = (expiry - now).coerceAtLeast(0L) / ONE_DAY_MILLIS
            val state = SubscriptionState.Active(
                subscription = entity,
                remainingDays = remaining,
                isBackendVerified = true
            )
            _subscriptionState.value = state
            return state
        }

        val state = if (expiry > 0L && now >= expiry) {
            SubscriptionState.Expired(expiredOn = expiry)
        } else {
            SubscriptionState.Inactive()
        }
        _subscriptionState.value = state
        return state
    }

    /**
     * Activates subscription by verifying against backend service and persisting
     * to Room database and DataStore.
     */
    suspend fun activateSubscriptionWithPayment(
        paymentId: String,
        durationDays: Long = DEFAULT_SUBSCRIPTION_DAYS,
        userId: String = "default_user"
    ): Boolean {
        val cleanId = paymentId.trim()
        if (cleanId.length < 6) return false

        // 1. Verify against Backend Verification API
        val backendResult = backendService.verifyWithBackend(cleanId, userId)
        if (!backendResult.isValid) {
            return false
        }

        val now = System.currentTimeMillis()
        val expiry = if (backendResult.expiryTimestamp > 0) backendResult.expiryTimestamp else (now + (durationDays * ONE_DAY_MILLIS))

        // 2. Persist to Room local database
        val entity = SubscriptionEntity(
            userId = userId,
            paymentTransactionId = cleanId,
            planId = backendResult.planId,
            status = "ACTIVE",
            activationTimestamp = now,
            expiryTimestamp = expiry,
            lastVerifiedTimestamp = now,
            isBackendVerified = backendResult.isSuccess,
            verificationSource = "BACKEND_API",
            allowedFeatures = "ALL_PREMIUM"
        )
        try {
            subscriptionDao.insertOrUpdate(entity)
        } catch (_: Exception) {}

        // 3. Persist to DataStore
        context.dataStore.edit { prefs ->
            prefs[KEY_ACTIVATION_TIMESTAMP] = now
            prefs[KEY_EXPIRY_TIMESTAMP] = expiry
            prefs[KEY_IS_PRO_ACTIVE] = true
            prefs[KEY_PAYMENT_TRANSACTION_ID] = cleanId
        }

        val remaining = (expiry - now).coerceAtLeast(0L) / ONE_DAY_MILLIS
        _subscriptionState.value = SubscriptionState.Active(
            subscription = entity,
            remainingDays = remaining,
            isBackendVerified = true
        )
        return true
    }

    /**
     * Gates access to a specific premium calculator feature.
     * Returns true if user has active subscription; otherwise false.
     */
    suspend fun isFeatureAccessible(feature: PremiumFeature): Boolean {
        val active = isPremiumEnabled()
        if (!active) return false
        val state = _subscriptionState.value
        return if (state is SubscriptionState.Active) {
            state.subscription.allowedFeatures == "ALL_PREMIUM" || 
            state.subscription.allowedFeatures.contains(feature.featureId)
        } else {
            active
        }
    }

    /**
     * Executes the protected block if feature is unlocked, or returns the denied block.
     */
    suspend fun <T> gateFeature(
        feature: PremiumFeature,
        onGranted: suspend () -> T,
        onDenied: suspend (String) -> T
    ): T {
        return if (isFeatureAccessible(feature)) {
            onGranted()
        } else {
            onDenied("Access to ${feature.displayName} is locked. Active ₹199/year subscription is required.")
        }
    }

    /**
     * Validates if the premium subscription is currently enabled.
     * Strictly requires:
     * 1. Active flag is true
     * 2. Current system time < stored expiration date
     * 3. A valid payment transaction ID is stored
     */
    suspend fun isPremiumEnabled(): Boolean {
        val state = _subscriptionState.value
        if (state is SubscriptionState.Active && state.subscription.isValidActive) {
            return true
        }
        val prefs = context.dataStore.data.first()
        val isActive = prefs[KEY_IS_PRO_ACTIVE] ?: false
        val expiry = prefs[KEY_EXPIRY_TIMESTAMP] ?: 0L
        val paymentId = prefs[KEY_PAYMENT_TRANSACTION_ID]
        val now = System.currentTimeMillis()
        return isActive && (now < expiry) && !paymentId.isNullOrBlank()
    }

    /**
     * Reactive Flow validating if premium is currently enabled.
     */
    val isPremiumEnabledFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        val isActive = prefs[KEY_IS_PRO_ACTIVE] ?: false
        val expiry = prefs[KEY_EXPIRY_TIMESTAMP] ?: 0L
        val paymentId = prefs[KEY_PAYMENT_TRANSACTION_ID]
        val now = System.currentTimeMillis()
        isActive && (now < expiry) && !paymentId.isNullOrBlank()
    }

    val localSubscriptionEntityFlow: Flow<SubscriptionEntity?> = subscriptionDao.getSubscriptionFlow("default_user")

    val paymentTransactionIdFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_PAYMENT_TRANSACTION_ID] ?: ""
    }

    val activationTimestampFlow: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[KEY_ACTIVATION_TIMESTAMP] ?: 0L
    }

    val expiryTimestampFlow: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[KEY_EXPIRY_TIMESTAMP] ?: 0L
    }

    suspend fun getActivationTimestamp(): Long {
        val prefs = context.dataStore.data.first()
        return prefs[KEY_ACTIVATION_TIMESTAMP] ?: 0L
    }

    suspend fun getExpiryTimestamp(): Long {
        val prefs = context.dataStore.data.first()
        return prefs[KEY_EXPIRY_TIMESTAMP] ?: 0L
    }

    suspend fun getPaymentTransactionId(): String {
        val prefs = context.dataStore.data.first()
        return prefs[KEY_PAYMENT_TRANSACTION_ID] ?: ""
    }

    suspend fun getRemainingDays(): Long {
        if (!isPremiumEnabled()) return 0L
        val expiry = getExpiryTimestamp()
        val diff = expiry - System.currentTimeMillis()
        return if (diff > 0) diff / ONE_DAY_MILLIS else 0L
    }

    val remainingDaysFlow: Flow<Long> = isPremiumEnabledFlow.map { active ->
        if (!active) 0L else {
            val expiry = getExpiryTimestamp()
            val diff = expiry - System.currentTimeMillis()
            if (diff > 0) diff / ONE_DAY_MILLIS else 0L
        }
    }

    suspend fun getFormattedExpiryDate(pattern: String = "dd MMMM yyyy"): String {
        if (!isPremiumEnabled()) return "Not active"
        val expiry = getExpiryTimestamp()
        if (expiry <= 0L) return "Not active"
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return sdf.format(Date(expiry))
    }

    /**
     * Cancels / resets the subscription in both local Room database and DataStore.
     */
    suspend fun cancelSubscription(userId: String = "default_user") {
        try {
            subscriptionDao.deleteSubscription(userId)
        } catch (_: Exception) {}

        context.dataStore.edit { prefs ->
            prefs[KEY_IS_PRO_ACTIVE] = false
            prefs[KEY_ACTIVATION_TIMESTAMP] = 0L
            prefs[KEY_EXPIRY_TIMESTAMP] = 0L
            prefs[KEY_PAYMENT_TRANSACTION_ID] = ""
        }
        _subscriptionState.value = SubscriptionState.Inactive("Subscription reset by user")
    }
}
