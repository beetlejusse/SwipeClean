package com.app.swipeclean.ui.trash

import android.app.PendingIntent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.swipeclean.data.local.TrashDao
import com.app.swipeclean.data.model.TrashEntry
import com.app.swipeclean.data.repository.TrashRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


data class TrashUiState(
    val entries : List<TrashEntry> = emptyList(),
    val totalSizeMb: Float = 0f
)

@HiltViewModel
class TrashViewModel @Inject constructor(
    private val trashRepo: TrashRepository,
    private val trashDao: TrashDao
): ViewModel() {
    val state: StateFlow<TrashUiState> = combine(
        trashRepo.observeAllTrash(),
        trashRepo.observeTrashSizeBytes()
    ) { entries, bytes ->
        TrashUiState(entries = entries, totalSizeMb = (bytes ?: 0L) / 1_048_576f)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TrashUiState())

    //Restore all -> empties the trash table w/o touching mediastore
    fun restoreAll() = viewModelScope.launch {
        state.value.entries.forEach {
            trashRepo.restoreFromTrash(it)
        }
    }

    //hard delete all entries immediately(not even waiting for 30 days)

    private val _deletePendingIntent = MutableStateFlow<PendingIntent?>(null)
    val deletePendingIntent: StateFlow<PendingIntent?> = _deletePendingIntent

    fun deleteAll() = viewModelScope.launch {
        val intent = trashRepo.hardDelete(
            state.value.entries
        )
        _deletePendingIntent.value = intent
    }

    fun onDeleteConfirmed() = viewModelScope.launch {
        trashDao.deleteByUris(state.value.entries.map { it.uri })
    }

}