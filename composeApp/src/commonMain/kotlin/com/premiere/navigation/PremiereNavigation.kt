package com.premiere.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.premiere.repository.MovieFilters
import com.premiere.ui.movies.details.MovieDetailsRoute
import com.premiere.ui.movies.details.MovieDetailsViewModel
import com.premiere.ui.movies.filter.FilterMoviesRoute
import com.premiere.ui.movies.filter.FilterMoviesViewModel
import com.premiere.ui.movies.list.MoviesListContract
import com.premiere.ui.movies.list.MoviesListRoute
import com.premiere.ui.movies.list.MoviesListViewModel
import org.koin.compose.viewmodel.koinViewModel

internal val SavedStateHandle.imdbId: String
    get() = toRoute<DetailsRoute>().imdbId

internal val SavedStateHandle.movieFilters: MovieFilters
    get() = toRoute<FilterRoute>().let {
        MovieFilters(
            query = it.query,
            genreId = it.genreId,
            minYear = it.minYear,
            maxYear = it.maxYear,
            minRating = it.minRating
        )
    }

@Composable
fun PremiereNavigation() {
    val navController = rememberNavController()

    // Scoped to PremiereNavigation so both MoviesListRoute and FilterMoviesRoute share the same
    // instance. Filter screen can dispatch ApplyFilters directly without passing data back
    // through the back stack.
    val moviesListViewModel = koinViewModel<MoviesListViewModel>()

    NavHost(
        navController = navController,
        startDestination = MoviesRoute
    ) {
        composable<MoviesRoute> {
            MoviesListRoute(
                viewModel = moviesListViewModel,
                onNavigateToDetails = { imdbId ->
                    navController.navigate(DetailsRoute(imdbId))
                },
                onNavigateToFilters = { filters ->
                    navController.navigate(FilterRoute(
                        query = filters.query,
                        genreId = filters.genreId,
                        minYear = filters.minYear,
                        maxYear = filters.maxYear,
                        minRating = filters.minRating
                    ))
                }
            )
        }

        composable<FilterRoute> {
            val viewModel = koinViewModel<FilterMoviesViewModel>()

            FilterMoviesRoute(
                viewModel = viewModel,
                onApplyFilters = { filters ->
                    moviesListViewModel.onIntent(MoviesListContract.Intent.ApplyFilters(filters))
                    navController.navigateUp()
                },
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable<DetailsRoute> {
            val viewModel = koinViewModel<MovieDetailsViewModel>()

            MovieDetailsRoute(
                viewModel = viewModel,
                onBack = { navController.navigateUp() }
            )
        }
    }
}
