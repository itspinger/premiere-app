package com.premiere

import androidx.compose.ui.window.ComposeUIViewController
import com.premiere.di.initKoin

fun MainViewController(): platform.UIKit.UIViewController {
    initKoin()
    return ComposeUIViewController { PremiereApp() }
}