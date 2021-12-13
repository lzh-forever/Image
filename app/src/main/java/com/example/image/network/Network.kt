package com.example.image.network

import android.util.Log
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

object Network {
    private val testService = MyRetrofit.create<TestService>()
    suspend fun test() = flow {
        val response=testService.test()
        if (response.code==0){
            Log.d("Network","${response.data}, ${response.code} ,${response.msg}")
            emit(response.data)
        } else{
            throw Exception(response.msg)
        }
        }
}