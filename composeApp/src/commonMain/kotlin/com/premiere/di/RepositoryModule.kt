package com.premiere.di

import com.premiere.repository.GenresRepository
import com.premiere.repository.MoviesRepository
import com.premiere.repository.impl.GenresRepositoryImpl
import com.premiere.repository.impl.MoviesRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<MoviesRepository> { MoviesRepositoryImpl(get()) }
    single<GenresRepository> { GenresRepositoryImpl(get()) }
}