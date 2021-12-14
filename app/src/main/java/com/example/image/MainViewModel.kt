package com.example.image

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel:ViewModel() {
    private val _snackbar = MutableLiveData<String?>()
    val snackbar :LiveData<String?>
        get() = _snackbar

    var uri:Uri? = null

    var bitmap:Bitmap? =null

    var newBitmap: Bitmap? =null

    private val clicked = MutableStateFlow(false)

    val data = liveData {
            emit(Repository.getTest().catch { cause: Throwable -> _snackbar.value=cause.message })
    }

    fun changeClicked(){
        clicked.value= !clicked.value
    }

    fun onSnackbarShown(){
        _snackbar.value= null
    }
}