package com.example.sowoongallery.di

import android.content.Context
import com.example.data.repository.remote.datasource.ArtworkDataSource
import com.example.data.repository.remote.datasource.AuthDataSource
import com.example.data.repository.remote.datasource.FirebaseDataSource
import com.example.data.repository.remote.datasourceimpl.ArtworkDataSourceImpl
import com.example.data.repository.remote.datasourceimpl.AuthDataSourceImpl
import com.example.data.repository.remote.datasourceimpl.FirebaseDataSourceImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataSourceModule {
    @Provides
    @Singleton
    fun provideFirebaseDataSource(
        firebaseAuth: FirebaseAuth,
        firebaseRtdb: FirebaseDatabase,
        firebaseStorage: FirebaseStorage
    ): FirebaseDataSource {
        return FirebaseDataSourceImpl(
            firebaseAuth, firebaseRtdb, firebaseStorage
        )
    }

    @Provides
    @Singleton
    fun provideArtworkDataSource(
        firebaseRtdb: FirebaseDatabase,
        firebaseStorage: FirebaseStorage
    ): ArtworkDataSource {
        return ArtworkDataSourceImpl(
            firebaseRtdb,
            firebaseStorage
        )
    }

    @Provides
    @Singleton
    fun provideAuthDataSource(
        firebaseAuth: FirebaseAuth,
        firebaseRtdb: FirebaseDatabase,
        @ApplicationContext context: Context
    ): AuthDataSource {
        return AuthDataSourceImpl(
            firebaseAuth,
            firebaseRtdb,
            context
        )
    }
}