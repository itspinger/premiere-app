package com.premiere.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.premiere.repository.MovieFilters
import com.premiere.ui.movies.details.MovieDetailsRoute
import com.premiere.ui.movies.details.MovieDetailsViewModel
import com.premiere.ui.movies.filter.FilterMoviesContract
import com.premiere.ui.movies.filter.FilterMoviesRoute
import com.premiere.ui.movies.filter.FilterMoviesViewModel
import com.premiere.ui.movies.list.MoviesListContract
import com.premiere.ui.movies.list.MoviesListRoute
import com.premiere.ui.movies.list.MoviesListViewModel
import androidx.lifecycle.SavedStateHandle
import io.ktor.http.decodeURLQueryComponent
import io.ktor.http.encodeURLParameter
import kotlinx.serialization.json.Json
import org.koin.compose.viewmodel.koinViewModel

private const val MOVIES_ROUTE = "movies"
private const val FILTER_ROUTE = "filter"
private const val DETAILS_ROUTE = "details"
private const val FILTERS_ARG = "filters"
private const val IMDB_ID_ARG = "imdbId"
private const val APPLIED_FILTERS_KEY = "applied_filters"

internal val SavedStateHandle.imdbId: String
    get() = checkNotNull(this[IMDB_ID_ARG]) { "imdbId nav argument is missing" }

internal val SavedStateHandle.movieFilters: MovieFilters
    get() = this.get<String>(FILTERS_ARG)
        ?.decodeURLQueryComponent()
        ?.let { Json.decodeFromString(it) }
        ?: MovieFilters()

@Composable
fun PremiereNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = MOVIES_ROUTE
    ) {
        composable(MOVIES_ROUTE) { backStackEntry ->
            val viewModel = koinViewModel<MoviesListViewModel>()
            val filtersFlow = backStackEntry.savedStateHandle.getStateFlow<String?>(APPLIED_FILTERS_KEY, null)

            LaunchedEffect(viewModel) {
                viewModel.effect.collect { effect ->
                    when (effect) {
                        is MoviesListContract.Effect.NavigateToDetails -> {
                            navController.navigate("$DETAILS_ROUTE/${effect.imdbId}")
                        }

                        is MoviesListContract.Effect.NavigateToFilters -> {
                            val encodedFilters = Json.encodeToString(effect.filters).encodeURLParameter()
                            navController.navigate("$FILTER_ROUTE?$FILTERS_ARG=$encodedFilters")
                        }
                    }
                }
            }

            LaunchedEffect(filtersFlow) {
                filtersFlow.collect { encodedFilters ->
                    if (encodedFilters != null) {
                        val filters = Json.decodeFromString<MovieFilters>(encodedFilters)
                        viewModel.onIntent(MoviesListContract.Intent.ApplyFilters(filters))
                        backStackEntry.savedStateHandle[APPLIED_FILTERS_KEY] = null
                    }
                }
            }

            MoviesListRoute(viewModel = viewModel)
        }

        composable(
            route = "$FILTER_ROUTE?$FILTERS_ARG={$FILTERS_ARG}",
            arguments = listOf(
                navArgument(FILTERS_ARG) {
                    type = NavType.StringType
                    defaultValue = Json.encodeToString(MovieFilters()).encodeURLParameter()
                }
            )
        ) {
            val viewModel = koinViewModel<FilterMoviesViewModel>()

            LaunchedEffect(viewModel) {
                viewModel.effect.collect { effect ->
                    when (effect) {
                        is FilterMoviesContract.Effect.ApplyFilters -> {
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set(APPLIED_FILTERS_KEY, Json.encodeToString(effect.filters))
                            navController.navigateUp()
                        }

                        FilterMoviesContract.Effect.NavigateBack -> {
                            navController.navigateUp()
                        }
                    }
                }
            }

            FilterMoviesRoute(viewModel = viewModel)
        }

        composable(
            route = "$DETAILS_ROUTE/{imdbId}",
            arguments = listOf(navArgument("imdbId") { type = NavType.StringType })
        ) {
            val viewModel = koinViewModel<MovieDetailsViewModel>()

            MovieDetailsRoute(
                viewModel = viewModel,
                onBack = { navController.navigateUp() }
            )
        }
    }
}
