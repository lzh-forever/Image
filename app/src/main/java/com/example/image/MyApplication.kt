package com.example.image

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
// save the app context, in case using context somewhere
class MyApplication:Application() {
    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var context:Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}