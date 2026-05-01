package com.example.tagscanner.feature.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tagscanner.core.util.formatTimestamp
import com.example.tagscanner.data.repository.FakeScanData
import com.example.tagscanner.domain.model.InterpretationSeverity

@Composable
fun DashboardScreen() {
    val scans = FakeScanData.scans
    val latestScan = scans.maxByOrNull { it.timestampMillis }

    val normalCount = scans.count {
        it.interpretation.severity == InterpretationSeverity.NORMAL
    }

    val warningCount = scans.count {
        it.interpretation.severity == InterpretationSeverity.WARNING
    }

    val criticalCount = scans.count {
        it.interpretation.severity == InterpretationSeverity.CRITICAL
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.headlineMedium
        )

        SummaryCard(
            title = "Total scans",
            value = scans.size.toString()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SmallSummaryCard(
                title = "Normal",
                value = normalCount.toString(),
                modifier = Modifier.weight(1f)
            )

            SmallSummaryCard(
                title = "Warning",
                value = warningCount.toString(),
                modifier = Modifier.weight(1f)
            )

            SmallSummaryCard(
                title = "Critical",
                value = criticalCount.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        if (latestScan != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Latest scan",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = latestScan.interpretation.label,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Text(
                        text = latestScan.interpretation.description,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = formatTimestamp(latestScan.timestampMillis),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    value: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge
            )
        }
    }
}

@Composable
private fun SmallSummaryCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}