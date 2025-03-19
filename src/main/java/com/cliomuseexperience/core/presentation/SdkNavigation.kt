package com.cliomuseexperience.core.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cliomuseexperience.feature.experience.ui.StartExperienceScreen
import com.cliomuseexperience.feature.map.ui.MapScreen

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun SdkNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "startExperience") {
        composable("startExperience") {
            StartExperienceScreen(navController)
        }
        composable("mapScreen") {
            MapScreen(navController)
        }
    }
}