package com.example.domain.model

enum class SortedType(val sortedType: String) {
    NONE("기본"),
    LIKE("좋아요 순"),
    BOOKMARK("북마크 순"),
    DATE("날짜 순")
}