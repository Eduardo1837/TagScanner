package com.example.tagscanner.feature.live

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tagscanner.domain.model.AnalysisResult
import com.example.tagscanner.domain.model.ScanDetails
import com.example.tagscanner.domain.repository.ActiveScanDetailsRepository
import com.example.tagscanner.ui.components.ActiveDetailsCompactCard
import com.example.tagscanner.ui.components.ScannerSaveActions
import com.example.tagscanner.ui.components.screenBackground
import java.util.concurrent.Executors

@Composable
fun LiveScanScreen(
    onSaveResultClick: () -> Unit,
    viewModel: LiveScanViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val activeDetails by ActiveScanDetailsRepository
        .observeActiveDetails()
        .collectAsState(initial = null)

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.onPermissionChanged(granted)
    }

    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        viewModel.onPermissionChanged(hasPermission)
    }

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
            if (uiState.hasCameraPermission) {
                CameraPreview(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )

                RoiFrame(
                    modifier = Modifier.align(Alignment.Center)
                )

                val result = uiState.currentResult
                if (result != null) {
                    ScanResultOverlay(
                        result = result,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                    )
                } else {
                    Text(
                        text = if (uiState.isAnalyzing) "Analyzing..." else "Align the tag inside the frame",
                        color = Color.White.copy(alpha = 0.78f),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                    )
                }

                uiState.errorMessage?.let { message ->
                    CompactWarning(
                        message = message,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(16.dp)
                    )
                }
            } else {
                PermissionState(
                    onGrantClick = {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                )
            }
        }

        LiveScanBottomActions(
            activeDetails = activeDetails,
            canSave = uiState.currentResult != null,
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
private fun CameraPreview(
    viewModel: LiveScanViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }

    LaunchedEffect(previewView, lifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener(
            {
                try {
                    val cameraProvider = cameraProviderFuture.get()

                    val preview = Preview.Builder()
                        .build()
                        .also { preview ->
                            preview.setSurfaceProvider(previewView.surfaceProvider)
                        }

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also { analysis ->
                            analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                                viewModel.analyzeFrame(imageProxy)
                            }
                        }

                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalysis
                    )
                } catch (exception: Exception) {
                    viewModel.onCameraError("Camera could not be started.")
                }
            },
            ContextCompat.getMainExecutor(context)
        )
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier
    )
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
private fun PermissionState(
    onGrantClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Camera access is required for live scanning.",
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(16.dp))

        Button(onClick = onGrantClick) {
            Text("Grant camera permission")
        }
    }
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
                color = Color.White.copy(alpha = 0.85f),
                shape = RoundedCornerShape(10.dp)
            )
    )
}

@Composable
private fun ScanResultOverlay(
    result: AnalysisResult,
    modifier: Modifier = Modifier
) {
    val measurement = result.colorMeasurement
    val swatchColor = Color(measurement.red, measurement.green, measurement.blue)
    val confidence = (measurement.confidence * 100).toInt()
    val quality = qualityFromResult(result)

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
                    .background(swatchColor)
            )

            Spacer(Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${result.interpretation.label} / ${result.interpretation.severity.name.lowercase().replaceFirstChar { it.uppercase() }}",
                    color = Color(0xFF111827),
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(Modifier.height(2.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "Quality: $quality%",
                        color = Color(0xFF6B7280),
                        style = MaterialTheme.typography.labelSmall
                    )

                    Text(
                        text = "Confidence: $confidence%",
                        color = Color(0xFF6B7280),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
private fun CompactWarning(
    message: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7))
    ) {
        Text(
            text = message,
            color = Color(0xFF92400E),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(10.dp)
        )
    }
}

@Composable
private fun LiveScanBottomActions(
    activeDetails: ScanDetails?,
    canSave: Boolean,
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

        if (canSave) {
            ScannerSaveActions(
                activeDetails = activeDetails,
                onSaveResultClick = onSaveResultClick,
                onSaveWithCurrentClick = onSaveWithCurrentClick,
                onSaveWithNewClick = onSaveWithNewClick
            )
        } else {
            Button(
                onClick = {},
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text("Waiting for detection")
            }
        }
    }
}

private fun qualityFromResult(result: AnalysisResult): Int {
    return when (result.interpretation.severity) {
        com.example.tagscanner.domain.model.InterpretationSeverity.NORMAL -> 96
        com.example.tagscanner.domain.model.InterpretationSeverity.WARNING -> 62
        com.example.tagscanner.domain.model.InterpretationSeverity.CRITICAL -> 24
        com.example.tagscanner.domain.model.InterpretationSeverity.UNKNOWN -> 0
    }
}
