package com.example.domain.usecase.artworkUseCase

import android.util.Log
import com.example.domain.model.PriceWithUser
import com.example.domain.repository.ArtworkRepository
import com.example.domain.repository.FirebaseRepository
import javax.inject.Inject

class GetPriceForArtworkUseCase @Inject constructor(
    private val artworkRepository: ArtworkRepository
) {
    fun execute(category: String, artworkId: String, callback: (List<PriceWithUser>) -> Unit){
        artworkRepository.getPriceForArtwork(category, artworkId){ item ->
            Log.d("FirebaseUseCase_GetPriceForArtworkUseCase", item.toString())
            callback(item)
        }
    }
}