package com.cliomuseexperience.feature.experience.domain

import com.cliomuseexperience.core.api.DownloadStatusResponse
import com.cliomuseexperience.feature.experience.domain.model.AccessResponse
import kotlinx.coroutines.flow.Flow

interface ExperienceRepository {
    //fun getRemoteObject(param1: String, param2: String): Flow<Result<AccessResponse>>
    fun getAccess(
        accessCode: String,
        xApiKey: String,
        userIdentifier: String
    ): Flow<Result<AccessResponse>>

    fun updateDownloadTourStatus(
        token: String,
        device: String,
        tourId: Int,
        statusId: Int,
        langId: Int,
        statusMessage: String?,
        operatingSystem: String?,
        operatingSystemVersion: String?,
        appVersion: String?
    ): Flow<Result<DownloadStatusResponse>>



}