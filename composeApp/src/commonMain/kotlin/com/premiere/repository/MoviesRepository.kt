package com.premiere.repository

import com.premiere.model.MovieDetail
import com.premiere.model.MovieImageSet
import com.premiere.model.MovieSummary
import com.premiere.model.PaginatedResponse
import com.premiere.model.Person
import com.premiere.model.Video
import kotlinx.serialization.Serializable

@Serializable
data class MovieFilters(
    val query: String? = null,
    val genreId: Int? = null,
    val minYear: Int? = null,
    val maxYear: Int? = null,
    val minRating: Float? = null
)

enum class MovieSort(val apiValue: String, val apiOrder: String, val label: String) {
    RATING(apiValue = "imdb_rating", apiOrder = "desc", label = "Rating"),
    YEAR(apiValue = "year", apiOrder = "desc", label = "Year"),
    TITLE(apiValue = "title", apiOrder = "asc", label = "Title"),
    POPULARITY(apiValue = "popularity", apiOrder = "desc", label = "Popularity")
}

interface MoviesRepository {

    suspend fun getMovies(
        filters: MovieFilters = MovieFilters(),
        sort: MovieSort = MovieSort.RATING,
        page: Int = 1,
        pageSize: Int = 30
    ): PaginatedResponse<MovieSummary>

    suspend fun getMovieDetails(imdbId: String): MovieDetail

    suspend fun getMovieCast(imdbId: String, page: Int = 1, pageSize: Int = 10): PaginatedResponse<Person>

    suspend fun getMovieImages(imdbId: String, type: String = "backdrop"): MovieImageSet

    suspend fun getMovieVideos(imdbId: String, type: String = "Trailer"): List<Video>
}
