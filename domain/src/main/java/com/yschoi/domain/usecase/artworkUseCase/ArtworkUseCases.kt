package com.yschoi.domain.usecase.artworkUseCase

import javax.inject.Inject

data class ArtworkUseCases @Inject constructor(
    val setFavoriteArtworkUseCase: SetFavoriteArtworkUseCase,
    val setLikedArtworkUseCase: SetLikedArtworkUseCase,
    val getArtworkUseCase: GetArtworksUseCase,
    val getFavoriteArtworksUseCase: GetFavoriteArtworksUseCase,
    val getLikedArtworksUseCase: GetLikedArtworksUseCase,
)
