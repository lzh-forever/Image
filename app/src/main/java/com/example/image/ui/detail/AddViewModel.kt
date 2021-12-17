package com.example.image.ui.detail

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.image.Repository
import com.example.image.model.Record
import kotlinx.coroutines.launch


import java.text.SimpleDateFormat
import java.util.*

class AddViewModel:ViewModel() {

    private val calendar = Calendar.getInstance()
    private val format = SimpleDateFormat("yyyy-MM-dd");

    private val _date = MutableLiveData(getCurDateString())
    val date:LiveData<String> = _date
    var photoName: String? = null
    var uri:Uri? = null

    var update = false
    var record: Record? =null




    private fun  getCurDateString():String{
        return format.format(calendar.time)
    }

    fun setDate(year: Int, month: Int, day: Int){
        calendar.set(year, month, day)
        _date.value = format.format(calendar.time)
    }

    fun setDate(date:String){
        _date.value = date
    }

    fun saveRecord(content:String){
        Log.d("add","${date.value}")
        viewModelScope.launch {
            if (!update){
                Repository.addRecord(photoName, content,date.value!!)
            } else{
                record?.let { record ->
                    record.content = content
                    record.date = date.value!!
                    record.photoName = photoName
                    Repository.updataRecord(record)
                }
            }
        }
    }

    fun deleteRecord(){
        viewModelScope.launch {
            record?.id?.let { id ->
                Repository.deleteRecord(id)
            }
        }
    }

}