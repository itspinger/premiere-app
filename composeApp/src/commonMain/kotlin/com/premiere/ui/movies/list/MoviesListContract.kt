package com.premiere.ui.movies.list

import com.premiere.model.MovieSummary
import com.premiere.repository.MovieFilters
import com.premiere.repository.MovieSort

interface MoviesListContract {

    data class State(
        val isLoading: Boolean = false,
        val movies: List<MovieSummary> = emptyList(),
        val totalCount: Int = 0,
        val selectedSort: MovieSort = MovieSort.RATING,
        val appliedFilters: MovieFilters = MovieFilters(),
        val errorMessage: String? = null
    ) {
        val isEmpty: Boolean
            get() = !isLoading && errorMessage == null && movies.isEmpty()
    }

    sealed interface Intent {
        data object Load : Intent
        data object Retry : Intent
        data class ChangeSort(val sort: MovieSort) : Intent
        data class ApplyFilters(val filters: MovieFilters) : Intent
        data class MovieClicked(val imdbId: String) : Intent
        data object FilterClicked : Intent
    }

    sealed interface Effect {
        data class NavigateToDetails(val imdbId: String) : Effect
        data class NavigateToFilters(val filters: MovieFilters) : Effect
    }
}
