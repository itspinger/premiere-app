package com.premiere.repository.impl

import com.premiere.api.PremiereApi
import com.premiere.model.MovieDetail
import com.premiere.model.MovieImageSet
import com.premiere.model.MovieSummary
import com.premiere.model.PaginatedResponse
import com.premiere.model.Person
import com.premiere.model.Video
import com.premiere.repository.MovieFilters
import com.premiere.repository.MovieSort
import com.premiere.repository.MoviesRepository

class MoviesRepositoryImpl(private val api: PremiereApi) : MoviesRepository {

    override suspend fun getMovies(filters: MovieFilters, sort: MovieSort, page: Int, pageSize: Int): PaginatedResponse<MovieSummary> =
        api.getMovies(
            page = page,
            pageSize = pageSize,
            query = filters.query,
            genreId = filters.genreId,
            minYear = filters.minYear,
            maxYear = filters.maxYear,
            minRating = filters.minRating,
            sortBy = sort.apiValue,
            sortOrder = sort.apiOrder
        )

    override suspend fun getMovieDetails(imdbId: String): MovieDetail = api.getMovieDetails(imdbId)

    override suspend fun getMovieCast(imdbId: String, page: Int, pageSize: Int): PaginatedResponse<Person> =
        api.getMovieCast(
            imdbId = imdbId,
            page = page,
            pageSize = pageSize
        )

    override suspend fun getMovieImages(imdbId: String, type: String): MovieImageSet =
        api.getMovieImages(
            imdbId = imdbId,
            type = type
        )

    override suspend fun getMovieVideos(imdbId: String, type: String): List<Video> =
        api.getMovieVideos(
            imdbId = imdbId,
            type = type
        )
}
