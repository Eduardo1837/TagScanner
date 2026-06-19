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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.tagscanner.R
import com.example.tagscanner.core.locale.ClassificationLocalizer
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
            title = stringResource(R.string.scan_details_not_found_title),
            description = uiState.errorMessage ?: stringResource(R.string.scan_details_not_found_desc)
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
            text = stringResource(R.string.scan_details_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF111827)
        )

        scan.imagePath?.let { path ->
            val imageUrl = SupabaseImageStorage().publicUrl(path)

            AsyncImage(
                model = imageUrl,
                contentDescription = stringResource(R.string.scan_details_image_preview_desc),
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
                            text = ClassificationLocalizer.label(scan.interpretation.label),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF111827)
                        )

                        Spacer(Modifier.height(8.dp))

                        StatusBadge(severity = scan.interpretation.severity)

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = stringResource(R.string.scan_details_quality, scan.qualityScore ?: 0),
                            color = Color(0xFF6B7280),
                            style = MaterialTheme.typography.bodySmall
                        )

                        Text(
                            text = stringResource(R.string.scan_details_confidence, (measurement.confidence * 100).toInt()),
                            color = Color(0xFF6B7280),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        val dash = stringResource(R.string.scan_details_placeholder_dash)

        DetailsSection(title = stringResource(R.string.scan_details_section_scan_info)) {
            DetailRow(stringResource(R.string.scan_details_timestamp), formatTimestamp(scan.timestampMillis))
            DetailRow(
                stringResource(R.string.scan_details_source),
                when(scan.source) {
                    ScanSource.LIVE_CAMERA -> stringResource(R.string.source_live_camera)
                    ScanSource.GALLERY_IMAGE -> stringResource(R.string.source_gallery_image)
                }
            )
        }

        Spacer(Modifier.height(12.dp))

        DetailsSection(title = stringResource(R.string.scan_details_section_product_details)) {
            DetailRow(stringResource(R.string.scan_details_provider), details?.provider ?: dash)
            DetailRow(stringResource(R.string.scan_details_product), details?.product ?: dash)
            DetailRow(stringResource(R.string.scan_details_batch), details?.batch ?: dash)
            DetailRow(stringResource(R.string.scan_details_category), details?.category ?: dash)
            DetailRow(stringResource(R.string.scan_details_notes), scan.note ?: dash)
        }

        Spacer(Modifier.height(12.dp))

        DetailsSection(title = stringResource(R.string.scan_details_section_color_values)) {
            DetailRow(stringResource(R.string.scan_details_rgb), "${measurement.red}, ${measurement.green}, ${measurement.blue}")
            DetailRow(
                stringResource(R.string.scan_details_hsv),
                "${measurement.hue.toInt()}, ${measurement.saturation}, ${measurement.value}"
            )
        }

        Spacer(Modifier.height(12.dp))

        DetailsSection(title = stringResource(R.string.scan_details_section_roi)) {
            val roi = scan.regionOfInterest

            if(roi == null) {
                DetailRow(stringResource(R.string.scan_details_roi), dash)
            } else {
                DetailRow(stringResource(R.string.scan_details_roi_x), roi.x.toString())
                DetailRow(stringResource(R.string.scan_details_roi_y), roi.y.toString())
                DetailRow(stringResource(R.string.scan_details_roi_width), roi.width.toString())
                DetailRow(stringResource(R.string.scan_details_roi_height), roi.height.toString())
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
            Text(stringResource(R.string.scan_details_edit))
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = onDeleteClick,
            enabled = !isDeleting,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (isDeleting) stringResource(R.string.scan_details_deleting)
                else stringResource(R.string.scan_details_delete)
            )
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.scan_details_back))
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