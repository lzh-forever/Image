package com.example.image.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.image.model.Photo

@Dao
interface PhotoDao {
    @Insert
    suspend fun insertPhotos(photos: List<Photo>)

    @Insert
    suspend fun insertPhoto(photo: Photo)

    @Query("SELECT * FROM photo")
    fun getPhotos(): PagingSource<Int, Photo>

    @Query("DELETE FROM photo")
    suspend fun deleteAll()
}