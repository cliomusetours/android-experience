package com.cliomuseexperience.core.presentation.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable


@Composable
fun SmoothAppearDisappearAnimation(
    isVisible: Boolean,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(durationMillis = 1000)) +
                scaleIn(initialScale = 0.8f, animationSpec = tween(durationMillis = 1500)),
        exit = fadeOut(animationSpec = tween(durationMillis = 1000)) +
                scaleOut(targetScale = 0.8f, animationSpec = tween(durationMillis = 1500))
    ) {
        content()
    }
}