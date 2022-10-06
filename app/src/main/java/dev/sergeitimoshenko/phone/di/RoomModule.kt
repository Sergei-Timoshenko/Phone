package dev.sergeitimoshenko.phone.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.sergeitimoshenko.phone.db.ContactsDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {
    @Provides
    @Singleton
    fun provideTaskDatabase(
        app: Application
    ) = Room.databaseBuilder(
        app,
        ContactsDatabase::class.java,
        ContactsDatabase.DATABASE_NAME
    ).fallbackToDestructiveMigration()
        .build()

    @Singleton
    @Provides
    fun provideContactDao(db: ContactsDatabase) = db.getContactDao()
}