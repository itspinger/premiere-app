package com.premiere.navigation

import kotlinx.serialization.Serializable

@Serializable
data object MoviesRoute

@Serializable
data class FilterRoute(
    val query: String? = null,
    val genreId: Int? = null,
    val minYear: Int? = null,
    val maxYear: Int? = null,
    val minRating: Float? = null
)

@Serializable
data class DetailsRoute(val imdbId: String)
