package dev.sergeitimoshenko.phone.db.converters

import androidx.room.TypeConverter
import dev.sergeitimoshenko.phone.models.Phone


class PhonesConverter {
    @TypeConverter
    fun fromList(phones: List<Phone>): String {
        var tempString = ""
        for (phone in phones) {
            tempString += "${phone.phone}, "
        }
        return tempString
    }

    @TypeConverter
    fun fromString(phonesString: String) {

    }
}