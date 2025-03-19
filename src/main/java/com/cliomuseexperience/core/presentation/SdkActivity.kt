package com.cliomuseexperience.core.presentation

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.cliomuseexperience.core.api.Lang_ID
import com.cliomuseexperience.core.api.Tour_ID
import com.cliomuseexperience.player.service.SimpleMediaService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SdkActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        // Clone the original configuration
        val config = Configuration(newBase.resources.configuration)

        // Remove any existing night mode flags
        config.uiMode = config.uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()

        // Set the UI mode to night NO (i.e., day/light)
        config.uiMode = config.uiMode or Configuration.UI_MODE_NIGHT_NO

        // Create a Context with the overridden config
        val overrideContext = newBase.createConfigurationContext(config)

        // Call super with the overridden context
        super.attachBaseContext(overrideContext)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getExtras()
        setContent {
            SdkNavigation()
        }
    }

    private fun getExtras() {
        Tour_ID = intent.getIntExtra("productId" , -1)
        Lang_ID = intent.getIntExtra("productLangId", -1)
    }

    private var isServiceRunning = false

    override fun onDestroy() {
        super.onDestroy()
        isServiceRunning = false
    }

    fun startService() {
        if (!isServiceRunning) {
            val intent = Intent(this, SimpleMediaService::class.java)
            startForegroundService(intent)
            isServiceRunning = true
        }
    }

}