package com.example.image.ui.process

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class ProcessViewModel:ViewModel() {
    var uri: Uri? = null
    val STATE_TO_LOAD = 0
    val STATE_TO_PROCESS = 1
    val STATE_PROCESSED = 2
    var saved = false
    var savedUri : Uri? =null

    val state = MutableLiveData<Int>(STATE_TO_LOAD)
    var processBitmap:Bitmap? = null

}