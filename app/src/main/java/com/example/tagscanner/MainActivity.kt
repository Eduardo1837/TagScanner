package com.example.tagscanner

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.tagscanner.core.locale.LocaleManager
import com.example.tagscanner.core.navigation.AppNavHost
import com.example.tagscanner.ui.theme.TagScannerTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TagScannerTheme {
                val currentLanguage by LocaleManager.currentLanguage.collectAsState()
                Crossfade(targetState = currentLanguage, label = "localeFade") {
                    AppNavHost()
                }
            }
        }
    }

    /**
     * On API <33 (no platform per-app LocaleManager), AppCompatDelegate applies a
     * locale change by calling recreate() directly, which bypasses any
     * android:configChanges declaration and always does a real destroy+create.
     * Restarting via finish()/startActivity() instead of the default in-place
     * relaunch lets us fade between the old and new window instead of showing
     * a black frame.
     */
    override fun recreate() {
        val restartIntent = intent
        finish()
        @Suppress("DEPRECATION")
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        startActivity(restartIntent)
        @Suppress("DEPRECATION")
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}