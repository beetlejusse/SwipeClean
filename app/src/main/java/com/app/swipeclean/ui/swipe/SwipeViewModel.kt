package com.app.swipeclean.ui.swipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.swipeclean.data.model.Photo
import dagger.hilt.android.lifecycle.HiltViewModel
import com.app.swipeclean.data.repository.SessionRepository
import com.app.swipeclean.domain.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SwipeUiState(
    val photos: List<Photo> = emptyList(),
    val currentIndex: Int = 0,
    val deletedCount: Int = 0,
    val bytesFreed: Long = 0L,
    val canUndo: Boolean = false,
    val sessionGoal: Int = 0,
    val isComplete: Boolean = false, // All photos swiped
    val isLoading: Boolean = true,
)
@HiltViewModel

class SwipeViewModel @Inject constructor(
    private val loadPhotos: LoadPhotoUseCase,
    private val swipePhoto: SwipePhotoUseCase,
    private val sessionRepo: SessionRepository
    ): ViewModel() {
    private val _state = MutableStateFlow(SwipeUiState())
    val state: StateFlow<SwipeUiState> = _state.asStateFlow()

    //making sure UNDO stack holds upto 10 recent deleted items
    private val undoStack = ArrayDeque<UndoToken>(10)
    private var sessionStart = System.currentTimeMillis()
    init{ loadAllPhotos() }

    private fun loadAllPhotos() {
        viewModelScope.launch {
            loadPhotos().collectLatest { photos ->
                _state.update { it.copy(photos = photos, isLoading = false) }
            }
        }
    }

    // below function will be called when user swipes left i.e. delete
    fun onSwipeLeft() {
        val photo = currentPhoto() ?: return
        viewModelScope.launch {
            val token = swipePhoto.swipeDelete(photo)
            undoStack.addFirst(token)
            if(undoStack.size > 10) undoStack.removeLast()
            _state.update {
                s -> s.copy(
                    currentIndex = s.currentIndex + 1,
                    deletedCount = s.deletedCount + 1,
                    bytesFreed = s.bytesFreed + photo.sizeBytes,
                    canUndo = true,
                    isComplete = s.currentIndex + 1 >= s.photos.size
                )
            }
        }
    }

    //below function will be called when usr swipes right
    fun onSwipeRight() {
        viewModelScope.launch {
            swipePhoto.swipeKeep(currentPhoto() ?: return@launch)
            _state.update {
                s-> s.copy(
                    currentIndex = s.currentIndex + 1,
                    isComplete = s.currentIndex + 1 >= s.photos.size
                )
            }
        }
    }

    //below function indo the recent left swipe
    fun onUndo() {
        if(undoStack.isEmpty()) return
        val token = undoStack.removeFirst()
        viewModelScope.launch {
            swipePhoto.undo(token)
            _state.update { s -> s.copy(
                currentIndex = (s.currentIndex - 1).coerceAtLeast(0),
                deletedCount = (s.deletedCount - 1).coerceAtLeast(0),
                bytesFreed = (s.bytesFreed - token.entry.sizeBytes).coerceAtLeast(0),
                canUndo = undoStack.isNotEmpty(),
                isComplete = false
            )}
        }
    }

    //below function makes sure that it save session to room when user leaves the screen
    fun saveSession() {
        val s = _state.value
        if(s.deletedCount == 0 && s.currentIndex == 0) return //nothing happened
        viewModelScope.launch {
            sessionRepo.saveSession(com.app.swipeclean.data.model.SessionRecord(
                startedAt = sessionStart,
                endedAt = System.currentTimeMillis(),
                photosReviewed = s.currentIndex,
                photosDeleted = s.deletedCount,
                bytesFreed = s.bytesFreed,
                date = sessionRepo.todayDateString()
            ))
        }
    }

    private fun currentPhoto(): Photo? {
        val s = _state.value
        return s.photos.getOrNull(s.currentIndex)
    }

    //auto save when view model is cleared(user backs out)
    override fun onCleared() { saveSession(); super.onCleared() }
}