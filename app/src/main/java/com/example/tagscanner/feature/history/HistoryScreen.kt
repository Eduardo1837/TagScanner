package com.example.tagscanner.feature.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tagscanner.core.util.formatTimestamp
import com.example.tagscanner.data.repository.FakeScanData
import com.example.tagscanner.domain.model.ScanResult

@Composable
fun HistoryScreen() {
    val scans = FakeScanData.scans

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Scan History",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(scans) { scan ->
                ScanHistoryItem(scan = scan)
            }
        }
    }
}

@Composable
private fun ScanHistoryItem(
    scan: ScanResult
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = scan.interpretation.label,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = scan.interpretation.description,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Source: ${scan.source}",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "RGB: ${scan.colorMeasurement.red}, ${scan.colorMeasurement.green}, ${scan.colorMeasurement.blue}",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "HSV: ${scan.colorMeasurement.hue}, ${scan.colorMeasurement.saturation}, ${scan.colorMeasurement.value}",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "Confidence: ${(scan.colorMeasurement.confidence * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = formatTimestamp(scan.timestampMillis),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}