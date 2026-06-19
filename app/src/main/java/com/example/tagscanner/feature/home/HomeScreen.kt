package com.example.tagscanner.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tagscanner.R
import com.example.tagscanner.core.locale.AppLanguage
import com.example.tagscanner.core.locale.LocaleManager
import com.example.tagscanner.domain.model.ScanDetails
import com.example.tagscanner.ui.components.EmptyState
import com.example.tagscanner.ui.components.ScanCard
import com.example.tagscanner.ui.components.screenBackground

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onLiveScanClick: () -> Unit,
    onGalleryScanClick: () -> Unit,
    onDashboardClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onClearActiveDetails: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .screenBackground()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.home_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF111827)
            )

            LanguageToggle()
        }

        Spacer(Modifier.height(4.dp))

        Text(
            text = stringResource(R.string.home_subtitle),
            color = Color(0xFF6B7280),
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            HomeActionCard(
                title = stringResource(R.string.home_action_live_scan),
                icon = Icons.Filled.CameraAlt,
                onClick = onLiveScanClick,
                modifier = Modifier.weight(1f)
            )

            HomeActionCard(
                title = stringResource(R.string.home_action_pick_image),
                icon = Icons.Filled.Image,
                onClick = onGalleryScanClick,
                modifier = Modifier.weight(1f)
            )
        }

        uiState.activeDetails?.let { details ->
            Spacer(Modifier.height(16.dp))
            ActiveDetailsPreview(
                details = details,
                onClearClick = onClearActiveDetails
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.home_dashboard_preview),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF111827)
        )

        Spacer(Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PreviewStatCard(stringResource(R.string.home_stat_total_scans), uiState.totalScans.toString(), Color(0xFF111827), Modifier.weight(1f))
                PreviewStatCard(stringResource(R.string.home_stat_avg_quality), "${uiState.averageQuality}%", Color(0xFF16A34A), Modifier.weight(1f))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PreviewStatCard(stringResource(R.string.home_stat_best_provider), uiState.bestProvider ?: "-", Color(0xFF111827), Modifier.weight(1f))
                PreviewStatCard(stringResource(R.string.home_stat_critical_scans), uiState.criticalScans.toString(), Color(0xFFDC2626), Modifier.weight(1f))
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.home_recent_scans),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF111827)
        )

        Spacer(Modifier.height(12.dp))

        if (uiState.recentScans.isEmpty()) {
            EmptyState(
                title = stringResource(R.string.home_empty_title),
                description = stringResource(R.string.home_empty_desc)
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                uiState.recentScans.forEach { scan ->
                    ScanCard(
                        scan = scan,
                        onClick = onHistoryClick
                    )
                }
            }
        }
    }
}

@Composable
private fun LanguageToggle(modifier: Modifier = Modifier) {
    val current by LocaleManager.currentLanguage.collectAsState()

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF3F4F6)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppLanguage.entries.forEach { language ->
            val selected = language == current
            Text(
                text = language.tag.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) Color.White else Color(0xFF6B7280),
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (selected) Color(0xFF2563EB) else Color.Transparent)
                    .clickable { LocaleManager.setLanguage(language) }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
private fun HomeActionCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(104.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
        border = CardDefaults.outlinedCardBorder(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFF2563EB),
                modifier = Modifier.size(32.dp)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = title,
                color = Color(0xFF1E3A8A),
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun ActiveDetailsPreview(
    details: ScanDetails,
    onClearClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.home_active_details),
                    color = Color(0xFF111827),
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = stringResource(R.string.home_clear),
                    color = Color(0xFF2563EB),
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.clickable(onClick = onClearClick)
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(stringResource(R.string.home_provider, details.provider), color = Color(0xFF374151), style = MaterialTheme.typography.bodySmall)
            Text(stringResource(R.string.home_product, details.product), color = Color(0xFF374151), style = MaterialTheme.typography.bodySmall)
            Text(stringResource(R.string.home_batch, details.batch), color = Color(0xFF374151), style = MaterialTheme.typography.bodySmall)

            details.category?.let {
                Text(stringResource(R.string.home_category, it), color = Color(0xFF374151), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun PreviewStatCard(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = label,
                color = Color(0xFF6B7280),
                style = MaterialTheme.typography.labelSmall
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = value,
                color = valueColor,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
