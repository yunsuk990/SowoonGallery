package com.example.presentation.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.DomainPrice
import com.example.domain.model.PriceWithUser
import com.example.domain.usecase.GetCurrentUserUseCase
import com.example.domain.usecase.GetFavoriteArtworkUseCase
import com.example.domain.usecase.GetLikedArtworkUseCase
import com.example.domain.usecase.GetLikedCountArtworkUsecase
import com.example.domain.usecase.GetPriceForArtworkUseCase
import com.example.domain.usecase.GetUserInfoUseCase
import com.example.domain.usecase.SetFavoriteArtworkUseCase
import com.example.domain.usecase.SetLikedArtworkUseCase
import com.example.domain.usecase.SetPriceForArtworkUseCase
import com.example.presentation.model.Price
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtworkViewModel @Inject constructor(
    private val getFavoriteArtworkUseCase: GetFavoriteArtworkUseCase,
    private val setFavoriteArtworkUseCase: SetFavoriteArtworkUseCase,
    private val getLikedArtworkUseCase: GetLikedArtworkUseCase,
    private val setLikedArtworkUseCase: SetLikedArtworkUseCase,
    private val getLikedCountArtworkUsecase: GetLikedCountArtworkUsecase,
    private val setPriceForArtworkUseCase: SetPriceForArtworkUseCase,
    private val getArtworkPriceForArtworkUseCase: GetPriceForArtworkUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
): ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    val firebaseUser = mutableStateOf<FirebaseUser?>(null)

    private val _artworkFavoriteState = MutableLiveData<Boolean>()
    var artworkFavoriteState: LiveData<Boolean> = _artworkFavoriteState

    private val _artworkLikedState = MutableLiveData<Boolean>()
    var artworkLikedState: LiveData<Boolean> = _artworkLikedState

    private val _artworkLikedCountState = MutableLiveData<Int>()
    var artworkLikedCountState: LiveData<Int> = _artworkLikedCountState

    // 가격 저장
    private val _priceSaveResult = MutableLiveData<Result<Boolean>>()
    val priceSaveResult: LiveData<Result<Boolean>> get() = _priceSaveResult

    // 가격 리스트
    private val _priceListResult = MutableLiveData<List<PriceWithUser>>()
    val priceListResult: LiveData<List<PriceWithUser>> get() = _priceListResult

    // X축 데이터 (가격)
    private val _xData = MutableLiveData<List<String>>(emptyList())
    val xData: LiveData<List<String>> = _xData

    // Y축 데이터 (날짜)
    private val _yData = MutableLiveData<List<Float>>(emptyList())
    val yData: LiveData<List<Float>> = _yData



    init {
        firebaseUser.value = getCurrentUserUseCase.execute()
        Log.d("ArtworkViewModel", firebaseUser.value?.uid.toString())
    }

    fun getFavoriteArtwork(artworkUid: String){
        getFavoriteArtworkUseCase.execute(auth.uid!!, artworkUid){ isFavorite ->
            _artworkFavoriteState.value = isFavorite
        }
    }

    fun getUserInfo(uid: String){
        getUserInfoUseCase.execute(uid){ response ->

        }
    }

    fun getArtworkPrice(category: String, artworkUid: String){
        getArtworkPriceForArtworkUseCase.execute(category = category, artworkId = artworkUid){ priceMap ->
            var xList = mutableListOf<String>()
            var yList = mutableListOf<Float>()
            priceMap.forEach { priceWithUser ->
                xList.add(priceWithUser.date)
                yList.add(priceWithUser.price)
            }
            if(xList.size == 1){
                xList.add(0,xList[0])
                yList.add(0,0f)
            }

            _xData.value = if(xList.isNullOrEmpty()) mutableListOf(" ", " ") else xList
            _yData.value = if(yList.isNullOrEmpty()) mutableListOf(0f,0f) else yList
            _priceListResult.value = priceMap
            Log.d("yData", xList.toString())
            Log.d("xData", yList.toString())
        }
    }

    fun setPriceForArtwork(category: String, artworkId: String, price: Float, userId: String = firebaseUser.value?.uid!!.toString()) {
        viewModelScope.launch {
            val result = setPriceForArtworkUseCase.execute(category, artworkId, price, userId)
            _priceSaveResult.value = result
        }
    }

    fun setFavoriteArtwork(isFavorite: Boolean, artworkUid: String, category: String) {
        Log.d("isFavorite", isFavorite.toString())
//        if(auth == null){
//            _authState.value = AuthState.Error("로그인하세요.")
//            return
//        }
        setFavoriteArtworkUseCase.execute(auth.uid!!, artworkUid, !isFavorite, category){ isFavorite ->
            _artworkFavoriteState.value = isFavorite
        }
    }

    fun getLikedArtwork(artworkUid: String){
        Log.d("getLikedArtwork", artworkUid.toString())
        getLikedArtworkUseCase.execute(auth.uid!!, artworkUid){ isLiked ->
            _artworkLikedState.value = isLiked
        }
    }

    fun setLikedArtwork(isLiked: Boolean, artworkUid: String, category: String){
        Log.d("setLikedArtwork", isLiked.toString())
        setLikedArtworkUseCase.execute(auth.uid!!, artworkUid, !isLiked, category){ isLiked ->
            _artworkLikedState.value = isLiked
        }
    }

    fun getLikedCountArtwork(artworkUid: String, category: String){
        getLikedCountArtworkUsecase.execute(artworkUid, category){ countVal ->
            _artworkLikedCountState.value = countVal
        }
    }

    fun addChartData(date: String, price: Float) {
        val currentXData = _xData.value?.toMutableList() ?: mutableListOf()
        val currentYData = _yData.value?.toMutableList() ?: mutableListOf()

        currentXData.add(date)
        currentYData.add(price)

        _xData.value = currentXData
        _yData.value = currentYData
    }


}