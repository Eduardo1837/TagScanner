package com.example.tagscanner.feature.gallery

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tagscanner.R
import com.example.tagscanner.core.locale.ClassificationLocalizer
import com.example.tagscanner.domain.model.AnalysisResult
import com.example.tagscanner.domain.model.ScanDetails
import com.example.tagscanner.domain.repository.ActiveScanDetailsRepository
import com.example.tagscanner.domain.repository.PendingScanResultRepository
import com.example.tagscanner.ui.components.ActiveDetailsCompactCard
import com.example.tagscanner.ui.components.ProfileSelectorRow
import com.example.tagscanner.ui.components.ScannerSaveActions
import com.example.tagscanner.ui.components.screenBackground
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.tagscanner.core.util.qualityScoreFor
import kotlinx.coroutines.delay
import kotlin.math.min
import kotlin.math.roundToInt

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
        onRoiChange = viewModel::onRoiChanged,
        onClearDetailsClick = ActiveScanDetailsRepository::clearActiveDetails,
        onSaveResultClick = {
            uiState.analysisResult?.let { result ->
                viewModel.preparePendingScan(
                    context = context,
                    onReady = onSaveResultClick
                )
            }
        },
        onSaveWithCurrentClick = {
            viewModel.preparePendingScan(
                context = context,
                initialDetails = activeDetails,
                onReady = onSaveResultClick
            )
        },
        onSaveWithNewClick = {
            uiState.analysisResult?.let { result ->
                viewModel.preparePendingScan(
                    context = context,
                    onReady = onSaveResultClick
                )
            }
        }
    )
}

@Composable
private fun GalleryScanContent(
    uiState: GalleryScanUiState,
    activeDetails: ScanDetails?,
    onPickImageClick: () -> Unit,
    onRoiChange: (fraction: Float, offsetXFraction: Float, offsetYFraction: Float) -> Unit,
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
                SelectedImagePreview(
                    imageUri = uiState.selectedImageUri,
                    modifier = Modifier.fillMaxSize()
                )

                ResizableRoiFrame(
                    roiFraction = uiState.roiFraction,
                    roiOffsetXFraction = uiState.roiOffsetXFraction,
                    roiOffsetYFraction = uiState.roiOffsetYFraction,
                    onRoiChange = onRoiChange,
                    modifier = Modifier.fillMaxSize()
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
            text = stringResource(R.string.gallery_scan_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF111827)
        )

        Text(
            text = stringResource(R.string.gallery_scan_subtitle),
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF6B7280)
        )

        Spacer(Modifier.height(8.dp))

        ProfileSelectorRow()
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
            text = stringResource(R.string.gallery_scan_no_image_selected),
            color = Color(0xFF6B7280),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun SelectedImagePreview(
    imageUri: android.net.Uri?,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = imageUri,
        contentDescription = stringResource(R.string.gallery_scan_selected_image_desc),
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}

private const val MIN_ROI_FRACTION = 0.10f
private const val MAX_ROI_FRACTION = 0.85f
private const val ROI_COMMIT_DEBOUNCE_MS = 250L

/**
 * Square region-of-interest overlay, resizable and movable by gesture over the
 * image: pinch to resize, drag to reposition (both work together in one
 * two-finger gesture too). The box updates instantly for smooth visual
 * feedback; the analyzed region is only pushed up (triggering a re-analysis)
 * after the gesture settles, so dragging/pinching stays smooth instead of
 * re-sampling the bitmap every frame.
 */
@Composable
private fun ResizableRoiFrame(
    roiFraction: Float,
    roiOffsetXFraction: Float,
    roiOffsetYFraction: Float,
    onRoiChange: (fraction: Float, offsetXFraction: Float, offsetYFraction: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    var liveFraction by remember { mutableFloatStateOf(roiFraction) }
    var liveOffsetPx by remember { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current

    LaunchedEffect(containerSize, roiOffsetXFraction, roiOffsetYFraction) {
        if (containerSize != IntSize.Zero) {
            liveOffsetPx = Offset(
                x = roiOffsetXFraction * containerSize.width,
                y = roiOffsetYFraction * containerSize.height
            )
        }
    }

    LaunchedEffect(liveFraction, liveOffsetPx) {
        delay(ROI_COMMIT_DEBOUNCE_MS)
        val offsetXFraction = if (containerSize.width > 0) liveOffsetPx.x / containerSize.width else 0f
        val offsetYFraction = if (containerSize.height > 0) liveOffsetPx.y / containerSize.height else 0f
        onRoiChange(liveFraction, offsetXFraction, offsetYFraction)
    }

    Box(
        modifier = modifier
            .onSizeChanged { containerSize = it }
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    liveFraction = (liveFraction * zoom).coerceIn(MIN_ROI_FRACTION, MAX_ROI_FRACTION)

                    val roiSizePx = min(containerSize.width, containerSize.height) * liveFraction
                    val maxOffsetX = ((containerSize.width - roiSizePx) / 2f).coerceAtLeast(0f)
                    val maxOffsetY = ((containerSize.height - roiSizePx) / 2f).coerceAtLeast(0f)

                    liveOffsetPx = Offset(
                        x = (liveOffsetPx.x + pan.x).coerceIn(-maxOffsetX, maxOffsetX),
                        y = (liveOffsetPx.y + pan.y).coerceIn(-maxOffsetY, maxOffsetY)
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        val shortestSidePx = min(containerSize.width, containerSize.height)
        if (shortestSidePx > 0) {
            val boxSizeDp = with(density) { (shortestSidePx * liveFraction).toDp() }
            val offsetPx = IntOffset(liveOffsetPx.x.roundToInt(), liveOffsetPx.y.roundToInt())
            Box(
                modifier = Modifier
                    .size(boxSizeDp)
                    .offset { offsetPx }
                    .border(
                        width = 4.dp,
                        color = Color(0xFF2563EB).copy(alpha = 0.78f),
                        shape = RoundedCornerShape(10.dp)
                    )
            )
        }
    }
}

@Composable
private fun GalleryResultOverlay(
    result: AnalysisResult,
    modifier: Modifier = Modifier
) {
    val measurement = result.colorMeasurement
    val swatch = Color(measurement.red, measurement.green, measurement.blue)
    val quality = qualityScoreFor(result)

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
                    text = stringResource(
                        R.string.gallery_scan_label_severity,
                        ClassificationLocalizer.label(result.interpretation.label),
                        ClassificationLocalizer.severityLabel(result.interpretation.severity)
                    ),
                    color = Color(0xFF111827),
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(Modifier.height(2.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = stringResource(R.string.gallery_scan_quality, quality.toString()),
                        color = Color(0xFF6B7280),
                        style = MaterialTheme.typography.labelSmall
                    )

                    Text(
                        text = stringResource(R.string.gallery_scan_confidence, (measurement.confidence * 100).toInt()),
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
                Text(stringResource(R.string.gallery_scan_pick_image))
            }
        } else {
            activeDetails?.let { details ->
                ActiveDetailsCompactCard(
                    details = details,
                    onClearClick = onClearDetailsClick
                )

                Spacer(Modifier.height(10.dp))
            }

            OutlinedButton(
                onClick = onPickImageClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(stringResource(R.string.gallery_scan_choose_another_photo))
            }

            Spacer(Modifier.height(10.dp))

            ScannerSaveActions(
                activeDetails = activeDetails,
                onSaveResultClick = onSaveResultClick,
                onSaveWithCurrentClick = onSaveWithCurrentClick,
                onSaveWithNewClick = onSaveWithNewClick
            )
        }
    }
}
