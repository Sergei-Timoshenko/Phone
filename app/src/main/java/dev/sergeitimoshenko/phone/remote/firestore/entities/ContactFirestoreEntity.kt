package dev.sergeitimoshenko.phone.remote.firestore.entities

import dev.sergeitimoshenko.phone.models.Phone

data class ContactFirestoreEntity(
    val name: String,
    val surname: String,
    val phones: List<Phone>,
    val email: String? = null,
    val photoUrl: String? = null,
    val isFavourite: Boolean = false,
    val id: String? = null
)
