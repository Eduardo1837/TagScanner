package com.example.tagscanner.feature.live

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tagscanner.domain.model.ScanDetails
import com.example.tagscanner.domain.repository.ActiveScanDetailsRepository
import com.example.tagscanner.ui.components.ActiveDetailsCompactCard
import com.example.tagscanner.ui.components.ScannerSaveActions
import com.example.tagscanner.ui.components.screenBackground

@Composable
fun LiveScanScreen(
    onSaveResultClick: () -> Unit
) {
    val activeDetails by ActiveScanDetailsRepository
        .observeActiveDetails()
        .collectAsState(initial = null)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .screenBackground()
    ) {
        LiveScanHeader()

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFF111827))
        ) {
            CameraPreviewPlaceholder()

            RoiFrame(
                modifier = Modifier.align(Alignment.Center)
            )

            Text(
                text = "Camera Preview",
                color = Color.White.copy(alpha = 0.45f),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.Center)
            )

            ScanResultOverlay(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }

        LiveScanBottomActions(
            activeDetails = activeDetails,
            onClearDetailsClick = ActiveScanDetailsRepository::clearActiveDetails,
            onSaveResultClick = onSaveResultClick,
            onSaveWithCurrentClick = {
                // Later: save scan immediately with active details.
            },
            onSaveWithNewClick = onSaveResultClick
        )
    }
}

@Composable
private fun LiveScanHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = "Live Scan",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF111827)
        )

        Text(
            text = "Align the tag inside the frame",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF6B7280)
        )
    }
}

@Composable
private fun CameraPreviewPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111827))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.18f))
    )
}

@Composable
private fun RoiFrame(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(192.dp)
            .border(
                width = 4.dp,
                color = Color.White.copy(alpha = 0.75f),
                shape = RoundedCornerShape(10.dp)
            )
    )
}

@Composable
private fun ScanResultOverlay(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.96f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(Color(0xFF2AB455))
            )

            Spacer(Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Green / Normal",
                    color = Color(0xFF111827),
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(Modifier.height(2.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "Quality: 96%",
                        color = Color(0xFF6B7280),
                        style = MaterialTheme.typography.labelSmall
                    )

                    Text(
                        text = "Confidence: 91%",
                        color = Color(0xFF6B7280),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
private fun LiveScanBottomActions(
    activeDetails: ScanDetails?,
    onClearDetailsClick: () -> Unit,
    onSaveResultClick: () -> Unit,
    onSaveWithCurrentClick: () -> Unit,
    onSaveWithNewClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(12.dp)
    ) {
        activeDetails?.let { details ->
            ActiveDetailsCompactCard(
                details = details,
                onClearClick = onClearDetailsClick
            )

            Spacer(Modifier.height(10.dp))
        }

        ScannerSaveActions(
            activeDetails = activeDetails,
            onSaveResultClick = onSaveResultClick,
            onSaveWithCurrentClick = onSaveWithCurrentClick,
            onSaveWithNewClick = onSaveWithNewClick
        )
    }
}
