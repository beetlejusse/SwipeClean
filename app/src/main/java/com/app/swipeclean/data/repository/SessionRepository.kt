package com.app.swipeclean.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.app.swipeclean.data.local.DailyFreed
import com.app.swipeclean.data.local.SessionDao
import com.app.swipeclean.data.model.SessionRecord
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepository @Inject constructor(
    private val sessionDao: SessionDao
) {
    private val dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    suspend fun saveSession(session: SessionRecord) = sessionDao.insertSession(session)
    fun observeAllSessions(): Flow<List<SessionRecord>> = sessionDao.observeAllSessions()
    fun observeDailyFreed(): Flow<List<DailyFreed>> = sessionDao.observeDailyFreed()
    fun observeTotalFreed(): Flow<Long?> = sessionDao.observeTotalBytesFreed()
    fun observeTotalDeleted():Flow<Int?> = sessionDao.observeTotalPhotosDeleted()

    // Calculate streak: how many consecutive days (ending today) had a session
    suspend fun calculateStreak() : Int {
        val activeDates = sessionDao.getActiveDates()
        if(activeDates.isEmpty()) return 0
        var streak = 0
        var expectedDate = LocalDate.now()
        for(dateStr in activeDates){
            val date = LocalDate.parse(dateStr, dateFmt)
            if(date == expectedDate) {
                streak++
                expectedDate = expectedDate.minusDays(1)
            } else {
                break
            }
        }
        return streak
    }
    fun todayDateString(): String = LocalDate.now().format(dateFmt)
}