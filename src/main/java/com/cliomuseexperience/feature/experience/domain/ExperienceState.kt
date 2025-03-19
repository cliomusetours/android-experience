package com.cliomuseexperience.feature.experience.domain

import com.cliomuseexperience.feature.experience.domain.model.Tour
import com.cliomuseexperience.feature.experience.domain.model.User

data class ExperienceState(
    val loading: Boolean = false,
    val user: User? = null,
    val toursList: List<Tour>? = null,
    val tour: Tour? = null,
    val result: String = "",
    val path: String = "",
    val downloading: Boolean = false,
    val downloadProgress: Float = 0f,
    val downloadTotalSize: String = "",
    val downloadCurrentSize: String = "",
    val downloadPercentage: String = "",
    val downloadState: DownloadStatus? = null
)

enum class DownloadStatus {
    IN_PROGRESS,
    COMPLETE,
    FAILED
}

sealed class ExperienceEvent{
    data class Error(val message: String) : ExperienceEvent()
    data object UnknownError : ExperienceEvent()
    data object DownloadingSuccess : ExperienceEvent()
    data object DownloadingFailed : ExperienceEvent()
    data object NavigateToDetail : ExperienceEvent()
    data object NavigateToApp : ExperienceEvent()
    data object StartService : ExperienceEvent()
}
