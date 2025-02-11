package com.example.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
data class DomainArtwork(
    var key: String? = "",                              //작품 ID
    var artistUid: String? = "",                        //화가 UID
    var material: String? = "",                         //작품 재료
    var size: String? = "",                             //작품 크기
    var name: String? = "",                             //작품 이름
    var upload_at: String? = SimpleDateFormat("yyyy.MM.dd").format(Date()),  //작품 업로드 날짜
    var category: String? = "",                         //작품 카테고리
    var madeIn: String? = "",                           //작품 그려진 년도
    var url: String? = "",                              //작품 사진 URL
    var review: String? = "",                           //작품 설명
    var favoriteUser: Map<String, Boolean> = emptyMap(),//작품 북마크한 유저 UID
    var likedArtworks: Map<String, Boolean> = emptyMap(),//작품 저장한 유저 UID
    var sold: Boolean = false,                          //작품 판매 여부
    var canMakeSuggestion: Boolean = true,              //작품 가격 제안 여부
    var minimalPrice: String = "",                          //작품 가격
): Parcelable