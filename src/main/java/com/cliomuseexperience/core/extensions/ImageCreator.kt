package com.cliomuseexperience.core.extensions

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.cliomuseexperience.core.api.PATH_EXTRA
import java.io.File

fun String.toTourImage(): ImageBitmap? {
    val routeImage = "$PATH_EXTRA/museums/museums_img/${this}"
    val file = File(routeImage)

    return if (file.exists()) {
        BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
    } else {
        null
    }
}

fun String.toTourThumbImage(): ImageBitmap? {
    val routeImage = "$PATH_EXTRA/museums/museums_thumb/${this}"
    val file = File(routeImage)

    return if (file.exists()) {
        BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
    } else {
        null
    }
}


fun String.toTourAuthorImage(): ImageBitmap? {
    val routeImage = "$PATH_EXTRA/museums/logos/${this}"
    val file = File(routeImage)

    return if (file.exists()) {
        BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
    } else {
        null
    }
}


fun String.toTourGroundImage(): ImageBitmap? {
    val routeImage = "$PATH_EXTRA/museums/ground_img/${this}"
    val file = File(routeImage)

    return if (file.exists()) {
        BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
    } else {
        null
    }
}


fun String.toPointsListImage(): ImageBitmap? {
    val routeImage = "$PATH_EXTRA/exhibits/exhibit_img/${this}"
    val file = File(routeImage)

    return if (file.exists()) {
        BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
    } else {
        null
    }
}


fun String.toPointsListThumbImage(): ImageBitmap? {
    val routeImage = "$PATH_EXTRA/exhibits/exhibit_thumb/${this}"
    val file = File(routeImage)

    return if (file.exists()) {
        BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
    } else {
        null
    }
}


fun String.toPointsListThumbImageUri(): Uri? {
    val routeImage = "$PATH_EXTRA/exhibits/exhibit_thumb/$this"
    val file = File(routeImage)

    return if (file.exists()) {
        Uri.fromFile(file)
    } else {
        null
    }
}

fun String.toAudioTour(): String? {
    val routeaudio = "$PATH_EXTRA/exhibits/historical_background/hb_audio/${this}"
    val file = File(routeaudio)

    return if (file.exists()) {
        file.absolutePath
    } else {
        null
    }
}

fun String.toMultimediaImage(): ImageBitmap? {
    val routeImage = "$PATH_EXTRA/exhibits/historical_background/hb_img/${this}"
    val file = File(routeImage)

    return if (file.exists()) {
        BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
    } else {
        null
    }
}



