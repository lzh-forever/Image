package com.example.image.ui

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.*
import com.example.image.Repository
import com.example.image.model.Record
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel:ViewModel() {
    private val _snackbar = MutableLiveData<String?>()
    val snackbar :LiveData<String?>
        get() = _snackbar

    var uri:Uri? = null

    var bitmap:Bitmap? =null

    fun addPhoto(photoName:String,content:String){
        viewModelScope.launch {
            Repository.addRecord(photoName, content)
        }
    }

    private val clicked = MutableStateFlow(false)

//    val data = liveData {
//            emit(Repository.getTest().catch { cause: Throwable -> _snackbar.value=cause.message })
//    }
//
//    fun changeClicked(){
//        clicked.value= !clicked.value
//    }

    fun onSnackbarShown(){
        _snackbar.value= null
    }
}