package com.premiere.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Person(
    @SerialName("imdbId")
    override val id: String,
    val name: String,
    val professions: String? = null,
    val department: String? = null,
    val profilePath: String? = null
) : Identifiable<String>