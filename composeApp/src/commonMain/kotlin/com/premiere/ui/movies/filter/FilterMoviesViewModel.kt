package com.premiere.ui.movies.filter

import androidx.lifecycle.viewModelScope
import com.premiere.mvi.BaseViewModel
import com.premiere.repository.GenresRepository
import com.premiere.repository.MovieFilters
import kotlinx.coroutines.launch

class FilterMoviesViewModel(
    initialFilters: MovieFilters,
    private val genresRepository: GenresRepository
) : BaseViewModel<FilterMoviesContract.State, FilterMoviesContract.Intent, FilterMoviesContract.Effect>(
    initialState = FilterMoviesContract.State(
        searchQuery = initialFilters.query.orEmpty(),
        selectedGenreId = initialFilters.genreId,
        minYear = initialFilters.minYear?.toString() ?: "1920",
        maxYear = initialFilters.maxYear?.toString() ?: "2025",
        minRating = initialFilters.minRating ?: 0f
    )
) {

    init {
        loadGenres()
    }

    override fun onIntent(intent: FilterMoviesContract.Intent) {
        when (intent) {
            is FilterMoviesContract.Intent.SearchChanged -> {
                setState { it.copy(searchQuery = intent.query) }
            }
            is FilterMoviesContract.Intent.GenreSelected -> {
                setState { current ->
                    current.copy(
                        selectedGenreId = if (current.selectedGenreId == intent.genreId) null else intent.genreId
                    )
                }
            }
            is FilterMoviesContract.Intent.MinYearChanged -> {
                setState { it.copy(minYear = intent.value.filter(Char::isDigit)) }
            }
            is FilterMoviesContract.Intent.MaxYearChanged -> {
                setState { it.copy(maxYear = intent.value.filter(Char::isDigit)) }
            }
            is FilterMoviesContract.Intent.RatingChanged -> {
                setState { it.copy(minRating = intent.value) }
            }
            FilterMoviesContract.Intent.ApplyFilters -> {
                emitEffect(FilterMoviesContract.Effect.ApplyFilters(currentFilters()))
            }
            FilterMoviesContract.Intent.ClearAll -> {
                setState {
                    it.copy(
                        searchQuery = "",
                        selectedGenreId = null,
                        minYear = "1920",
                        maxYear = "2025",
                        minRating = 0f
                    )
                }
            }
            FilterMoviesContract.Intent.BackClicked -> {
                emitEffect(FilterMoviesContract.Effect.NavigateBack)
            }
        }
    }

    private fun loadGenres() {
        setState { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val genres = genresRepository.getGenres()
                setState {
                    it.copy(
                        genres = genres,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                setState {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to load genres"
                    )
                }
            }
        }
    }

    private fun currentFilters(): MovieFilters {
        val state = state.value
        return MovieFilters(
            query = state.searchQuery.trim().ifBlank { null },
            genreId = state.selectedGenreId,
            minYear = state.minYear.toIntOrNull(),
            maxYear = state.maxYear.toIntOrNull(),
            minRating = state.minRating.takeIf { it > 0f }
        )
    }
}
