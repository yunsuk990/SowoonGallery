package com.example.sowoongallery.di

import com.example.domain.repository.FirebaseRepository
import com.example.domain.usecase.CheckUserRtdbUseCase
import com.example.domain.usecase.GetArtworksUseCase
import com.example.domain.usecase.SaveUserInfoUseCase
import com.example.domain.usecase.SignInWithPhoneUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetArtworksUseCase(
        firebaseRepository: FirebaseRepository
    ) =  GetArtworksUseCase(firebaseRepository)

    @Provides
    @Singleton
    fun provideSaveUserInfoUseCase(
        firebaseRepository: FirebaseRepository
    ) = SaveUserInfoUseCase(firebaseRepository)

    @Provides
    @Singleton
    fun provideCheckUserRtdbUseCase(
        firebaseRepository: FirebaseRepository
    ) = CheckUserRtdbUseCase(firebaseRepository)

    @Provides
    @Singleton
    fun provideSignInWithPhoneUseCase(
        firebaseRepository: FirebaseRepository
    ) = SignInWithPhoneUseCase(firebaseRepository)


}