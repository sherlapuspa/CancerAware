package com.dicoding.asclepius.model

import com.google.gson.annotations.SerializedName

data class DataArticle(

    @field:SerializedName("title") val title: String,

    @field:SerializedName("author") val author: String,

    @field:SerializedName("description") val description: String,

    @field:SerializedName("url") val url: String,

    @field:SerializedName("urlToImage") val urlToImage: String,

    @SerializedName("publishedAt") val publishedAt: String,

    @field:SerializedName("content") val content: Any
)