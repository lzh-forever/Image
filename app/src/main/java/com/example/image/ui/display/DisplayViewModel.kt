package com.example.image.ui.display

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.image.Repository

class DisplayViewModel:ViewModel() {

    fun  getPagingData() = Repository.getPagingData().cachedIn(viewModelScope)

}