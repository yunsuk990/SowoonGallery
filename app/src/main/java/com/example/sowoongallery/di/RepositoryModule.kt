package com.example.sowoongallery.di

import com.example.data.repository.remote.ArtworkRepositoryImpl
import com.example.data.repository.remote.AuthRepositoryImpl
import com.example.data.repository.remote.FirebaseRepositoryImpl
import com.example.data.repository.remote.datasource.ArtworkDataSource
import com.example.data.repository.remote.datasource.AuthDataSource
import com.example.data.repository.remote.datasource.FirebaseDataSource
import com.example.domain.repository.ArtworkRepository
import com.example.domain.repository.AuthRepository
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
    fun provideArtworkRepository(
        artworkDataSource: ArtworkDataSource
    ): ArtworkRepository {
        return ArtworkRepositoryImpl(artworkDataSource)
    }

    @Provides
    @Singleton
    fun provideFirebaseRepository(
        firebaseDataSource: FirebaseDataSource,
        artworkDataSource: ArtworkDataSource,
        authDataSource: AuthDataSource
    ): FirebaseRepository {
        return FirebaseRepositoryImpl(firebaseDataSource, artworkDataSource, authDataSource)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        authDataSource: AuthDataSource,
        artworkDataSource: ArtworkDataSource
    ): AuthRepository {
        return AuthRepositoryImpl(authDataSource,artworkDataSource)
    }
}