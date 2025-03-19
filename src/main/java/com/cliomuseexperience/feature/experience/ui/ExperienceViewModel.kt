package com.cliomuseexperience.feature.experience.ui

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cliomuseexperience.core.api.PATH_EXTRA
import com.cliomuseexperience.core.api.TOUR_EXTRA
import com.cliomuseexperience.core.presentation.viewmodel.EventDelegate
import com.cliomuseexperience.core.presentation.viewmodel.EventDelegateViewModel
import com.cliomuseexperience.feature.experience.domain.DownloadStatus
import com.cliomuseexperience.feature.experience.domain.ExperienceEvent
import com.cliomuseexperience.feature.experience.domain.ExperienceRepository
import com.cliomuseexperience.feature.experience.domain.ExperienceState
import dagger.hilt.android.lifecycle.HiltViewModel
import com.cliomuseexperience.core.extensions.getDirectoryPath
import com.cliomuseexperience.core.extensions.parseJsonToTourModel
import com.cliomuseexperience.core.extensions.readJsonFile
import com.cliomuseexperience.core.extensions.saveLastTourInfo
import com.cliomuseexperience.core.extensions.startDownload
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ExperienceViewModel
@Inject constructor(
    @Named("sdk") private val experienceRepository: ExperienceRepository
) : ViewModel(), EventDelegate<ExperienceEvent> by EventDelegateViewModel() {

    var viewState by mutableStateOf(ExperienceState())


    init {
        viewModelScope.launch {
            viewState = viewState.copy(
                loading = true
            )
        }
    }

    fun getAccess(context: Context, tourId: Int, langId: Int) {
        saveLastTourInfo(context, tourId, langId)

        viewState = viewState.copy(loading = true)

        val targetDirectoryPath = getDirectoryPath(context, tourId, langId)

        if (!isTourDownloaded(context, tourId, langId)) {
            viewModelScope.launch {
                viewState = viewState.copy(
                    downloading = true,
                    downloadState = DownloadStatus.IN_PROGRESS
                )
                startDownload(
                    context = context,
                    tourId = tourId,
                    langId = langId,
                    onDownloadProgress = { progress, totalMB, downloadedMB, percentage ->
                        viewState = viewState.copy(
                            downloadProgress = progress,
                            downloadTotalSize = totalMB,
                            downloadCurrentSize = downloadedMB,
                            downloadPercentage = percentage
                        )
                    },
                    onDownloadFailed = {
                        viewModelScope.launch {
                            viewState = viewState.copy(
                                downloadState = DownloadStatus.FAILED
                            )
                            sendEvent(ExperienceEvent.DownloadingFailed)
                        }
                    },
                )

                { zipFilePath ->
                    //    E.g. statusId = 11 (DOWNLOAD_COMPLETED_SUCCESSFULLY)
                    viewModelScope.launch {
                        viewState = viewState.copy(
                            downloadState = DownloadStatus.COMPLETE,
                            downloading = false
                        )

                        processDownloadedTour(zipFilePath, 1000)
                    }
                }
            }
        } else {
            processDownloadedTour(targetDirectoryPath)
        }
    }


    private fun processDownloadedTour(zipFilePath: String, delayTime: Long = 0) {
        viewModelScope.launch {
            getTourFromJson(zipFilePath)
            delay(delayTime)
            sendEvent(ExperienceEvent.DownloadingSuccess)
        }
    }

    private fun getTourFromJson(zipFilePath: String) {
        viewModelScope.launch {
            val jsonFilePath = "$zipFilePath/tour.json"
            val jsonContent = readJsonFile(jsonFilePath)
            if (jsonContent != null) {
                val tour = parseJsonToTourModel(jsonContent)

                TOUR_EXTRA = tour
                PATH_EXTRA = zipFilePath
                viewState = viewState.copy(
                    tour = tour,
                    path = zipFilePath
                )
            } else {
                Log.e("Download", "No se pudo leer el archivo JSON: $jsonFilePath")
            }
        }
    }

    private fun isTourDownloaded(context: Context, tourId: Int, langId: Int): Boolean {
        val targetDirectoryPath = getDirectoryPath(context, tourId, langId)
        val targetDirectory = File(targetDirectoryPath)

        if (targetDirectory.exists() && targetDirectory.isDirectory) {
            val keyFile = File(
                targetDirectoryPath
            )
            if (keyFile.exists()) {
                return true
            }
        }
        return false
    }

    fun closeDownloadModal() {
        viewModelScope.launch {
            viewState = viewState.copy(
                downloading = false,
                downloadState = null
            )
        }
    }

    fun navigateToDetail() {
        viewModelScope.launch {
            sendEvent(ExperienceEvent.NavigateToDetail)
        }
    }

    fun navigateToApp() {
        viewModelScope.launch {
            sendEvent(ExperienceEvent.NavigateToApp)
        }
    }
}


