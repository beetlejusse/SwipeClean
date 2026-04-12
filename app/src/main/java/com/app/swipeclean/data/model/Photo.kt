package com.app.swipeclean.data.model

data class Photo(
    val uri: String,
    val displayName: String,
    val sizeBytes: Long,
    val dateTaken: Long,
    val mimeType: String = "image/jpeg",
    val width: Int = 0,
    val height: Int = 0
)
