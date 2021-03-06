package com.example.image

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.image.model.Record
import com.example.image.network.Network
import com.example.image.room.AppDatabase
import kotlinx.coroutines.flow.Flow
import java.util.*


object Repository {

    private const val PAGE_SIZE = 20
    private val recordDao = AppDatabase.getDatabase(MyApplication.context).recordDao()

//    suspend fun getTest() = Network.test()

    suspend fun addRecord(photoName:String?, content:String,date:String ) {
        val record = Record()
        record.photoName = photoName
        record.content = content
        record.date = date
        recordDao.insertRecord(record)
    }

    suspend fun updataRecord(record: Record) = recordDao.updateRecord(record)

    suspend fun deleteRecord(id:Long) = recordDao.deleteRecord(id)

    suspend fun getCount() = recordDao.getCount()

    // paging3 api
    fun getPagingData(): Flow<PagingData<Record>> {
        return Pager(
            config = PagingConfig(PAGE_SIZE),
            pagingSourceFactory = { recordDao.getRecords() }
        ).flow
    }

}