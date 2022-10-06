package dev.sergeitimoshenko.phone.db.converters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter

import java.io.ByteArrayOutputStream

class PhotoConverter {
    @TypeConverter
    fun fromBitmap(bitmap: Bitmap?): ByteArray? {
        val outputStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        return outputStream.toByteArray()
    }

    @TypeConverter
    fun fromByteArray(byteArray: ByteArray?): Bitmap? =
        BitmapFactory.decodeByteArray(byteArray, 0, byteArray?.size ?: 0)
}