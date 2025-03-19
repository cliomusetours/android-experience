package com.cliomuseexperience.core.presentation.viewmodel

import kotlinx.coroutines.flow.Flow

interface EventDelegate<E> {
    val viewEvents: Flow<E?>
    suspend fun sendEvent(event:E)
    suspend fun resetEvent()
}