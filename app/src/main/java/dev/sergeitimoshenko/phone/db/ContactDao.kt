package dev.sergeitimoshenko.phone.db

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.room.*
import dev.sergeitimoshenko.phone.models.Contact

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts_table WHERE contact_name LIKE '%' || :searchQuery || '%' OR contact_surname LIKE '%' || :searchQuery || '%' OR contact_phone LIKE '%' || :searchQuery || '%' ORDER BY contact_name ASC")
    fun getContacts(searchQuery: String): LiveData<List<Contact>>

    @Query("SELECT * FROM contacts_table WHERE contact_is_favourite = 1 AND (contact_name LIKE '%' || :searchQuery || '%' OR contact_surname LIKE '%' || :searchQuery || '%' OR contact_phone LIKE '%' || :searchQuery || '%') ORDER BY contact_name ASC")
    fun getFavouriteContacts(searchQuery: String): LiveData<List<Contact>>

    @Query("SELECT * FROM contacts_table WHERE contact_phone = :phoneNumber LIMIT 1")
    fun getContactByPhone(phoneNumber: String): LiveData<Contact>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: Contact)

    @Delete
    suspend fun deleteContact(contact: Contact)

    @Update
    suspend fun updateContact(contact: Contact)
}