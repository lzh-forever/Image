package com.example.image.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.image.Repository

class SecondViewModel:ViewModel() {

    fun  getPagingData() = Repository.getPagingData().cachedIn(viewModelScope)

}