package com.premiere.ui.movies.filter

import com.premiere.model.Genre
import com.premiere.repository.MovieFilters

interface FilterMoviesContract {

    data class State(
        val searchQuery: String = "",
        val genres: List<Genre> = emptyList(),
        val selectedGenreId: Int? = null,
        val minYear: String = "1920",
        val maxYear: String = "2025",
        val minRating: Float = 0f,
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    )

    sealed interface Intent {
        data class SearchChanged(val query: String) : Intent
        data class GenreSelected(val genreId: Int?) : Intent
        data class MinYearChanged(val value: String) : Intent
        data class MaxYearChanged(val value: String) : Intent
        data class RatingChanged(val value: Float) : Intent
        data object ApplyFilters : Intent
        data object ClearAll : Intent
        data object BackClicked : Intent
    }

    sealed interface Effect {
        data class ApplyFilters(val filters: MovieFilters) : Effect
        data object NavigateBack : Effect
    }
}
