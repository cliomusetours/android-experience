package com.cliomuseexperience.feature.experience.data

import com.cliomuseexperience.core.api.ApiService
import com.cliomuseexperience.core.api.DownloadStatusRequest
import com.cliomuseexperience.core.api.DownloadStatusResponse
import com.cliomuseexperience.core.api.FailureFactory
import com.cliomuseexperience.core.extensions.safeCall
import com.cliomuseexperience.feature.experience.domain.ExperienceRepository
import com.cliomuseexperience.feature.experience.domain.model.AccessResponse
import com.cliomuseexperience.feature.experience.domain.request.AccessRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class ExperienceDataRepository(
    private val apiService: ApiService
    ) : ExperienceRepository {

    override fun getAccess(
        accessCode: String,
        xApiKey: String,
        userIdentifier: String,
    ): Flow<Result<AccessResponse>> = flow {
        emit(apiService.getAccess(
            accessRequest = AccessRequest(
                accessCode = accessCode,
                userIdentifier = userIdentifier
            ),
            xApiKey = xApiKey,
            device = "Android"
        ).safeCall({ response -> response })
        )
    }.catch {
        emit(FailureFactory<AccessResponse>().handleException(it))
    }


    override fun updateDownloadTourStatus(
        token: String,
        device: String,
        tourId: Int,
        statusId: Int,
        langId: Int,
        statusMessage: String?,
        operatingSystem: String?,
        operatingSystemVersion: String?,
        appVersion: String?
    ): Flow<Result<DownloadStatusResponse>> = flow {
        emit(
            apiService.updateDownloadTourStatus(
                tourId = tourId,
                statusId = statusId,
                token = token,
                device = device,
                request = DownloadStatusRequest(
                    langId = langId,
                    statusMessage = statusMessage,
                    operatingSystem = operatingSystem,
                    operatingSystemVersion = operatingSystemVersion,
                    appVersion = appVersion
                )
            ).safeCall({ response -> response })
        )
    }.catch {
        emit(FailureFactory<DownloadStatusResponse>().handleException(it))
    }

}