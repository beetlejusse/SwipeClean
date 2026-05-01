package com.app.swipeclean.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.swipeclean.data.local.DailyFreed
import com.app.swipeclean.data.model.SessionRecord
import com.app.swipeclean.data.repository.SessionRepository
import com.app.swipeclean.domain.StatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class StatsUIState(
    val streak: Int = 0,
    val totalFreedBytes: Long = 0L,
    val totalDeleted: Int = 0,
    val session: List<SessionRecord> = emptyList(),
    val dailyFreed: List<DailyFreed> = emptyList()
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val statsUseCase: StatsUseCase,
    private val sessionRepo: SessionRepository
): ViewModel() {
    val state: StateFlow<StatsUIState> = combine (
        statsUseCase.observeStreak(),
        statsUseCase.observeTotalFreed(),
        statsUseCase.observeTotalDeleted(),
        sessionRepo.observeAllSessions(),
        sessionRepo.observeDailyFreed()
    ) {streak, freed, deleted, sessions, dailyFreed ->
        StatsUIState(
            streak = streak,
            totalFreedBytes = freed ?: 0L,
            totalDeleted = deleted ?: 0,
            session = sessions,
            dailyFreed = dailyFreed
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StatsUIState())
}