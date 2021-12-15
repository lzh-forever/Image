package com.example.image.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Record(var content: String="") {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var photoName: String?=null
    var year: Int = 1970
    var month: Int = 1
    var day: Int = 1
}