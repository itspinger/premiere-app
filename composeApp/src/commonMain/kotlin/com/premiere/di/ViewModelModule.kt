package com.premiere.di

import com.premiere.ui.movies.details.MovieDetailsViewModel
import com.premiere.ui.movies.filter.FilterMoviesViewModel
import com.premiere.ui.movies.list.MoviesListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::MoviesListViewModel)
    viewModelOf(::MovieDetailsViewModel)
    viewModelOf(::FilterMoviesViewModel)
}