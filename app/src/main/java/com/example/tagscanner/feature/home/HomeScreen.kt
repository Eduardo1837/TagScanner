package com.example.tagscanner.feature.home

import android.widget.Space
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
    uiState: HomeUiState,
    onLiveScanClick: () -> Unit,
    onGalleryScanClick: () -> Unit,
    onDashboardClick: () -> Unit,
    onHistoryClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .screenBackground()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Tag Scanner", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(
            "Analyze tags using live camera or images from gallery.",
            color = Color(0xFF6B7280),
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(20.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)){
            ActionTitle("Live Scan", "Real-time camera", true, onLiveScanClick, Modifier.weight(1f))
            ActionTitle("Pick Image", "From gallery", false, onGalleryScanClick, Modifier.weight(1f))
        }

       if(uiState.totalScans > 0){
           Spacer(Modifier.height(16.dp))

           Card(
               colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
               elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
           ) {
               Row(
                   modifier = Modifier
                       .fillMaxWidth()
                       .padding(16.dp),
                   horizontalArrangement = Arrangement.SpaceBetween
               ) {
                   QuickStat(uiState.totalScans.toString(), "Total scans")
                   QuickStat(uiState.latestResult ?: "-", "Latest result")
                   QuickStat("${uiState.latestConfidence ?: 0}%", "Confidence")
               }
           }
       }

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Recent Scans", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text("View all", color = Color(0xFF2563EB), modifier = Modifier.padding(top = 2.dp))
        }

        Spacer(Modifier.height(12.dp))

        if(uiState.recentScans.isEmpty()){
            EmptyState("No scans yet", "Start scanning tags to see your history here")
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                uiState.recentScans.forEach { scan ->
                    ScanCard(scan = scan, onClick = onHistoryClick)
                }
            }
        }
    }
}

@Composable
private fun QuickStat(value: String, label: String) {
    Column {
        Text(value, color = Color(0xFF2563EB), fontWeight = FontWeight.Bold)
        Text(label, color = Color(0xFF6B7280), style = MaterialTheme.typography.labelSmall)
    }
}