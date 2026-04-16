package com.premiere.ui.movies.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.premiere.model.Video
import com.premiere.mvi.BaseViewModel
import com.premiere.navigation.imdbId
import com.premiere.repository.MoviesRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MovieDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val moviesRepository: MoviesRepository
) : BaseViewModel<MovieDetailsContract.State, MovieDetailsContract.Intent, MovieDetailsContract.Effect>(
    initialState = MovieDetailsContract.State(isLoading = true)
) {
    private val imdbId: String = savedStateHandle.imdbId

    init {
        loadMovieDetails()
    }

    override fun onIntent(intent: MovieDetailsContract.Intent) {
        when (intent) {
            MovieDetailsContract.Intent.Retry -> loadMovieDetails()
            MovieDetailsContract.Intent.BackClicked -> emitEffect(MovieDetailsContract.Effect.NavigateBack)
            MovieDetailsContract.Intent.PlayTrailerClicked -> {
                state.value.trailerUrl?.let {
                    emitEffect(MovieDetailsContract.Effect.OpenTrailer(it))
                }
            }
        }
    }

    private fun loadMovieDetails() {
        setState { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val movieDeferred = async { moviesRepository.getMovieDetails(imdbId) }
                val castDeferred = async { moviesRepository.getMovieCast(imdbId = imdbId, pageSize = 10) }
                val imagesDeferred = async { moviesRepository.getMovieImages(imdbId = imdbId, type = "backdrop") }
                val videosDeferred = async { moviesRepository.getMovieVideos(imdbId = imdbId, type = "Trailer") }

                val movie = movieDeferred.await()
                val cast = castDeferred.await()
                val images = imagesDeferred.await()
                val videos = videosDeferred.await()

                val actors = cast.items
                    .filter { it.department == "Acting" || it.professions?.contains("actor") == true }
                    .take(10)

                val trailer = videos.firstOrNull { it.site == "YouTube" }

                setState {
                    it.copy(
                        isLoading = false,
                        movie = movie,
                        actors = actors,
                        backdrops = images.backdrops.take(6),
                        trailerUrl = trailer?.toYouTubeUrl(),
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                setState {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to load movie details"
                    )
                }
            }
        }
    }

    private fun Video.toYouTubeUrl(): String? {
        if (site != "YouTube") return null
        return "https://www.youtube.com/watch?v=$key"
    }
}
