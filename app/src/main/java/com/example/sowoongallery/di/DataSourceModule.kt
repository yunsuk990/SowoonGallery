package com.example.sowoongallery.di

import com.example.data.repository.remote.datasource.FirebaseDataSource
import com.example.data.repository.remote.datasourceimpl.FirebaseDataSourceImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
}