package com.dicoding.asclepius.model

import com.google.gson.annotations.SerializedName

data class DataArticleResponse(
    @field:SerializedName("totalResults") val totalResults: Int,

    @field:SerializedName("articles") val articles: List<DataArticle>,

    @field:SerializedName("status") val status: String
)