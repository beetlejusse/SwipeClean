package com.app.swipeclean.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class SessionRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startedAt: Long,
    val endedAt: Long,
    val photosReviewed: Int, // it counts total number of swipes(left+right)
    val photosDeleted: Int, //left swipe
    val bytesFreed: Long, // Sum of sizeBytes for deleted photos
    val date: String, // 'yyyy-MM-dd' for streak grouping
)
