package com.example.image.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.image.model.Record

@Database(version = 1, entities = [Record::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao

    companion object {

        private var instance: AppDatabase? = null

        @Synchronized
        fun getDatabase(applicationContext: Context): AppDatabase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(applicationContext, AppDatabase::class.java,"app_database").build()
                .apply {
                    instance = this
                }
        }
    }

}