package com.example.tagscanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.tagscanner.core.navigation.AppNavHost
import com.example.tagscanner.ui.theme.TagScannerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TagScannerTheme {
                AppNavHost()
            }
        }
    }
}