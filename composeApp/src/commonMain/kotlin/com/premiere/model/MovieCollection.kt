package com.premiere.model

import kotlinx.serialization.Serializable

@Serializable
data class MovieCollection(
    override val id: Int,
    val name: String,
    val posterPath: String? = null,
    val backdropPath: String? = null
) : Identifiable<Int>
