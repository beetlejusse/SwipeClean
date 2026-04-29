package com.app.swipeclean.data.repository

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.app.swipeclean.data.model.Photo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaStoreRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // things we want from MediaStore
    private val projection = arrayOf (
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.SIZE,
        MediaStore.Images.Media.DATE_TAKEN,
        MediaStore.Images.Media.MIME_TYPE,
        MediaStore.Images.Media.WIDTH,
        MediaStore.Images.Media.HEIGHT
    )

    //fetches/emits a full list of photos, runs on IO dispatcher, never on main thread because doing so will greeze ui
    fun loadPhotos(): Flow<List<Photo>> = flow {
        val photos = mutableListOf<Photo>()
        
        // Use MediaStore.Files to access more images including app-specific folders
        // This works with MANAGE_MEDIA permission on Android 12+
        val uri = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            MediaStore.Files.getContentUri("external")
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        
        // Add WHERE clause to filter only images (not videos or other files)
        val selection = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            "${MediaStore.Files.FileColumns.MEDIA_TYPE} = ${MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE}"
        } else {
            null
        }
        
        context.contentResolver.query(
            uri,
            projection,
            selection,   // Filter for images only when using Files API
            null,
            "${MediaStore.Images.Media.DATE_TAKEN} DESC, ${MediaStore.Images.Media.DATE_MODIFIED} DESC, ${MediaStore.Images.Media.DATE_ADDED} DESC"
        ) ?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            val dateCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
            val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)
            val widthCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
            val heightCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)

            while(cursor.moveToNext()) {
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    cursor.getLong(idCol)
                )

                photos.add(Photo(
                    uri = contentUri.toString(),
                    displayName = cursor.getString(nameCol) ?: "unknown",
                    sizeBytes = cursor.getLong(sizeCol),
                    dateTaken = cursor.getLong(dateCol),
                    mimeType = cursor.getString(mimeCol) ?: "image/jpeg",
                    width = cursor.getInt(widthCol),
                    height = cursor.getInt(heightCol)
                ))
            }
        }
        emit(photos)
    }.flowOn(Dispatchers.IO) //switching to IO dispatcher for the media queries

    // get total count and size to show on homescreen and stats screen header`
    suspend fun getTotalStats(): Pair<Int, Long> {
        var count = 0; var totalBytes = 0L

        // Use same URI logic as loadPhotos for consistency
        val uri = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            MediaStore.Files.getContentUri("external")
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        
        val selection = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            "${MediaStore.Files.FileColumns.MEDIA_TYPE} = ${MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE}"
        } else {
            null
        }

        context.contentResolver.query (
            uri,
            arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.SIZE),
            selection, null, null
        ) ?. use { cursor ->
            count = cursor.count
            while(cursor.moveToNext()) {
                totalBytes += cursor.getLong (
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                )
            }
        }
        return Pair(count, totalBytes)
    }
}