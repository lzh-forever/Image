package com.example.image.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.image.model.Record

@Dao
interface RecordDao {
    @Insert
    suspend fun insertRecords(records: List<Record>)

    @Insert
    suspend fun insertRecord(record: Record)

    @Update
    suspend fun updateRecord(record: Record)

    @Query("SELECT * FROM record")
    fun getRecords(): PagingSource<Int, Record>

    @Query("DELETE FROM record")
    suspend fun deleteAll()
}