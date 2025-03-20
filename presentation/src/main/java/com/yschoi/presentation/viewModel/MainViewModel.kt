package com.yschoi.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yschoi.domain.model.*
import com.yschoi.domain.usecase.*
import com.yschoi.domain.usecase.artworkUseCase.*
import com.yschoi.domain.usecase.authUseCase.*
import com.yschoi.domain.usecase.chatUseCase.GetUserChatListsUseCase
import com.yschoi.presentation.model.ArtworkSort
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getArtworksUseCase: GetArtworksUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val favoriteArtworksUseCase: GetFavoriteArtworksUseCase,
    private val likeArtworksUseCase: GetLikedArtworksUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val saveUserProfileImageUseCase: SaveUserProfileImageUseCase,
    private val getAdvertiseImagesUseCase: GetAdvertiseImagesUseCase,
    private val getUserChatListsUseCase: GetUserChatListsUseCase,
    private val getAuthStateUseCase: GetAuthStateUseCase,
    private val logOutUseCase: LogOutUseCase,
    private val getRecentArtworksUseCase: GetRecentArtworksUseCase,
    private val getCurrentUserUidUseCase: GetCurrentUserUidUseCase,
    private val getArtworkById: GetArtworkByIdUseCase,
    private val getArtistSoldArtworkUseCase: GetArtistSoldArtworkUseCase,
    private val saveMessagingToken: SaveMessagingToken,
    private val getMostViewedCategoryUseCase: GetMostViewedCategoryUseCase,
    private val saveRecentCategoryUseCase: SaveRecentCategoryUseCase,
): ViewModel() {


    //로그인 상태
    private var _isLoggedInState = MutableStateFlow<Boolean>(true)
    var isLoggedInState: StateFlow<Boolean> = _isLoggedInState.asStateFlow()

    //User 정보
    private var _userInfoStateFlow = MutableStateFlow<DomainUser> (DomainUser())
    val userInfoStateFlow: StateFlow<DomainUser> = _userInfoStateFlow.asStateFlow()

    //현재 카테고리 작품 리스트
    private var _artworkLiveData = MutableStateFlow<List<DomainArtwork>>(emptyList())
    val artworkLiveData: StateFlow<List<DomainArtwork>> = _artworkLiveData.asStateFlow()

    //전체 작품 리스트
    private var _artworkAllLiveData = MutableStateFlow<List<DomainArtwork>>(emptyList())
    val artworkAllLiveData: StateFlow<List<DomainArtwork>> = _artworkAllLiveData.asStateFlow()

    //북마크한 작품 리스트
    private var _artworkFavoriteLiveData = MutableStateFlow<List<DomainArtwork>>(emptyList())
    var artworkFavoriteLiveData: StateFlow<List<DomainArtwork>> = _artworkFavoriteLiveData.asStateFlow()

    //좋아요한 작품 리스트
    private var _artworkLikedLiveData = MutableStateFlow<List<DomainArtwork>>(emptyList())
    var artworkLikedLiveData: StateFlow<List<DomainArtwork>> = _artworkLikedLiveData.asStateFlow()

    //소개 사진
    private val _advertiseImagesState = MutableStateFlow<List<String>>(emptyList())
    var advertiseImagesState: StateFlow<List<String>> = _advertiseImagesState.asStateFlow()
    private val _isLoadingAdvertiseImages = MutableStateFlow(true)
    val isLoadingAdvertiseImages: StateFlow<Boolean> = _isLoadingAdvertiseImages

    //채팅방 리스트
    private val _chatRoomsList = MutableStateFlow<List<DomainChatRoomWithUser>>(emptyList())
    val chatRoomsList: StateFlow<List<DomainChatRoomWithUser>> = _chatRoomsList.asStateFlow()

    //최근 작품 리스트
    private val _artistRecentArtworks = MutableStateFlow<List<DomainArtwork>>(emptyList())
    val artistRecentArtworks: StateFlow<List<DomainArtwork>> = _artistRecentArtworks.asStateFlow()
    private val _isLoadingRecentArtworks = MutableStateFlow(true)
    val isLoadingRecentArtworks: StateFlow<Boolean> = _isLoadingRecentArtworks.asStateFlow()

    //유저 안읽은 메세지
    private val _unreadMessageCount = MutableStateFlow<Int>(0)
    val unreadMessageCount: StateFlow<Int> = _unreadMessageCount

    //작가 판매 작품
    private val _artistSoldArtworks = MutableStateFlow<List<DomainArtwork>>(emptyList())
    val artistSoldArtworks: StateFlow<List<DomainArtwork>> = _artistSoldArtworks

    //최근 본 작품과 비슷한 작품들
    private val _recommendArtworks = MutableStateFlow<List<DomainArtwork>>(emptyList())
    val recommendArtworks: StateFlow<List<DomainArtwork>> = _recommendArtworks

    val job: Job? = null

    init {
        viewModelScope.launch {
            getAuthStateUseCase.execute()
                .distinctUntilChanged()
                .flatMapLatest { uid ->
                    if(!uid.isNullOrEmpty()){
                        getUserInfoUseCase.execute(uid)
                    }else{
                        flowOf(null)
                    }
                }.collectLatest { userInfo ->
                    _userInfoStateFlow.value = userInfo ?: DomainUser()
                    _isLoggedInState.value = !userInfo?.uid.isNullOrEmpty()

                    if(!_isLoggedInState.value){
                        _artistSoldArtworks.value = emptyList()
                    }

                    userInfo?.uid?.let { uid ->
                        saveMessagingToken.execute(userInfo.uid)
                        loadChatLists(userInfo.uid)
                    }
                }
        }
        //소개 사진 가져오기
        advertiseImages()
    }

    //최근 작품들 가져오기
    fun loadRecentArtworks(limit : Int = 10){
        viewModelScope.launch {
            _isLoadingRecentArtworks.value = true
            _artistRecentArtworks.value = getRecentArtworksUseCase.execute(limit).reversed()
            _isLoadingRecentArtworks.value = false
        }
    }

    //채팅방 목록 가져오기 -> 리스너로 구현
    fun loadChatLists(uid: String?){
        uid?.let {
            viewModelScope.launch {
                getUserChatListsUseCase.execute(uid)
                    .distinctUntilChanged()
                    .collectLatest{ chatRoomLists ->
                        _chatRoomsList.value = emptyList()
                        var sum = 0
                        chatRoomLists.forEach { chatRoom ->
                            sum += chatRoom.chatRoom.unreadMessages[uid] ?: 0
                        }
                        _chatRoomsList.value = chatRoomLists.sortedByDescending{ it.chatRoom.lastMessage.timestamp }
                        _unreadMessageCount.value = sum
                }
            }
        }
    }

    fun getArtworkById(artworkId: String) = viewModelScope.launch {
        getArtworkById.execute(artworkId)
    }

    fun getMostViewedCategory(){
        val category = getMostViewedCategoryUseCase.execute()
        _recommendArtworks.value = artworkAllLiveData.value.filter {
            it.category == category
        }.take(20)
    }

    fun saveRecentCategory(category: String) = saveRecentCategoryUseCase.execute(category)

    // 카테고리 작품들 가져오기
    fun loadArtworks() = viewModelScope.launch {
        _artworkAllLiveData.value = getArtworksUseCase.execute()
        _artworkLiveData.value = _artworkAllLiveData.value
    }

    // 광고 사진들 가져오기
    fun advertiseImages(){
        _isLoadingAdvertiseImages.value = true
        viewModelScope.launch {
            var listImages = getAdvertiseImagesUseCase.execute()
            when(listImages){
                is Response.Success -> {
                    _advertiseImagesState.value = listImages.data
                    _isLoadingAdvertiseImages.value = false
                }
                is Response.Error -> {

                }
            }
        }
    }

    //작품 카테고리 분류
    fun sortCategoryArtworks(category: String){
        if(category.equals("전체")) _artworkLiveData.value = _artworkAllLiveData.value
        else  _artworkLiveData.value = _artworkAllLiveData.value.filter { it.category == category }
    }


    //작품 정렬
    fun sortArtworks(sortBy: ArtworkSort, category: String){
        when(sortBy){
            ArtworkSort.NONE -> loadArtworks()
            ArtworkSort.BOOKMARK -> _artworkLiveData.value = _artworkLiveData.value.sortedByDescending { it.favoriteUser.size }
            ArtworkSort.DATE -> _artworkLiveData.value = _artworkLiveData.value.sortedBy { it.upload_at }
            ArtworkSort.LIKE -> _artworkLiveData.value = _artworkLiveData.value.sortedByDescending { it.likedArtworks.size }
            ArtworkSort.PRICE -> _artworkLiveData.value = _artworkLiveData.value.sortedByDescending { it.minimalPrice }
        }
    }


    fun getFavoriteArtworksList() {
        if(!isLoggedInState.value) return
        viewModelScope.launch {
            favoriteArtworksUseCase.execute(userInfoStateFlow.value.uid)
                .catch { exception ->  }
                .collect { artworkList ->
                    _artworkFavoriteLiveData.value = artworkList
                }
        }
    }

    fun getLikedArtworksList(){
        if(!isLoggedInState.value) return
        viewModelScope.launch {
            likeArtworksUseCase.execute(userInfoStateFlow.value.uid)
                .catch { exception ->  }
                .collect { artworkList ->
                    _artworkLikedLiveData.value = artworkList
                }
        }
    }

    fun logOut(){
        logOutUseCase.execute()
        _chatRoomsList.value = emptyList()
    }

    fun getArtistSoldArtwork(artworks: Map<String, Boolean>) {
        artworks.isNotEmpty().let {
            viewModelScope.launch {
                _artistSoldArtworks.value = getArtistSoldArtworkUseCase.execute(artworks)
            }
        }
    }
}