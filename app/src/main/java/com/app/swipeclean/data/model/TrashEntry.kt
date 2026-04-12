package com.app.swipeclean.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trash")
data class TrashEntry(
    @PrimaryKey
    val uri: String,
    val displayName: String,
    val sizeBytes: Long,
    val deleteAt: Long = System.currentTimeMillis(),  //timestamp of when the photo was deleted
    val purgeAfterMs: Long = deleteAt + 30L * 24 * 60 * 60 * 1000 // default to 30 days after deletion
)
