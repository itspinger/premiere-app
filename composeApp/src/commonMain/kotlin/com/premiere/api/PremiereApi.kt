package com.premiere.api

import com.premiere.model.Genre
import com.premiere.model.MovieDetail
import com.premiere.model.MovieImageSet
import com.premiere.model.MovieSummary
import com.premiere.model.PaginatedResponse
import com.premiere.model.Person
import com.premiere.model.Video
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

interface PremiereApi {

    @GET("movies")
    suspend fun getMovies(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20,
        @Query("query") query: String? = null,
        @Query("genre_id") genreId: Int? = null,
        @Query("min_year") minYear: Int? = null,
        @Query("max_year") maxYear: Int? = null,
        @Query("min_rating") minRating: Float? = null,
        @Query("sort_by") sortBy: String = "imdb_votes",
        @Query("sort_order") sortOrder: String = "desc"
    ): PaginatedResponse<MovieSummary>

    @GET("movies/{id}")
    suspend fun getMovieDetails(@Path("id") imdbId: String): MovieDetail

    @GET("movies/{id}/cast")
    suspend fun getMovieCast(
        @Path("id") imdbId: String,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20
    ): PaginatedResponse<Person>

    @GET("movies/{id}/images")
    suspend fun getMovieImages(
        @Path("id") imdbId: String,
        @Query("type") type: String? = null
    ): MovieImageSet

    @GET("movies/{id}/videos")
    suspend fun getMovieVideos(
        @Path("id") imdbId: String,
        @Query("type") type: String? = null
    ): List<Video>

    @GET("genres")
    suspend fun getGenres(): List<Genre>

}
