package com.app.swipeclean.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.swipeclean.data.repository.PreferenceRepository
import com.app.swipeclean.data.repository.TrashRepository
import com.app.swipeclean.domain.StatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val totalPhotos: Int = 0,
    val totalGalleryMb: Float = 0f,
    val trashCount: Int = 0,
    val trashMb: Float = 0f,
    val streak: Int = 0,
    val totalDeleted: Int = 0,
    val totalFreedMb: Float = 0f,
    val isLoading: Boolean = true,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val statsUseCase: StatsUseCase,
    private val trashRepo: TrashRepository,
    private val prefsRepo: PreferenceRepository
): ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        trashRepo.observeTrashCount(),
        trashRepo.observeTrashSizeBytes(),
        statsUseCase.observeTotalDeleted(),
        statsUseCase.observeTotalFreed()
    ) { trashCount, trashBytes, deleted, freed ->
        HomeUiState(
            trashCount = trashCount,
            trashMb = (trashBytes ?: 0L) / 1_048_576f,
            totalDeleted = deleted ?: 0,
            totalFreedMb = (freed ?: 0L) / 1_048_576f,
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    init {
            viewModelScope.launch {
                val stats = statsUseCase.getStats()
                // prefsRepo.setTotalPhotos(stats.totalPhotos)
                // prefsRepo.setTotalGalleryMb(stats.totalGalleryMb)
            }
    }
}