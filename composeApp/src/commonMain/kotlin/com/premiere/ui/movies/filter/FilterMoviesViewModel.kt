package com.premiere.ui.movies.filter

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.premiere.mvi.BaseViewModel
import com.premiere.navigation.movieFilters
import com.premiere.repository.GenresRepository
import com.premiere.repository.MovieFilters
import com.premiere.util.currentYear
import kotlinx.coroutines.launch

class FilterMoviesViewModel(
    savedStateHandle: SavedStateHandle,
    private val genresRepository: GenresRepository
) : BaseViewModel<FilterMoviesContract.State, FilterMoviesContract.Intent, FilterMoviesContract.Effect>(
    initialState = savedStateHandle.movieFilters.let { filters ->
        FilterMoviesContract.State(
            searchQuery = filters.query.orEmpty(),
            selectedGenreId = filters.genreId,
            minYear = filters.minYear?.toString() ?: "",
            maxYear = filters.maxYear?.toString() ?: "",
            minRating = filters.minRating ?: 0f
        )
    }
) {

    init {
        loadGenres()
    }

    override fun handleIntent(intent: FilterMoviesContract.Intent) {
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
                        maxYear = currentYear().toString(),
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
            minYear = state.minYear.toIntOrNull()?.takeIf { state.minYear.length == 4 },
            maxYear = state.maxYear.toIntOrNull()?.takeIf { state.maxYear.length == 4 },
            minRating = state.minRating.takeIf { it > 0f }
        )
    }
}
