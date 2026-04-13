package com.app.swipeclean.domain

import android.content.Context
import android.net.Uri
import com.app.swipeclean.data.model.Photo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import androidx.core.net.toUri
import java.security.MessageDigest

data class DuplicateGroup(val photos: List<Photo>) // All photos in a group are duplicates
class DuplicateDetectorUseCase @Inject constructor(
    @ApplicationContext private val context: Context
)
{
    //below is the function to return 2 same photos or duplicates
    // it uses sha256 hash of the photo content to find duplicates, which is more reliable than just comparing metadata like name or size
    //also runs on IO dispatcher since it involves reading photo data, which can be slow and should not block the main thread
    suspend fun findDuplicates(photos: List<Photo>): List<DuplicateGroup> = withContext(Dispatchers.IO) {
        // first filter -> group by size of file
        val bySizeGroups =  photos.groupBy { it.sizeBytes }.filter {
            it.value.size > 1 // only keep groups with more than 1 photo, since single photos can't be duplicates
        }

        val duplicateGroups = mutableListOf<DuplicateGroup>()
        for((_, sizeGroup) in bySizeGroups){
            //for each sizeGroup use sha256 to confirm exact match
            val byHash = sizeGroup.groupBy { photo ->
                computeHash(photo.uri.toUri())
            }.filter { it.value.size > 1 }

            duplicateGroups.addAll(
                byHash.values.map { DuplicateGroup(it) }
            )
        }
        duplicateGroups
    }

    private fun computeHash(uri: Uri): String {
        val md = MessageDigest.getInstance("SHA-256")
        context.contentResolver.openInputStream(uri)?.use { stream ->
            val buffer = ByteArray(8192)
            var read: Int
            while(stream.read(buffer).also { read = it } != -1) {
                md.update(buffer, 0, read)
            }
        }
        return md.digest().joinToString("") { "%02x".format(it) }
    }
}