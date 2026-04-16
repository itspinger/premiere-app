package com.premiere.model

import kotlinx.serialization.Serializable

@Serializable
data class MovieImage(
    val filePath: String,
    val width: Int? = null,
    val height: Int? = null,
    val voteAverage: Float? = null,
    val language: String? = null
)

@Serializable
data class MovieImageSet(
    val posters: List<MovieImage> = emptyList(),
    val backdrops: List<MovieImage> = emptyList(),
    val logos: List<MovieImage> = emptyList()
)

@Serializable
data class Video(
    val key: String,
    val site: String,
    val name: String? = null,
    val type: String? = null,
    val official: Boolean = false,
    val publishedAt: String? = null
)
