package com.example.tagscanner.feature.home

import android.widget.Space
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tagscanner.domain.model.ScanResult
import com.example.tagscanner.ui.components.ActionTitle
import com.example.tagscanner.ui.components.EmptyState
import com.example.tagscanner.ui.components.ScanCard
import com.example.tagscanner.ui.components.screenBackground

@Composable
fun HomeScreen(
    onLiveScanClick: () -> Unit,
    onGalleryScanClick: () -> Unit,
    onDashboardClick: () -> Unit,
    onHistoryClick: () -> Unit,
    recentScans: List<ScanResult> = emptyList(),
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .screenBackground()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Tag Scanner",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111827)
        )

        Spacer(Modifier.height(4.dp))

       Text(
           text = "Analyze colored tags",
           style = MaterialTheme.typography.bodyMedium,
           color = Color(0xFF6B7280)
       )

        Spacer(Modifier.height(20.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)){
            ActionTitle(
                title = "Live Scan",
                subtitle = "Real-time camera",
                primary = true,
                onClick = onLiveScanClick,
                modifier = Modifier.weight(1f)
            )

            ActionTitle(
                title = "Pick Image",
                subtitle = "From gallery",
                primary = false,
                onClick = onGalleryScanClick,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Recent Scans",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF111827)
        )

        Spacer(Modifier.height(12.dp))

        if(recentScans.isEmpty()){
            EmptyState(
                title = "No scans yet",
                description = "Start scanning tags to see history here"
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                recentScans.take(3).forEach { scan ->
                    ScanCard(
                        scan = scan,
                        onClick = onHistoryClick
                    )
                }
            }
        }
    }
}