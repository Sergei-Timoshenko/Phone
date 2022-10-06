package dev.sergeitimoshenko.phone.remote.firestore.wrappers

import dev.sergeitimoshenko.phone.models.Contact
import dev.sergeitimoshenko.phone.remote.firestore.entities.ContactFirestoreEntity

class ContactFirestoreMapper {
    fun mapFromEntity(entity: Map<String, Any>): Contact =
        Contact(
            name = entity["name"].toString(),
            surname = entity["surname"].toString(),
            phone = entity["phone"].toString(),
            email = entity["email"].toString(),
            photo = null,
            isFavourite = entity["favourite"].toString().toBoolean(),
            id = entity["id"].toString()
        )

    fun mapToEntity(domainModel: Contact): ContactFirestoreEntity =
        ContactFirestoreEntity(
            name = domainModel.name,
            surname = domainModel.surname,
            phone = domainModel.phone,
            email = domainModel.email,
            photoUrl = null,
            isFavourite = domainModel.isFavourite,
            id = domainModel.id
        )
}