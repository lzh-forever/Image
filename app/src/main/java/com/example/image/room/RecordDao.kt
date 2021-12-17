package com.example.image.room

import androidx.paging.PagingSource
import androidx.room.*
import com.example.image.model.Record

@Dao
interface RecordDao {
    @Insert
    suspend fun insertRecords(records: List<Record>)

    @Insert
    suspend fun insertRecord(record: Record)

    @Update
    suspend fun updateRecord(record: Record)

    @Query("SELECT * FROM record order by date desc")
    fun getRecords(): PagingSource<Int, Record>

    @Query("DELETE FROM record")
    suspend fun deleteAll()

    @Query("delete from record where id==:id")
    suspend fun deleteRecord(id: Long)
}