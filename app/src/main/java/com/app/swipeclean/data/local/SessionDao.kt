package com.app.swipeclean.data.local

import androidx.room.*
import com.app.swipeclean.data.model.SessionRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {

    @Insert
    suspend fun insertSession(session: SessionRecord)

    @Query("SELECT * FROM sessions ORDER BY startedAt DESC")
    fun observeAllSessions(): Flow<List<SessionRecord>>

    //streak: get distinct date that had atleast 1 session
    @Query("SELECT DISTINCT date FROM sessions ORDER BY date DESC")
    suspend fun getActiveDates(): List<String>

    // for the freed-storage bar chart - sum per day
    @Query("SELECT date, SUM(bytesFreed) as total FROM sessions GROUP BY date ORDER BY date DESC LIMIT 30")
    fun observeDailyFreed(): Flow<List<DailyFreed>>

    //grand total for stat screen header
    @Query("SELECT SUM(bytesFreed) FROM sessions")
    fun observeTotalBytesFreed(): Flow<Long?>

    // keep track of total photos deleted
    @Query("SELECT SUM(photosDeleted) FROM sessions")
    fun observeTotalPhotosDeleted(): Flow<Int?>
}

data class DailyFreed(
    val date: String,
    val total: Long
)