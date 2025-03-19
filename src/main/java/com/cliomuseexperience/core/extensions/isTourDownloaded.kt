package com.cliomuseexperience.core.extensions

import com.liulishuo.okdownload.OkDownloadProvider.context
import java.io.File

fun isTourDownloaded(tourId: Int, langId: Int): Boolean {
        val targetDirectoryPath = getDirectoryPath(context,tourId, langId)
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