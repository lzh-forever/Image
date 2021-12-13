package com.example.image.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import kotlin.math.max

fun compress(bitmap: Bitmap, maxSize: Int = 1024): Bitmap {
    val outputStream = ByteArrayOutputStream()
    val targetSize = 1024 * maxSize
    var quality = 100
    bitmap.compress(Bitmap.CompressFormat.JPEG,100, outputStream)
    while ( outputStream.toByteArray().size > targetSize){
        outputStream.reset()
        quality = if ( quality>=10 ){ quality-10 } else { quality-1 }
        if (quality>0) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        } else {
            throw Exception("upload photo is too big")
        }
    }

    val inputStream = ByteArrayInputStream(outputStream.toByteArray())
    outputStream.close()
    return  BitmapFactory.decodeStream(inputStream)

}

fun getBitmapFromInputSteam( inputStream: InputStream, maxWidth: Int = 1080, maxHeight: Int =2340): Bitmap?{
    var size = BitmapFactory.Options().run {
        inJustDecodeBounds = true
        inPreferredConfig = Bitmap.Config.ARGB_8888
        BitmapFactory.decodeStream(inputStream, null , this)
        val width = outWidth
        val height = outHeight
        max(width/maxWidth, height/maxHeight)
    }
    size = if (size ==0){ 1 } else { size }

    BitmapFactory.Options().apply {
        inSampleSize = size
        inPreferredConfig = Bitmap.Config.ARGB_8888
        val bitmap = BitmapFactory.decodeStream(inputStream, null , this)

        bitmap?.let {
            return compress(bitmap)
        }
        return  null
    }

}

