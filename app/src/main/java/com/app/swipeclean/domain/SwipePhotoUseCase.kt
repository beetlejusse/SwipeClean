package com.app.swipeclean.domain

import com.app.swipeclean.data.model.*
import com.app.swipeclean.data.repository.TrashRepository
import javax.inject.Inject

data class UndoToken(val entry: TrashEntry)  //opaque token for undo

class SwipePhotoUseCase @Inject constructor(
    private val trashRepo: TrashRepository
) {
    // Called on LEFT swipe — moves photo to soft-trash
    // Returns an UndoToken so the action can be reversed
    suspend fun swipeDelete(photo: Photo): UndoToken {
        val entry = TrashEntry(
            uri = photo.uri,
            displayName = photo.displayName,
            sizeBytes = photo.sizeBytes
        )
        trashRepo.softDelete(entry)
        return UndoToken(entry)
    }

    // Called on RIGHT swipe — nothing persisted, just advance the queue
    suspend fun swipeKeep(photo: Photo) {
        // No-op for now, but could log or track if desired
        //if photo is right swiped nothing happens, we just move on to the next photo, so no need to do anything here
    }

    suspend fun undo(token: UndoToken) = trashRepo.restoreFromTrash(token.entry)
}