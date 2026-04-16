package com.premiere.model

/**
 * Represents an object that has a unique identifier.
 *
 * @param I Type of the identifier.
 */
interface Identifiable<I> {

    /**
     * Returns the unique identifier of the object.
     */
    val id: I
}