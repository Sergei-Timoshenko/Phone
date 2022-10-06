package dev.sergeitimoshenko.phone.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.sergeitimoshenko.phone.repositories.FirebaseRepository
import dev.sergeitimoshenko.phone.repositories.MainRepository
import dev.sergeitimoshenko.phone.repositories.RoomRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideMainRepository(
        firebaseRepository: FirebaseRepository, roomRepository: RoomRepository
    ) = MainRepository(firebaseRepository, roomRepository)
}