package com.example.tagscanner.feature.gallery

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tagscanner.domain.model.AnalysisResult

@Composable
fun GalleryScanScreen(
    viewModel: GalleryScanViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if(uri != null){
            viewModel.onImageSelected(
                context = context,
                uri = uri
            )
        }
    }

    GalleryScanContent(
        uiState = uiState,
        onPickImageClick = {
            imagePickerLauncher.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }
    )
}

@Composable
private fun GalleryScanContent(
    uiState: GalleryScanUiState,
    onPickImageClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Gallery Scan",
            style = MaterialTheme.typography.headlineMedium
        )

        Button(
            onClick = onPickImageClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Pick Image")
        }

        if(uiState.isLoading){
            CircularProgressIndicator()
        }

        if(uiState.errorMessage != null){
            Text(
                text = uiState.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        val result = uiState.analysisResult

        if(result != null){
            AnalysisResultCard(result = result)
        } else {
            Text(
                text = "Pick an image to analyze the tag color.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun AnalysisResultCard(
    result: AnalysisResult
) {
    val measurement = result.colorMeasurement

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = result.interpretation.label,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = result.interpretation.description,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "RGB: ${measurement.red}, ${measurement.green}, ${measurement.blue}",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "Hue: ${measurement.hue}",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "Saturation: ${measurement.saturation}",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "Value: ${measurement.value}",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "Confidence: ${(measurement.confidence * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}