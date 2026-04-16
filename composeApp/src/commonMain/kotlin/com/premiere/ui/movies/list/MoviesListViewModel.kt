package com.premiere.ui.movies.list

import androidx.lifecycle.viewModelScope
import com.premiere.mvi.BaseViewModel
import com.premiere.repository.MovieFilters
import com.premiere.repository.MovieSort
import com.premiere.repository.MoviesRepository
import kotlinx.coroutines.launch

class MoviesListViewModel(
    private val moviesRepository: MoviesRepository
) : BaseViewModel<MoviesListContract.State, MoviesListContract.Intent, MoviesListContract.Effect>(
    initialState = MoviesListContract.State()
) {

    init {
        onIntent(MoviesListContract.Intent.Load)
    }

    override fun handleIntent(intent: MoviesListContract.Intent) {
        when (intent) {
            MoviesListContract.Intent.Load -> loadMovies()
            MoviesListContract.Intent.Retry -> loadMovies()
            is MoviesListContract.Intent.ChangeSort -> {
                setState { it.copy(selectedSort = intent.sort) }
                loadMovies(sort = intent.sort)
            }
            is MoviesListContract.Intent.ApplyFilters -> {
                setState { it.copy(appliedFilters = intent.filters) }
                loadMovies(filters = intent.filters)
            }
            is MoviesListContract.Intent.MovieClicked -> {
                emitEffect(MoviesListContract.Effect.NavigateToDetails(intent.imdbId))
            }
            MoviesListContract.Intent.FilterClicked -> {
                emitEffect(MoviesListContract.Effect.NavigateToFilters(state.value.appliedFilters))
            }
        }
    }

    private fun loadMovies(
        filters: MovieFilters = state.value.appliedFilters,
        sort: MovieSort = state.value.selectedSort
    ) {
        setState {
            it.copy(
                isLoading = true,
                errorMessage = null,
                appliedFilters = filters,
                selectedSort = sort
            )
        }

        viewModelScope.launch {
//            delay(2000) // Just for demonstration purposes for loading bar
            try {
                val response = moviesRepository.getMovies(
                    filters = filters,
                    sort = sort
                )

                // Simulate exception as well
                //throw IllegalArgumentException("Unable to load movies")

                setState {
                    it.copy(
                        isLoading = false,
                        movies = response.items,
                        totalCount = response.totalItems,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                setState {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to load movies"
                    )
                }
            }
        }
    }
}
