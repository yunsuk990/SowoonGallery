package com.example.sowoongallery.di

import com.example.data.repository.FirebaseRepositoryImpl
import com.example.data.repository.remote.datasource.FirebaseDataSource
import com.example.domain.repository.FirebaseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideFirebaseRepository(
        firebaseDataSource: FirebaseDataSource
    ): FirebaseRepository {
        return FirebaseRepositoryImpl(firebaseDataSource)
    }


}