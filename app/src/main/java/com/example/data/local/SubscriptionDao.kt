package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for local subscription state verification and caching.
 */
@Dao
interface SubscriptionDao {

    @Query("SELECT * FROM user_subscriptions WHERE userId = :userId LIMIT 1")
    fun getSubscriptionFlow(userId: String = "default_user"): Flow<SubscriptionEntity?>

    @Query("SELECT * FROM user_subscriptions WHERE userId = :userId LIMIT 1")
    suspend fun getSubscription(userId: String = "default_user"): SubscriptionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(subscription: SubscriptionEntity)

    @Update
    suspend fun update(subscription: SubscriptionEntity)

    @Query("UPDATE user_subscriptions SET status = :status WHERE userId = :userId")
    suspend fun updateStatus(status: String, userId: String = "default_user")

    @Query("DELETE FROM user_subscriptions WHERE userId = :userId")
    suspend fun deleteSubscription(userId: String = "default_user")

    @Query("DELETE FROM user_subscriptions")
    suspend fun clearAll()
}
