package com.cliomuseexperience.core.extensions

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.cliomuseexperience.feature.experience.domain.model.Tour
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.SpeedCalculator
import com.liulishuo.okdownload.core.breakpoint.BlockInfo
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.core.listener.DownloadListener4WithSpeed
import com.liulishuo.okdownload.core.listener.assist.Listener4SpeedAssistExtend
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


object Constants {
    const val URL_COMPRESSED_TOUR = "https://media.cliomuseappserver.com/tours/tour_"

    // This function returns the path to the app's external files directory for storing downloads
    // If the directory is not available, it falls back to the app's internal files directory
    fun getFilePath(context: Context): String {
        return context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath
            ?: context.filesDir.absolutePath
    }
}

fun startDownload(
    context: Context,
    tourId: Int,
    langId: Int,
    onDownloadProgress: (Float, String, String, String) -> Unit,
    onDownloadFailed: () -> Unit,
    onDownloadComplete: (String) -> Unit
) {
    var totalLength = 0L

    val urlCompressedTour = "${Constants.URL_COMPRESSED_TOUR}${tourId}_${langId}_v1.zip"
    val fileName = "tour_${tourId}_${langId}_v1.zip"
    val filePath = Constants.getFilePath(context)
    val file = File(filePath, fileName)

    if (file.exists()) {
        // If file already exists, skip downloading and proceed to unzip
        val zipFilePath = "$filePath/$fileName"
        val targetDirectoryPath = getDirectoryPath(context, tourId, langId)
        if (unzipFile(zipFilePath, targetDirectoryPath)) {
            onDownloadComplete(targetDirectoryPath)
        } else {
            Log.e("Download", "Error during unzip or JSON file not found")
            onDownloadFailed()
        }
        return
    }

    val task = DownloadTask.Builder(urlCompressedTour, file)
        .setMinIntervalMillisCallbackProcess(30)
        .setPassIfAlreadyCompleted(false)
        .setConnectionCount(1)
        .build()

    task.enqueue(object : DownloadListener4WithSpeed() {
        override fun taskStart(task: DownloadTask) {
            showDownloadProgress(context, 0, 100)
            onDownloadProgress(0f, "0 MB", "0 MB", "0%") // Inicializamos el progreso
        }

        override fun connectStart(
            task: DownloadTask,
            blockIndex: Int,
            requestHeaderFields: MutableMap<String, MutableList<String>>
        ) {
            Log.d("Download", "Connecting to server...")
        }

        override fun connectEnd(
            task: DownloadTask,
            blockIndex: Int,
            responseCode: Int,
            responseHeaderFields: MutableMap<String, MutableList<String>>
        ) {
            Log.d("Download", "Connected to server")
        }

        override fun infoReady(
            task: DownloadTask,
            info: BreakpointInfo,
            fromBreakpoint: Boolean,
            model: Listener4SpeedAssistExtend.Listener4SpeedModel
        ) {
            totalLength = info.totalLength
        }

        override fun progressBlock(
            task: DownloadTask,
            blockIndex: Int,
            currentBlockOffset: Long,
            blockSpeed: SpeedCalculator
        ) {
            Log.d("Download", "Downloading block $blockIndex...")

        }

        override fun progress(
            task: DownloadTask,
            currentOffset: Long,
            taskSpeed: SpeedCalculator
        ) {
            // Calculamos el progreso como porcentaje del total descargado
            val progress = (currentOffset.toFloat() / totalLength.toFloat())
            val progressPercentage = (progress * 100).toInt()

            // Conversión a MB
            val totalMB = totalLength.toFloat() / (1024 * 1024) // Convertimos bytes a MB
            val downloadedMB = currentOffset.toFloat() / (1024 * 1024) // Convertimos bytes a MB

            val totalMBString = String.format("%.2f MB", totalMB)
            val downloadedMBString = String.format("%.2f MB", downloadedMB)
            val progressPercentageString = "$progressPercentage%"


            showDownloadProgress(context, progressPercentage, 100) // Notificación usa Int
            onDownloadProgress(
                progress,
                totalMBString,
                downloadedMBString,
                progressPercentageString
            ) // Actualizamos el progreso con valores en String
        }

        override fun blockEnd(
            task: DownloadTask,
            blockIndex: Int,
            info: BlockInfo?,
            blockSpeed: SpeedCalculator
        ) {
            Log.d("Download", "Block $blockIndex downloaded")
        }

        override fun taskEnd(
            task: DownloadTask,
            cause: EndCause,
            realCause: Exception?,
            taskSpeed: SpeedCalculator
        ) {
            if (cause == EndCause.COMPLETED) {
                showDownloadProgress(context, 100, 100) // Completa la notificación
                onDownloadProgress(1f, "100%", "100%", "100%") // Progreso completo para la UI

                NotificationManagerCompat.from(context).cancel(1001)

                val zipFilePath = "$filePath/$fileName"
                val targetDirectoryPath = getDirectoryPath(context, tourId, langId)

                if (unzipFile(zipFilePath, targetDirectoryPath)) {
                    onDownloadComplete(targetDirectoryPath)
                } else {
                    Log.d("Download", "Error during unzip or JSON file not found")
                    onDownloadFailed()
                }
            } else {
                showDownloadProgress(context, 0, 100)
                NotificationManagerCompat.from(context).cancel(1001)
                Log.d("Download", "Download failed")
                onDownloadFailed()
            }
        }
    })
}

fun unzipFile(zipFilePath: String, targetDirectoryPath: String): Boolean {
    val zipFile = File(zipFilePath)

    if (!zipFile.exists() || !zipFile.isFile) {
        Log.d("Download", "The ZIP file does not exist: $zipFilePath")
        return false
    }

    val targetDir = File(targetDirectoryPath)
    if (targetDir.exists()) {
        targetDir.deleteRecursively()
    }
    if (!targetDir.mkdirs()) {
        Log.d("Download", "Could not create target directory: $targetDirectoryPath")
        return false
    }

    try {
        ZipInputStream(BufferedInputStream(FileInputStream(zipFile))).use { zis ->
            val buffer = ByteArray(1024)
            var entry: ZipEntry? = zis.nextEntry

            while (entry != null) {
                val filePath = "$targetDirectoryPath/${entry.name}"
                if (entry.isDirectory) {
                    val dir = File(filePath)
                    if (!dir.exists() && !dir.mkdirs()) {
                        Log.d("Download", "Could not create directory: $filePath")
                    }
                } else {
                    val newFile = File(filePath)
                    newFile.parentFile?.mkdirs()
                    FileOutputStream(newFile).use { fos ->
                        var len: Int
                        while (zis.read(buffer).also { len = it } > 0) {
                            fos.write(buffer, 0, len)
                        }
                    }
                }
                zis.closeEntry()
                entry = zis.nextEntry
            }
        }
        if (!zipFile.delete()) {
            Log.d("Download", "Could not delete the ZIP file: $zipFilePath")
        }
    } catch (e: IOException) {
        Log.d("Download", "Error unzipping the file: ${e.message}")
        return false
    }

    // Check for the existence of the JSON file
    val jsonFilePath = "$targetDirectoryPath/tour.json"
    val jsonFile = File(jsonFilePath)
    if (!jsonFile.exists() || !jsonFile.isFile) {
        Log.d("Download", "The JSON file was not found after unzipping: $jsonFilePath")
        return false
    }

    return true
}

fun showDownloadProgress(context: Context, progress: Int, maxProgress: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
    }

    val channelId = "download_channel"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Download Progress",
            NotificationManager.IMPORTANCE_LOW
        )
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(channel)
    }

    val notificationBuilder = NotificationCompat.Builder(context, channelId).apply {
        setContentTitle("Download in Progress")
        setContentText("Downloading file...")
        setSmallIcon(android.R.drawable.stat_sys_download)
        setPriority(NotificationCompat.PRIORITY_LOW)
        setOnlyAlertOnce(true)
        setProgress(maxProgress, progress, false)
    }

    val notificationManager = NotificationManagerCompat.from(context)
    notificationManager.notify(1001, notificationBuilder.build())
}

fun readJsonFile(filePath: String): String? {
    return try {
        val file = File(filePath)
        if (file.exists()) {
            file.readText()
        } else {
            null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun parseJsonToTourModel(json: String): Tour {
    val gson = Gson()
    val tourType = object : TypeToken<Tour>() {}.type
    return gson.fromJson(json, tourType)
}

fun getDirectoryPath(context: Context, tourId: Int, langId: Int): String {
    return "${Constants.getFilePath(context)}/tour_${tourId}_${langId}"
}

fun deleteTourFiles(
    context: Context,
    tourId: Int,
    langId: Int,
    onDeleteComplete: () -> Unit,
    onDeleteFailed: (String) -> Unit
) {
    val filePath = Constants.getFilePath(context)
    val fileName = "tour_${tourId}_${langId}_v1.zip"
    val zipFile = File(filePath, fileName)

    // Directory for unzipped files
    val targetDirectoryPath = getDirectoryPath(context, tourId, langId)
    val targetDirectory = File(targetDirectoryPath)

    try {
        // Delete the zip file if it exists
        if (zipFile.exists()) {
            if (!zipFile.delete()) {
                onDeleteFailed("Failed to delete the ZIP file: ${zipFile.absolutePath}")
                return
            }
        }

        // Delete the extracted directory if it exists
        if (targetDirectory.exists()) {
            if (!targetDirectory.deleteRecursively()) {
                onDeleteFailed("Failed to delete the extracted directory: $targetDirectoryPath")
                return
            }
        }

        // Notify that the deletion is complete
        onDeleteComplete()
    } catch (e: Exception) {
        // Handle any exceptions that occur during deletion
        e.printStackTrace()
        onDeleteFailed("An error occurred during deletion: ${e.message}")
    }
}


