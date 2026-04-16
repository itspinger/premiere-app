package com.premiere.repository.impl

import com.premiere.api.PremiereApi
import com.premiere.model.Genre
import com.premiere.repository.GenresRepository

class GenresRepositoryImpl(private val api: PremiereApi) : GenresRepository {

    override suspend fun getGenres(): List<Genre> = api.getGenres()
}
