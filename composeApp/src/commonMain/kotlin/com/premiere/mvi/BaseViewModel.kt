package com.premiere.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Base class for all ViewModels in the MVI architecture.
 *
 * Manages three unidirectional data flows:
 * - [S] State   — the current UI state, exposed as a [StateFlow]
 * - [I] Intent  — user actions dispatched via [onIntent], processed asynchronously
 * - [F] Effect  — one-time side effects (navigation, toasts) exposed as a [SharedFlow]
 *
 * @param S the type representing UI state
 * @param I the type representing user intents
 * @param F the type representing one-time side effects
 * @param initialState the initial value of the state
 */
abstract class BaseViewModel<S, I, F>(initialState: S) : ViewModel() {

    private val _state = MutableStateFlow(initialState)

    /**
     * The current UI state. Backed by a [MutableStateFlow] so collectors always
     * receive the latest value immediately upon subscription.
     */
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<F>()

    /**
     * Fire-and-forget side effects such as navigation or opening a URL.
     * Backed by a [MutableSharedFlow] with no replay, so effects are only
     * delivered to active collectors and never re-triggered on recomposition.
     */
    val effect = _effect.asSharedFlow()

    private val _intent = MutableSharedFlow<I>()

    init {
        // Collect intents on viewModelScope so handling is decoupled from the UI thread
        viewModelScope.launch {
            _intent.collect { handleIntent(it) }
        }
    }

    /**
     * Entry point for the UI to dispatch a user intent.
     */
    fun onIntent(intent: I) {
        viewModelScope.launch { _intent.emit(intent) }
    }

    /**
     * Called for every intent collected from the internal flow. Subclasses implement
     * their business logic here by switching on the intent type.
     */
    protected abstract fun handleIntent(intent: I)

    /**
     * Applies [reducer] to the current state and atomically updates it.
     */
    protected fun setState(reducer: (S) -> S) {
        _state.update(reducer)
    }

    /**
     * Emits a fire-and-forget side effect to the UI (e.g. navigation, opening a URL).
     * Because effects are backed by a [MutableSharedFlow] with no replay, each effect
     * is delivered once to active collectors and never re-triggered on recomposition.
     */
    protected fun emitEffect(effect: F) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}
