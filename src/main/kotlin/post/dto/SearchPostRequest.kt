package com.overtheinfinite.board.dto

data class SearchPostRequest (
    val keyword: String?,
    val boardId: Long?,
    val pageNo: Long,
    val pagePostCount: Long,
)