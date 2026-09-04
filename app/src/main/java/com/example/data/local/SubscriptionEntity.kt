package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room database entity storing subscription state, verification records,
 * and gated features locally.
 */
@Entity(tableName = "user_subscriptions")
data class SubscriptionEntity(
    @PrimaryKey
    val userId: String = "default_user",
    val paymentTransactionId: String,
    val planId: String = "pro_annual_199",
    val status: String = "ACTIVE", // ACTIVE, EXPIRED, CANCELLED, PENDING_VERIFICATION
    val activationTimestamp: Long = System.currentTimeMillis(),
    val expiryTimestamp: Long = System.currentTimeMillis() + (365L * 24L * 60L * 60L * 1000L),
    val lastVerifiedTimestamp: Long = System.currentTimeMillis(),
    val isBackendVerified: Boolean = true,
    val verificationSource: String = "BACKEND_API", // BACKEND_API, LOCAL_DATABASE, RAZORPAY_GATEWAY
    val allowedFeatures: String = "ALL_PREMIUM"
) {
    val isExpired: Boolean
        get() = System.currentTimeMillis() >= expiryTimestamp

    val isValidActive: Boolean
        get() = status == "ACTIVE" && !isExpired && paymentTransactionId.isNotBlank()
}
