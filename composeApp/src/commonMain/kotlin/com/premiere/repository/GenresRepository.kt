package com.premiere.repository

import com.premiere.model.Genre

interface GenresRepository {

    suspend fun getGenres(): List<Genre>
}
