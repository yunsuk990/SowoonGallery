package com.example.domain.usecase

import android.util.Log
import com.example.domain.model.DomainPrice
import com.example.domain.model.PriceWithUser
import com.example.domain.model.Response
import com.example.domain.repository.FirebaseRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GetPriceForArtworkUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    fun execute(category: String, artworkId: String, callback: (List<PriceWithUser>) -> Unit){
        firebaseRepository.getPriceForArtwork(category, artworkId){ item ->
            Log.d("FirebaseUseCase_GetPriceForArtworkUseCase", item.toString())
            callback(item)
        }
    }
}