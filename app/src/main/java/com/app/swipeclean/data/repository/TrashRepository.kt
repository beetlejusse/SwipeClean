package com.app.swipeclean.data.repository

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.app.swipeclean.data.local.TrashDao
import com.app.swipeclean.data.model.TrashEntry
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.net.toUri

@Singleton
class TrashRepository @Inject constructor(
    private val trashDao: TrashDao,
    @ApplicationContext private val context: Context
) {

    fun observeAllTrash(): Flow<List<TrashEntry>> = trashDao.observeAllTrash()
    fun observeTrashCount(): Flow<Int> = trashDao.observeTrashCount()
    fun observeTrashSizeBytes(): Flow<Long?> = trashDao.observeTrashSizeBytes()

    // below function will be called when user swipes left - soft delete
    suspend fun softDelete(entry: TrashEntry) {
        trashDao.insertTrashEntry(entry)
        // Note: we do NOT touch MediaStore here.
        // The file stays on device; only Room knows it's 'deleted'.
    }

    // below function will be called when user taps "Undo" just remove from the rooms
    suspend fun restoreFromTrash(entry: TrashEntry) {
        trashDao.deleteTrashEntry(entry)
    }

    // Hard delete: actually remove files from MediaStore + Room
    // Called by WorkManager purger and 'Delete Now' button
    suspend fun hardDelete(entries: List<TrashEntry>) = withContext(Dispatchers.IO) {
        val uris = entries.map{ it.uri.toUri() }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // API 30+: Use createDeleteRequest — system shows confirmation dialog
            // The Activity must handle the resulting IntentSender

            val pendingIntent = MediaStore.createDeleteRequest(
                context.contentResolver, uris
            )

            // Return the PendingIntent to the UI layer to launch
            // (see TrashViewModel for how this is handled)

        } else {
            // API 26-29: Direct ContentResolver deletion
            uris.forEach { uri ->
                try { context.contentResolver.delete(uri, null, null) }
                catch (e: Exception) { /* log and skip */ }
            }
        }
        // Remove from Room regardless of API level
        trashDao.deleteByUris(entries.map { it.uri })
    }
    // Called by WorkManager: find entries past 30-day TTL and purge them
    suspend fun purgeExpired() {
        val expired = trashDao.getExpiredEntries(System.currentTimeMillis())
        if (expired.isNotEmpty()) hardDelete(expired)
    }
}