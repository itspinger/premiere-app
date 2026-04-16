package com.premiere.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<S, I, F>(initialState: S) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<F>()
    val effect = _effect.asSharedFlow()

    protected fun setState(reducer: (S) -> S) {
        _state.update(reducer)
    }

    protected fun emitEffect(effect: F) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }

    abstract fun onIntent(intent: I)
}
