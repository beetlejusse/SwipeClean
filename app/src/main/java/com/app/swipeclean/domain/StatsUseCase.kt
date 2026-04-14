package com.app.swipeclean.domain

import com.app.swipeclean.data.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

data class AppStats(
    val totalPhotos: Int,
    val totalGalleryBytes: Long,
    val totalDeletedAllTime: Int,
    val totalFreedBytesAllTime: Long,
    val currentStreak: Int
)
class StatsUseCase @Inject constructor(
    private val mediaRepo: MediaStoreRepository,
    private val sessionRepo: SessionRepository
) {
    suspend fun getStats(): AppStats {
        val (photoCount, galleryBytes) = mediaRepo.getTotalStats()
        val streak = sessionRepo.calculateStreak()

        return AppStats (
            totalPhotos = photoCount,
            totalGalleryBytes = galleryBytes,
            totalDeletedAllTime = sessionRepo.observeTotalDeleted().firstOrNull() ?: 0,
            totalFreedBytesAllTime = sessionRepo.observeTotalFreed().firstOrNull() ?: 0L,
            currentStreak = streak
        )
    }

    fun observeTotalFreed(): Flow<Long?> = sessionRepo.observeTotalFreed()
    fun observeTotalDeleted(): Flow<Int?> = sessionRepo.observeTotalDeleted()
}