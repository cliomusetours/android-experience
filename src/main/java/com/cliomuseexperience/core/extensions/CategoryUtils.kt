package com.cliomuseexperience.core.extensions

import androidx.compose.ui.graphics.Color
import com.cliomuseexperience.core.presentation.theme.SdkBlueInfo
import com.cliomuseexperience.core.presentation.theme.SdkBlueInfoBackground
import com.cliomuseexperience.core.presentation.theme.SdkRedInfo
import com.cliomuseexperience.core.presentation.theme.SdkRedInfoBackground
import com.cliomuseexperience.core.presentation.theme.SdkRedInfoBackground2
import com.cliomuseexperience.core.presentation.theme.SdkYellowInfo
import com.cliomuseexperience.core.presentation.theme.SdkYellowInfo2
import com.cliomuseexperience.core.presentation.theme.SdkYellowInfoBackground
import com.cliomuseexperience.experiencecliomuse.R

fun String?.toColor(): Color = when (this?.lowercase()) {
        "info" -> SdkYellowInfo
        "themes" -> SdkBlueInfo
        "tips" -> SdkRedInfo
        "quiz" -> Color.LightGray
        "cultural activity" -> Color.LightGray
        else -> SdkBlueInfo
    }
fun String?.toTextColor(): Color = when (this?.lowercase()) {
    "info" -> SdkYellowInfoBackground
    "themes" -> SdkBlueInfoBackground
    "tips" -> SdkRedInfoBackground
    "quiz" -> Color.LightGray
    "cultural activity" -> Color.LightGray
    else -> SdkBlueInfo
}


fun String?.toBackground(): Color = when (this?.lowercase()) {
        "info" -> SdkYellowInfoBackground
        "themes" -> SdkBlueInfoBackground
        "tips" -> SdkRedInfoBackground
        "quiz" -> Color.LightGray
        "cultural activity" -> Color.LightGray
        else -> SdkBlueInfoBackground
    }



fun String?.toCardBackground(): Color = when (this?.lowercase()) {
    "info" -> SdkYellowInfo2
    "themes" -> Color.White
    "tips" -> SdkRedInfoBackground2
    "quiz" -> Color.LightGray
    "cultural activity" -> Color.LightGray
    else -> Color.White
}

fun String?.toItemBorder(): Color = when (this?.lowercase()) {
    "info" -> SdkYellowInfo2
    "themes" -> SdkBlueInfoBackground
    "tips" -> SdkRedInfo
    "quiz" -> Color.LightGray
    "cultural activity" -> Color.LightGray
    else -> SdkBlueInfoBackground
}

fun String?.toIcon(): Int {
    return when (this?.lowercase()) {
        "directions" -> R.drawable.ic_directions
        "history" -> R.drawable.ic_history
        "alternative activity" -> R.drawable.ic_alternative_b
        "introduction" -> R.drawable.ic_introduction_closure
        "closure" -> R.drawable.ic_introduction_closure
        "shopping" -> R.drawable.ic_shopping
        "gastronomy - restaurant" -> R.drawable.ic_gastronomy1
        "nightlife - bar" -> R.drawable.ic_nightlife
        "selfie stop" -> R.drawable.ic_selfie
        "architecture" -> R.drawable.ic_architecture
        "archeology" -> R.drawable.ic_archeological
        "art" -> R.drawable.ic_art
        "mythology" -> R.drawable.ic_mythology
        "legend" -> R.drawable.ic_legend
        "quirky" -> R.drawable.ic_quirky
        "nature" -> R.drawable.ic_nature
        "kids" -> R.drawable.ic_kids
        "fun" -> R.drawable.ic_fun
        "religion" -> R.drawable.ic_religion
        "tradition/folklore" -> R.drawable.ic_folklore
        "science" -> R.drawable.ic_science
        "alternative activity_" -> R.drawable.ic_alternative_a
        "gastronomy" -> R.drawable.ic_gastronomy
        "nightlife" -> R.drawable.ic_nightlife
        "selfie" -> R.drawable.ic_selfie
        "virtualtours_category" -> R.drawable.ic_virtualtours_category
        else -> R.drawable.ic_directions
    }
}

fun String?.shouldShowCircles(): Boolean {
    return when (this?.lowercase()) {
        "themes"-> true
        else -> false
    }
}