package dev.sergeitimoshenko.phone.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

fun fromByteArray(byteArray: ByteArray?): Bitmap? =
    BitmapFactory.decodeByteArray(byteArray, 0, byteArray?.size ?: 0)

fun fromBitmap(bitmap: Bitmap?): ByteArray? {
    val outputStream = ByteArrayOutputStream()
    bitmap?.compress(Bitmap.CompressFormat.JPEG, 20, outputStream)
    return outputStream.toByteArray()
}

fun compressBitmap(bitmap: Bitmap?): Bitmap? {
    val byteArray = fromBitmap(bitmap)
    return fromByteArray(byteArray)
}