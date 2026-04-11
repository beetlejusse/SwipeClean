package com.app.swipeclean.data.local

import androidx.room.*
import com.app.swipeclean.data.model.TrashEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface TrashDao {
    //inserting a newly soft-deleted photo into trash
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrashEntry(entry: TrashEntry)

    // now remove from trash
    @Delete
    suspend fun deleteTrashEntry(entry: TrashEntry)

    // observe all trash entries - Flow auto-emits on any change (insert/delete)
    @Query("SELECT * FROM trash ORDER BY deleteAt DESC")
    fun observeAllTrash(): Flow<List<TrashEntry>>

    // get all entries that are due for permanent deletion (purgeAfterMs <= now)
    @Query("SELECT * FROM trash WHERE purgeAfterMs <= :now")
    suspend fun getExpiredEntries(now: Long): List<TrashEntry>

    // hard-deleting rows from room after mediastore deletion completes
    @Query("DELETE FROM trash WHERE uri IN (:uris)")
    suspend fun deleteByUris(uris: List<String>)

    //counting for the homescreen badge
    @Query("SELECT COUNT(*) FROM trash")
    fun observeTrashCount(): Flow<Int>

    //t0 display total size of items in trash
    @Query("SELECT SUM(sizeBytes) FROM trash")
    fun observeTrashSizeBytes(): Flow<Long?>
}