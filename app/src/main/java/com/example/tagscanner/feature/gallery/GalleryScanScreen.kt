package com.example.tagscanner.feature.gallery

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tagscanner.domain.model.AnalysisResult
import com.example.tagscanner.domain.model.ScanDetails
import com.example.tagscanner.domain.repository.ActiveScanDetailsRepository
import com.example.tagscanner.ui.components.ActiveDetailsCompactCard
import com.example.tagscanner.ui.components.ScannerSaveActions
import com.example.tagscanner.ui.components.screenBackground

@Composable
fun GalleryScanScreen(
    viewModel: GalleryScanViewModel = viewModel(),
    onSaveResultClick: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val activeDetails by ActiveScanDetailsRepository
        .observeActiveDetails()
        .collectAsState(initial = null)

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.onImageSelected(
                context = context,
                uri = uri
            )
        }
    }

    GalleryScanContent(
        uiState = uiState,
        activeDetails = activeDetails,
        onPickImageClick = {
            imagePickerLauncher.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        },
        onClearDetailsClick = ActiveScanDetailsRepository::clearActiveDetails,
        onSaveResultClick = onSaveResultClick,
        onSaveWithCurrentClick = {
            // Later: save scan immediately with active details.
        },
        onSaveWithNewClick = onSaveResultClick
    )
}

@Composable
private fun GalleryScanContent(
    uiState: GalleryScanUiState,
    activeDetails: ScanDetails?,
    onPickImageClick: () -> Unit,
    onClearDetailsClick: () -> Unit,
    onSaveResultClick: () -> Unit,
    onSaveWithCurrentClick: () -> Unit,
    onSaveWithNewClick: () -> Unit
) {
    val hasImage = uiState.selectedImageUri != null
    val result = uiState.analysisResult

    Column(
        modifier = Modifier
            .fillMaxSize()
            .screenBackground()
    ) {
        GalleryScanHeader()

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(if (hasImage) Color(0xFFD1D5DB) else Color(0xFFF3F4F6)),
            contentAlignment = Alignment.Center
        ) {
            if (!hasImage) {
                EmptyImageState()
            } else {
                SelectedImagePlaceholder()

                RoiFrame(
                    modifier = Modifier.align(Alignment.Center)
                )

                Text(
                    text = "Selected Image",
                    color = Color(0xFF4B5563),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Center)
                )

                if (result != null) {
                    GalleryResultOverlay(
                        result = result,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                    )
                }
            }

            if (uiState.isLoading) {
                CircularProgressIndicator()
            }

            uiState.errorMessage?.let { error ->
                ErrorOverlay(
                    message = error,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                )
            }
        }

        GalleryBottomActions(
            hasImage = hasImage,
            activeDetails = activeDetails,
            onPickImageClick = onPickImageClick,
            onClearDetailsClick = onClearDetailsClick,
            onSaveResultClick = onSaveResultClick,
            onSaveWithCurrentClick = onSaveWithCurrentClick,
            onSaveWithNewClick = onSaveWithNewClick
        )
    }
}

@Composable
private fun GalleryScanHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = "Gallery Scan",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF111827)
        )

        Text(
            text = "Pick an image and analyze the tag color",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF6B7280)
        )
    }
}

@Composable
private fun EmptyImageState() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.Upload,
            contentDescription = null,
            tint = Color(0xFF9CA3AF),
            modifier = Modifier.size(64.dp)
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "No image selected",
            color = Color(0xFF6B7280),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun SelectedImagePlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD1D5DB))
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
                color = Color(0xFF2563EB).copy(alpha = 0.78f),
                shape = RoundedCornerShape(10.dp)
            )
    )
}

@Composable
private fun GalleryResultOverlay(
    result: AnalysisResult,
    modifier: Modifier = Modifier
) {
    val measurement = result.colorMeasurement
    val swatch = Color(measurement.red, measurement.green, measurement.blue)

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
                    .background(swatch)
            )

            Spacer(Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = result.interpretation.label,
                    color = Color(0xFF111827),
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(Modifier.height(2.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "Quality: 62%",
                        color = Color(0xFF6B7280),
                        style = MaterialTheme.typography.labelSmall
                    )

                    Text(
                        text = "Confidence: ${(measurement.confidence * 100).toInt()}%",
                        color = Color(0xFF6B7280),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorOverlay(
    message: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2))
    ) {
        Text(
            text = message,
            color = Color(0xFF991B1B),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Composable
private fun GalleryBottomActions(
    hasImage: Boolean,
    activeDetails: ScanDetails?,
    onPickImageClick: () -> Unit,
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
        if (!hasImage) {
            Button(
                onClick = onPickImageClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text("Pick image")
            }
        } else {
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
}
