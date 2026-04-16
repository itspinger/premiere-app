package com.premiere.model

import kotlinx.serialization.Serializable

@Serializable
data class Genre(override val id: Int, val name: String) : Identifiable<Int>