package com.premiere.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieSummary(
    @SerialName("imdbId")
    override val id: String,

    val title: String,
    val year: Int? = null,
    val imdbRating: Float? = null,
    val imdbVotes: Int? = null,
    val posterPath: String? = null,
    val genres: List<Genre> = emptyList()
) : Identifiable<String>