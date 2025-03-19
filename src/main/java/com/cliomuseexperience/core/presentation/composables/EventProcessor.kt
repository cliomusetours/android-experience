package com.cliomuseexperience.core.presentation.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.cliomuseexperience.core.presentation.viewmodel.EventDelegate

/**
 * Collects the events of a ViewModel implementing EventDelegate
 * After the event is consumed, we reset the state of the event to null
 */
@Composable
fun <E> EventProcessor(
    viewModelEventDelegate: EventDelegate<E>,
    content: @Composable (E?)->Unit
) {

    val value = viewModelEventDelegate.viewEvents.collectAsState(initial = null).value

    content(value)

    LaunchedEffect(value){
        if (value != null){
            viewModelEventDelegate.resetEvent()
        }
    }

}