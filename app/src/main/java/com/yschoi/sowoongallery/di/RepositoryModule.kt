package com.yschoi.sowoongallery.di

import com.yschoi.data.repository.remote.ArtworkRepositoryImpl
import com.yschoi.data.repository.remote.AuthRepositoryImpl
import com.yschoi.data.repository.remote.FirebaseRepositoryImpl
import com.yschoi.data.repository.remote.datasource.ArtworkDataSource
import com.yschoi.data.repository.remote.datasource.AuthDataSource
import com.yschoi.data.repository.remote.datasource.FirebaseDataSource
import com.yschoi.domain.repository.ArtworkRepository
import com.yschoi.domain.repository.AuthRepository
import com.yschoi.domain.repository.FirebaseRepository
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
        artworkDataSource: ArtworkDataSource,
        firebaseDataSource: FirebaseDataSource
    ): AuthRepository {
        return AuthRepositoryImpl(authDataSource,artworkDataSource,firebaseDataSource)
    }
}