package com.app.swipeclean.util

fun formatFileSize(bytes: Long): String {
    val mb = bytes / 1_048_576f
    return when {
        mb < 1f -> "< 1 MB"  // Very small files
        mb < 1024f -> "%.0f MB".format(mb)  // 1 MB to 1023 MB
        else -> "%.1f GB".format(mb / 1024f)  // 1 GB and above
    }
}
