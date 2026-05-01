package com.app.swipeclean.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.swipeclean.data.repository.PreferenceRepository
import com.app.swipeclean.data.repository.TrashRepository
import com.app.swipeclean.domain.StatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val totalPhotos: Int = 0,
    val totalGalleryBytes: Long = 0L,
    val trashCount: Int = 0,
    val trashBytes: Long = 0L,
    val streak: Int = 0,
    val totalDeleted: Int = 0,
    val totalFreedBytes: Long = 0L,
    val isLoading: Boolean = true,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val statsUseCase: StatsUseCase,
    private val trashRepo: TrashRepository,
    private val prefsRepo: PreferenceRepository
): ViewModel() {

    private val _photoStats = MutableStateFlow<Pair<Int, Long>>(Pair(0, 0L))

    val uiState: StateFlow<HomeUiState> = combine(
        _photoStats,
        statsUseCase.observeStreak(),
        trashRepo.observeTrashCount(),
        trashRepo.observeTrashSizeBytes(),
        statsUseCase.observeTotalDeleted(),
        statsUseCase.observeTotalFreed()
    ) { args: Array<Any?> ->
        val  photoStats = args[0] as Pair<Int, Long>
        val streak = args[1] as Int
        val trashCount = args[2] as Int
        val trashBytes = args[3] as? Long ?: 0L
        val deleted = args[4] as? Int ?: 0
        val freed = args[5] as? Long ?: 0L
        HomeUiState(
            totalPhotos = photoStats.first,
            totalGalleryBytes = photoStats.second,
            streak = streak,
            trashCount = trashCount,
            trashBytes = trashBytes,
            totalDeleted = deleted,
            totalFreedBytes = freed,
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            val stats = statsUseCase.getStats()
            _photoStats.value = Pair(stats.totalPhotos, stats.totalGalleryBytes)
        }
    }

    fun refreshStats() {
        loadStats()
    }
}