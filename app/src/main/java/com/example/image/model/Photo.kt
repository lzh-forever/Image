package com.example.image.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Photo(val name:String){
    @PrimaryKey(autoGenerate = true)
    var id:Long =0
}