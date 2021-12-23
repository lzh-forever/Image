package com.example.image.util

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Rect
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.contentValuesOf
import androidx.exifinterface.media.ExifInterface
import java.io.*
import kotlin.math.max
import kotlin.math.round
import kotlin.math.roundToInt
var quality = 100
// compress the bitmap to a maxSize of 1M
private fun compress(bitmap: Bitmap, maxSize: Int = 500): Bitmap {
    val outputStream = ByteArrayOutputStream()
    val targetSize = 1024 * maxSize
    quality = 100
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    Log.d("util","bitmap ${bitmap.allocationByteCount}")
    while (outputStream.toByteArray().size > targetSize) {
        outputStream.reset()
        quality = if (quality > 10) {
            quality - 10
        } else {
            quality - 1
        }
        if (quality > 0) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            Log.d("util","quality $quality")
            Log.d("util","stream ${outputStream.toByteArray().size}")

        } else {
            throw Exception("upload photo is too big")
        }
    }
    Log.d("util","$quality")
    Log.d("util","${outputStream.toByteArray().size}")

//    val inputStream = ByteArrayInputStream(outputStream.toByteArray())
//    val returnedBitmap = BitmapFactory.decodeByteArray(outputStream.toByteArray(),0,outputStream.toByteArray().size)
//    Log.d("util","bitmap :  ${returnedBitmap.allocationByteCount}" )
//    inputStream.close()
    outputStream.close()
    return bitmap

}

// because we get the image uri from the MediaStore
// we need to open inputStream to get the bitmap into memory
// maxWidth , maxHeight to restrict the sample size
fun getBitmapFromUri(
    contentResolver: ContentResolver,
    uri: Uri,
    maxWidth: Int = 1080,
    maxHeight: Int = 2340
): Bitmap? {
    var matrix = Matrix()
    val boundOption = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
        contentResolver.openInputStream(uri)?.use { inputStream ->
            matrix = processExif(inputStream)
            BitmapFactory.decodeStream(inputStream, null, this)
        }
    }
//
//    var size = boundOption.run {
//
//        val width = outWidth
//        val height = outHeight
//        max((width * 1.0 / maxWidth).roundToInt(), (height * 1.0 / maxHeight).roundToInt())
//    }
//    size = if (size == 0) {
//        1
//    } else {
//        size
//    }
//    Log.d("util","$size")
//    val option = BitmapFactory.Options().apply{
////        inSampleSize = size
//        inPreferredConfig = Bitmap.Config.ARGB_8888
//    }
    BitmapFactory.Options().apply {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream, null, this)?.let {


                return compress(Bitmap.createBitmap(it,0,0,it.width,it.height,matrix,false))
            }
        }
        return null
    }

}

// use the MediaStore insert api to save a photo in the shared storage
fun saveBitmapInMedia(bitmap: Bitmap, contentResolver: ContentResolver): Uri? {
    val values = contentValuesOf(
        MediaStore.Images.Media.DISPLAY_NAME to "Image${System.currentTimeMillis()}.jpg",
        MediaStore.Images.Media.MIME_TYPE to "image/jpeg"
    )
    val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    uri?.let { uri ->
        contentResolver.openOutputStream(uri)?.let { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            outputStream.close()
        }
        return uri
    } ?: return null
}

// save photo as a file in the Internal storage
fun saveBitmapInternal(bitmap: Bitmap, context: Context): String {
    val filename = "Image${System.currentTimeMillis()}.jpg"
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
    context.openFileOutput(filename, Context.MODE_PRIVATE).use {
        it.write(outputStream.toByteArray())
    }
    return filename
}

private fun processExif(inputStream: InputStream): Matrix {
    val exifInterface = ExifInterface(inputStream)
    val orientation = exifInterface.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL
    )
    val matrix = Matrix()
    Log.d("util", "$orientation")
    Log.d("util", "$matrix")
    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
    }
    return matrix
}
