package com.example.tagscanner.feature.details

import android.widget.Space
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.tagscanner.core.util.formatTimestamp
import com.example.tagscanner.data.remote.storage.SupabaseImageStorage
import com.example.tagscanner.domain.model.ScanDetails
import com.example.tagscanner.domain.model.ScanResult
import com.example.tagscanner.domain.model.ScanSource
import com.example.tagscanner.ui.components.ColorSwatch
import com.example.tagscanner.ui.components.EmptyState
import com.example.tagscanner.ui.components.StatusBadge
import com.example.tagscanner.ui.components.screenBackground

@Composable
fun ScanDetailsScreen(
    scanId: String,
    onBackClick: () -> Unit,
    viewModel: ScanDetailsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(scanId) {
        viewModel.loadScan(scanId)
    }

    val scan = uiState.scan

    LaunchedEffect(uiState.deleteCompleted) {
        if(uiState.deleteCompleted){
            onBackClick()
        }
    }

    if(scan == null) {
        EmptyState(
            title = "Scan not found",
            description = uiState.errorMessage ?: "This scan could not be loaded"
        )
    } else {
        ScanDetailsContent(
            scan = scan,
            onBackClick = onBackClick,
            isDeleting = uiState.isDeleting,
            onDeleteClick = viewModel::deleteCurrentScan
        )
    }
}

@Composable
private fun ScanDetailsContent(
    scan: ScanResult,
    isDeleting: Boolean,
    onDeleteClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val measurement = scan.colorMeasurement
    val details = scan.details
    val swatchColor = Color(measurement.red, measurement.green, measurement.blue)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .screenBackground()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Scan Details",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF111827)
        )

        scan.imagePath?.let { path ->
            val imageUrl = SupabaseImageStorage().publicUrl(path)

            AsyncImage(
                model = imageUrl,
                contentDescription = "Scan preview",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(12.dp))
        }

        Spacer(Modifier.height(16.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    ColorSwatch(color = swatchColor, size = 96)

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = scan.interpretation.label,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF111827)
                        )

                        Spacer(Modifier.height(8.dp))

                        StatusBadge(severity = scan.interpretation.severity)

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "Quality: ${scan.qualityScore ?: 0}%",
                            color = Color(0xFF6B7280),
                            style = MaterialTheme.typography.bodySmall
                        )

                        Text(
                            text = "Confidence: ${(measurement.confidence * 100).toInt()}%",
                            color = Color(0xFF6B7280),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        DetailsSection(title = "Scan Info") {
            DetailRow("Timestamp", formatTimestamp(scan.timestampMillis))
            DetailRow(
                "Source",
                when(scan.source) {
                    ScanSource.LIVE_CAMERA -> "Live Camera"
                    ScanSource.GALLERY_IMAGE -> "Gallery Image"
                }
            )
        }

        Spacer(Modifier.height(12.dp))

        DetailsSection(title="Product Details") {
            DetailRow("Provider",details?.provider ?: "-")
            DetailRow("Product", details?.product ?: "-")
            DetailRow("Batch", details?.batch ?: "-")
            DetailRow("Category", details?.category ?: "-")
            DetailRow("Notes", scan.note ?: "-")
        }

        Spacer(Modifier.height(12.dp))

        DetailsSection(title = "Color Values") {
            DetailRow("RGB", "${measurement.red}, ${measurement.green}, ${measurement.blue}")
            DetailRow(
                "HSV",
                "${measurement.hue.toInt()}, ${measurement.saturation}, ${measurement.value}"
            )
        }

        Spacer(Modifier.height(12.dp))

        DetailsSection(title="Region Of Interest") {
            val roi = scan.regionOfInterest

            if(roi == null) {
                DetailRow("ROI", "-")
            } else {
                DetailRow("X", roi.x.toString())
                DetailRow("Y", roi.y.toString())
                DetailRow("Width",roi.width.toString())
                DetailRow("Height", roi.height.toString())
            }
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                //TODO
                //edit details
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Edit details")
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = onDeleteClick,
            enabled = !isDeleting,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isDeleting) "Deleting..." else "Delete Scan")
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}

@Composable
private fun DetailsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF111827),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(10.dp))

            content()
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Absolute.SpaceBetween
    ) {
        Text(
            text = label,
            color = Color(0xFF6B7280),
            style = MaterialTheme.typography.bodySmall
        )

        Text(
            text = value,
            color = Color(0xFF111827),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }

    Spacer(Modifier.height(8.dp))
}