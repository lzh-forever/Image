package com.example.image

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.image.model.Photo
import com.example.image.network.Network
import com.example.image.room.AppDatabase
import kotlinx.coroutines.flow.Flow


object Repository {

    private const val PAGE_SIZE = 15
    private val photoDap = AppDatabase.getDatabase(MyApplication.context).photoDao()

    suspend fun getTest() = Network.test()

    suspend fun addPhoto(photo: Photo) =  photoDap.insertPhoto(photo)

    fun getPagingData() : Flow<PagingData<Photo>> {
        return Pager(
           config =  PagingConfig(PAGE_SIZE),
            pagingSourceFactory = { photoDap.getPhotos() }
            ).flow
    }

}