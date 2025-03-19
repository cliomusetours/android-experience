package com.cliomuseexperience.core.presentation.viewmodel

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.receiveAsFlow

open class EventDelegateViewModel<E> : EventDelegate<E> {

    private val _viewEvents = Channel<E?>(Channel.BUFFERED)

    override val viewEvents: Flow<E?> = _viewEvents
        .receiveAsFlow()
        .conflate()

    override suspend fun sendEvent(event:E) {
        _viewEvents.send(event)
    }

    override suspend fun resetEvent() {
        _viewEvents.send(null)
    }

}