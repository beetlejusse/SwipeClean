package com.app.swipeclean.domain

import com.app.swipeclean.data.model.Photo
import com.app.swipeclean.data.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

// Loads photos from MediaStore, filtering out those already in trash.
// The ViewModel only sees the filtered list — no trash photos appear in the swipe queue.
class LoadPhotoUseCase @Inject constructor(
    private val mediaRepo: MediaStoreRepository,
    private val trashRepo: TrashRepository
) {
    operator fun invoke(): Flow<List<Photo>> = combine (
        mediaRepo.loadPhotos(),
        trashRepo.observeAllTrash()
    ) {
        allPhotos, trashEntries ->
        val trashUris = trashEntries.map { it.uri }.toSet()

        //return only photos whose URIs are NOT in the trash
        allPhotos.filter { it.uri !in trashUris }
    }
}