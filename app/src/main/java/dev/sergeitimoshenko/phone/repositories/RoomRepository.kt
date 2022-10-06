package dev.sergeitimoshenko.phone.repositories

import dev.sergeitimoshenko.phone.db.ContactDao
import dev.sergeitimoshenko.phone.models.Contact
import javax.inject.Inject

class RoomRepository @Inject constructor(
    val contactDao: ContactDao,
) {
    suspend fun insertContact(contact: Contact) {
//        if (contactDao.getContactByPhone(contact.phone).value?.phone?.isEmpty() == false) {
//            contactDao.insertContact(contact)
//        }
        contactDao.insertContact(contact)
    }

    suspend fun deleteContact(contact: Contact) = contactDao.deleteContact(contact)

    suspend fun updateContact(contact: Contact) = contactDao.updateContact(contact)
}