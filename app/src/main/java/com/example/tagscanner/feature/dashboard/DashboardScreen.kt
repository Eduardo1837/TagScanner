package com.example.tagscanner.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.BarChart
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

        ProviderComparisonCard(
            providers = uiState.providerStats
        )

        Spacer(Modifier.height(16.dp))

        QualityTrendPlaceholder()

        Spacer(Modifier.height(16.dp))

        BatchComparisonCard(
            batches = uiState.batchStats
        )

        Spacer(Modifier.height(16.dp))

        DistributionCard(
            totalScans = uiState.totalScans,
            normalCount = uiState.normalCount,
            warningCount = uiState.warningCount,
            criticalCount = uiState.criticalCount
        )

        Spacer(Modifier.height(16.dp))

        uiState.latestScan?.let { scan ->
            LatestScanCard(scan = scan)
        } ?: EmptyState(
            title = "No scan data",
            description = "Scan tags to see dashboard insights"
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
                valueColor = Color(0xFF111827),
                modifier = Modifier.weight(1f)
            )

            SummaryStatCard(
                value = "${uiState.averageQuality}%",
                label = "Avg Quality",
                valueColor = Color(0xFF16A34A),
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SummaryStatCard(
                value = uiState.bestProvider ?: "-",
                label = "Best Provider",
                valueColor = Color(0xFF111827),
                modifier = Modifier.weight(1f)
            )

            SummaryStatCard(
                value = "${(uiState.criticalRate * 100).toInt()}%",
                label = "Critical Rate",
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
private fun QualityTrendPlaceholder() {
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
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                title = "Quality Trend"
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(132.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                listOf(65, 72, 68, 78, 85, 82, 87, 90).forEach { value ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(value / 100f)
                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                            .background(Color(0xFF2563EB))
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Quality improving over time",
                color = Color(0xFF16A34A),
                style = MaterialTheme.typography.labelSmall
            )
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

@Composable
private fun ProviderComparisonCard(
    providers: List<ProviderStats>
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
            Text(
                text = "Provider Comparison",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF111827)
            )

            Spacer(Modifier.height(12.dp))

            if (providers.isEmpty()) {
                Text(
                    text = "No provider data available",
                    color = Color(0xFF6B7280),
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                providers.forEachIndexed { index, provider ->
                    ProviderRow(
                        rank = index + 1,
                        provider = provider
                    )

                    if (index != providers.lastIndex) {
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ProviderRow(
    rank: Int,
    provider: ProviderStats
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(99.dp))
                .background(Color(0xFFF3F4F6)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = rank.toString(),
                color = Color(0xFF374151),
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Spacer(Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = provider.provider,
                    modifier = Modifier.weight(1f),
                    color = Color(0xFF111827),
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "${provider.averageQuality}%",
                    color = Color(0xFF111827),
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(Modifier.height(2.dp))

            Text(
                text = "${provider.totalScans} scans • ${provider.normalCount} normal • ${provider.warningCount} warning • ${provider.criticalCount} critical",
                color = Color(0xFF6B7280),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun BatchComparisonCard(
    batches: List<BatchStats>
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
            Text(
                text = "Batch Comparison",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF111827)
            )

            Spacer(Modifier.height(12.dp))

            if (batches.isEmpty()) {
                Text(
                    text = "No batch data available",
                    color = Color(0xFF6B7280),
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                batches.forEachIndexed { index, batch ->
                    BatchRow(batch = batch)

                    if (index != batches.lastIndex) {
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun BatchRow(
    batch: BatchStats
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = batch.batch,
                color = Color(0xFF111827),
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = batch.product,
                color = Color(0xFF6B7280),
                style = MaterialTheme.typography.labelSmall
            )

            Spacer(Modifier.height(2.dp))

            Text(
                text = "${batch.warningCount} warnings • ${batch.criticalCount} critical",
                color = Color(0xFF6B7280),
                style = MaterialTheme.typography.labelSmall
            )
        }

        Text(
            text = "${batch.averageQuality}%",
            color = Color(0xFF111827),
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
