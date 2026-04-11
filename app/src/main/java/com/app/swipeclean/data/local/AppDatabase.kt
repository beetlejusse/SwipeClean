package com.app.swipeclean.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.swipeclean.data.model.TrashEntry
import com.app.swipeclean.data.model.SessionRecord
// version = 1 for first release. Bump + add a Migration whenever schema changes.
@Database(
    entities = [TrashEntry::class, SessionRecord::class],
    version = 1,
    exportSchema = true // Generates schema JSON — good for migration auditing
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trashDao(): TrashDao
    abstract fun sessionDao(): SessionDao
// we dont need no photo dao as they will come from MediaStore query and not stored in our db. We only store deleted photos in trash and session records
}