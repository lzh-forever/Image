package com.example.image.util

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.contentValuesOf
import java.io.*
import kotlin.math.max

fun compress(bitmap: Bitmap, maxSize: Int = 1024): Bitmap {
    val outputStream = ByteArrayOutputStream()
    val targetSize = 1024 * maxSize
    var quality = 100
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    while (outputStream.toByteArray().size > targetSize) {
        outputStream.reset()
        quality = if (quality >= 10) {
            quality - 10
        } else {
            quality - 1
        }
        if (quality > 0) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        } else {
            throw Exception("upload photo is too big")
        }
    }
    outputStream.close()

    val inputStream = ByteArrayInputStream(outputStream.toByteArray())
    val returnedBitmap = BitmapFactory.decodeStream(inputStream)
    inputStream.close()
    return returnedBitmap

}

fun getBitmapFromUri(
    contentResolver: ContentResolver,
    uri: Uri,
    maxWidth: Int = 1080,
    maxHeight: Int = 2340
): Bitmap? {
    var size = BitmapFactory.Options().run {
        inJustDecodeBounds = true
        inPreferredConfig = Bitmap.Config.ARGB_8888
        val inputStream = contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(inputStream, null, this)
        inputStream?.close()
        val width = outWidth
        val height = outHeight
        max(width / maxWidth, height / maxHeight)
    }
    size = if (size == 0) {
        1
    } else {
        size
    }

    BitmapFactory.Options().apply {
        inSampleSize = size
        inPreferredConfig = Bitmap.Config.ARGB_8888
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream, null, this)
        inputStream?.close()
        bitmap?.let {
            return compress(bitmap)
        }
        return null
    }

}

fun saveBitmapInMedia(bitmap: Bitmap, contentResolver: ContentResolver) {
    val values = contentValuesOf(
        MediaStore.Images.Media.DISPLAY_NAME to "Image${System.currentTimeMillis()}.jpg",
        MediaStore.Images.Media.MIME_TYPE to "image/jpeg"
    )
    contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)?.let { uri ->
        contentResolver.openOutputStream(uri)?.let { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream)
            outputStream.close()
        }
    }
}

fun saveBitmapInternal(bitmap: Bitmap, context: Context):String{
    val filename = "Image${System.currentTimeMillis()}.jpg"
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream)
    context.openFileOutput(filename, Context.MODE_PRIVATE).use {
        it.write(outputStream.toByteArray())
    }
    return filename
}
