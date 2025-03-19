package com.cliomuseexperience.core.extensions

import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit
import kotlin.time.toDuration


fun Long.toTimeString(): String {
    if (this <= 0) return "00:00"

    val duration = this.toDuration(DurationUnit.MILLISECONDS)
    val minutes = duration.inWholeMinutes
    val seconds = (duration - minutes.minutes).inWholeSeconds
    return String.format("%02d:%02d", minutes, seconds)
}