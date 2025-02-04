package com.example.sowoongallery.di

import com.example.domain.repository.ArtworkRepository
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.FirebaseRepository
import com.example.domain.usecase.*
import com.example.domain.usecase.artworkUseCase.GetArtworksUseCase
import com.example.domain.usecase.authUseCase.CheckUserRtdbUseCase
import com.example.domain.usecase.authUseCase.SaveUserInfoUseCase
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
        artworkRepository: ArtworkRepository
    ) =  GetArtworksUseCase(artworkRepository)

    @Provides
    @Singleton
    fun provideSaveUserInfoUseCase(
        authRepository: AuthRepository
    ) = SaveUserInfoUseCase(authRepository)

    @Provides
    @Singleton
    fun provideCheckUserRtdbUseCase(
        authRepository: AuthRepository
    ) = CheckUserRtdbUseCase(authRepository)

    @Provides
    @Singleton
    fun provideSignInWithPhoneUseCase(
        authRepository: AuthRepository
    ) = SignInWithPhoneUseCase(authRepository)
}