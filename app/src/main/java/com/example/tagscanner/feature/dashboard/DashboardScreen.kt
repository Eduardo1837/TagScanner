package com.example.tagscanner.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tagscanner.core.util.formatTimestamp
import com.example.tagscanner.domain.model.InterpretationSeverity
import com.example.tagscanner.domain.model.ScanResult
import com.example.tagscanner.ui.components.ColorSwatch
import com.example.tagscanner.ui.components.EmptyState
import com.example.tagscanner.ui.components.StatusBadge
import com.example.tagscanner.ui.components.screenBackground

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    DashboardContent(uiState = uiState)
}

@Composable
private fun DashboardContent(
    uiState: DashboardUiState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .screenBackground()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111827)
        )

        Spacer(Modifier.height(16.dp))

        SummaryGrid(uiState = uiState)

        Spacer(Modifier.height(16.dp))

        uiState.latestScan?.let { scan ->
            LatestScanCard(scan = scan)
        } ?: EmptyState(
            title = "No scan data",
            description = "Scan tags to see dashboard insights"
        )

        Spacer(Modifier.height(16.dp))

        HueTrendPlaceholder()

        Spacer(Modifier.height(16.dp))

        DistributionCard(
            totalScans = uiState.totalScans,
            normalCount = uiState.normalCount,
            warningCount = uiState.warningCount,
            criticalCount = uiState.criticalCount
        )
    }
}

@Composable
private fun SummaryGrid(
    uiState: DashboardUiState
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SummaryStatCard(
                value = uiState.totalScans.toString(),
                label = "Total Scans",
                valueColor = Color(0xFF2563EB),
                modifier = Modifier.weight(1f)
            )

            SummaryStatCard(
                value = uiState.normalCount.toString(),
                label = "Normal",
                valueColor = Color(0xFF16A34A),
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SummaryStatCard(
                value = uiState.warningCount.toString(),
                label = "Warning",
                valueColor = Color(0xFFD97706),
                modifier = Modifier.weight(1f)
            )

            SummaryStatCard(
                value = uiState.criticalCount.toString(),
                label = "Critical",
                valueColor = Color(0xFFDC2626),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SummaryStatCard(
    value: String,
    label: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )

            Spacer(Modifier.height(2.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF6B7280)
            )
        }
    }
}

@Composable
private fun LatestScanCard(
    scan: ScanResult
) {
    val measurement = scan.colorMeasurement
    val swatch = Color(measurement.red, measurement.green, measurement.blue)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            SectionTitle(
                icon = Icons.Filled.TrackChanges,
                title = "Latest Scan"
            )

            Spacer(Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ColorSwatch(color = swatch, size = 48)

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = scan.interpretation.label,
                            modifier = Modifier.weight(1f),
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF111827)
                        )

                        StatusBadge(severity = scan.interpretation.severity)
                    }

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = formatTimestamp(scan.timestampMillis),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF6B7280)
                    )

                    Spacer(Modifier.height(2.dp))

                    Text(
                        text = "Confidence: ${(measurement.confidence * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF6B7280)
                    )
                }
            }
        }
    }
}

@Composable
private fun HueTrendPlaceholder() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            SectionTitle(
                icon = Icons.Filled.TrendingUp,
                title = "Hue Trend"
            )

            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF9FAFB)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Chart preview will appear here",
                    color = Color(0xFF6B7280),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun DistributionCard(
    totalScans: Int,
    normalCount: Int,
    warningCount: Int,
    criticalCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            SectionTitle(
                icon = Icons.Filled.BarChart,
                title = "Distribution"
            )

            Spacer(Modifier.height(12.dp))

            DistributionRow(
                label = "Normal",
                count = normalCount,
                total = totalScans,
                color = Color(0xFF22C55E)
            )

            Spacer(Modifier.height(10.dp))

            DistributionRow(
                label = "Warning",
                count = warningCount,
                total = totalScans,
                color = Color(0xFFF59E0B)
            )

            Spacer(Modifier.height(10.dp))

            DistributionRow(
                label = "Critical",
                count = criticalCount,
                total = totalScans,
                color = Color(0xFFEF4444)
            )
        }
    }
}

@Composable
private fun DistributionRow(
    label: String,
    count: Int,
    total: Int,
    color: Color
) {
    val progress = if (total == 0) 0f else count.toFloat() / total.toFloat()

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.width(72.dp),
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF6B7280)
        )

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(99.dp)),
            color = color,
            trackColor = Color(0xFFF3F4F6),
            strokeCap = ProgressIndicatorDefaults.LinearStrokeCap
        )

        Text(
            text = count.toString(),
            modifier = Modifier.width(36.dp),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF111827)
        )
    }
}

@Composable
private fun SectionTitle(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF6B7280),
            modifier = Modifier.size(22.dp)
        )

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF111827)
        )
    }
}
