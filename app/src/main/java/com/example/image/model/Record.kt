package com.example.image.model

import androidx.room.Entity
import androidx.room.PrimaryKey
// record entity
@Entity
data class Record(var content: String="") {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var photoName: String?=null
    var date:String = ""
}