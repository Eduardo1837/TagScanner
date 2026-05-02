package com.example.tagscanner.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onLiveScanClick: () -> Unit,
    onGalleryScanClick: () -> Unit,
    onDashboardClick: () -> Unit,
    onHistoryClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(
            space = 16.dp,
            alignment = Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Tag Scanner",
            style = MaterialTheme.typography.headlineMedium
        )


        Button(onClick = onLiveScanClick) {
            Text("Live Scan")
        }

        Button(onClick = onGalleryScanClick) {
            Text("Pick Image")
        }

        Button(onClick = onDashboardClick) {
            Text("Dashboard")
        }

        Button(onClick = onHistoryClick) {
            Text("History")
        }
    }
}