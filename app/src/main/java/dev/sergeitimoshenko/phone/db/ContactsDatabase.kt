package dev.sergeitimoshenko.phone.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.sergeitimoshenko.phone.db.converters.PhonesConverter
import dev.sergeitimoshenko.phone.db.converters.PhotoConverter
import dev.sergeitimoshenko.phone.models.Contact

@Database(
    entities = [Contact::class], version = 1
)
@TypeConverters(PhotoConverter::class, PhonesConverter::class)
abstract class ContactsDatabase : RoomDatabase() {
    companion object {
        const val DATABASE_NAME = "contacts_db"
    }

    abstract fun getContactDao(): ContactDao
}