package com.premiere.ui.movies.details

import com.premiere.model.MovieDetail
import com.premiere.model.MovieImage
import com.premiere.model.Person

interface MovieDetailsContract {

    data class State(
        val isLoading: Boolean = false,
        val movie: MovieDetail? = null,
        val actors: List<Person> = emptyList(),
        val backdrops: List<MovieImage> = emptyList(),
        val trailerUrl: String? = null,
        val errorMessage: String? = null
    )

    sealed interface Intent {
        data object Retry : Intent
        data object BackClicked : Intent
        data object PlayTrailerClicked : Intent
    }

    sealed interface Effect {
        data object NavigateBack : Effect
        data class OpenTrailer(val url: String) : Effect
    }
}
