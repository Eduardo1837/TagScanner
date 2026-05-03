package com.example.tagscanner.feature.save

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tagscanner.domain.repository.ActiveScanDetailsRepository
import com.example.tagscanner.ui.components.ColorSwatch
import com.example.tagscanner.ui.components.ReuseDetailsDialog
import com.example.tagscanner.ui.components.screenBackground

@Composable
fun SaveScanDetailsScreen(
    onFinishSave: () -> Unit,
    onCancelClick: () -> Unit,
    viewModel: SaveScanDetailsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    SaveScanDetailsContent(
        uiState = uiState,
        onProviderChanged = viewModel::onProviderChanged,
        onProductChanged = viewModel::onProductChanged,
        onBatchChanged = viewModel::onBatchChanged,
        onCategoryChanged = viewModel::onCategoryChanged,
        onNoteChanged = viewModel::onNoteChanged,
        onSuggestionClick = viewModel::applyProviderSuggestion,
        onSaveClick = viewModel::onSaveScanClicked,
        onCancelClick = onCancelClick
    )

    uiState.pendingReuseDetails?.let { details ->
        ReuseDetailsDialog(
            details = details,
            onConfirmReuse = {
                ActiveScanDetailsRepository.setActiveDetails(details)
                viewModel.clearPendingReuseDetails()
                onFinishSave()
            },
            onDismissReuse = {
                viewModel.clearPendingReuseDetails()
                onFinishSave()
            }
        )
    }
}

@Composable
private fun SaveScanDetailsContent(
    uiState: SaveScanDetailsUiState,
    onProviderChanged: (String) -> Unit,
    onProductChanged: (String) -> Unit,
    onBatchChanged: (String) -> Unit,
    onCategoryChanged: (String) -> Unit,
    onNoteChanged: (String) -> Unit,
    onSuggestionClick: (String) -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .screenBackground()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Save Scan Details",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF111827)
        )

        Spacer(Modifier.height(16.dp))

        ScanResultPreview(uiState = uiState)

        Spacer(Modifier.height(16.dp))

        DetailsTextField(
            value = uiState.provider,
            onValueChange = onProviderChanged,
            label = "Provider *",
            placeholder = "Enter provider name"
        )

        Spacer(Modifier.height(12.dp))

        DetailsTextField(
            value = uiState.product,
            onValueChange = onProductChanged,
            label = "Product *",
            placeholder = "Enter product name"
        )

        Spacer(Modifier.height(12.dp))

        DetailsTextField(
            value = uiState.batch,
            onValueChange = onBatchChanged,
            label = "Batch Code *",
            placeholder = "Enter batch code"
        )

        Spacer(Modifier.height(12.dp))

        DetailsTextField(
            value = uiState.category,
            onValueChange = onCategoryChanged,
            label = "Category",
            placeholder = "Optional category"
        )

        Spacer(Modifier.height(12.dp))

        DetailsTextField(
            value = uiState.note,
            onValueChange = onNoteChanged,
            label = "Notes",
            placeholder = "Optional notes"
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Recent Suggestions",
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF6B7280)
        )

        Spacer(Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            uiState.providerSuggestions.take(2).forEach { suggestion ->
                SuggestionChip(
                    text = suggestion,
                    onClick = { onSuggestionClick(suggestion) }
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = onSaveClick,
            enabled = uiState.canSave,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save scan")
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = onCancelClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancel")
        }
    }
}

@Composable
private fun ScanResultPreview(
    uiState: SaveScanDetailsUiState
) {
    val measurement = uiState.scanResult?.colorMeasurement
    val interpretation = uiState.scanResult?.interpretation

    val swatchColor = if (measurement != null) {
        Color(measurement.red, measurement.green, measurement.blue)
    } else {
        Color(0xFF2AB455)
    }

    val status = interpretation?.label ?: "Green / Normal"
    val quality = "96%"
    val confidence = measurement?.confidence
        ?.times(100)
        ?.toInt()
        ?.let { "$it%" }
        ?: "91%"

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ColorSwatch(color = swatchColor, size = 48)

            Column {
                Text(
                    text = status,
                    color = Color(0xFF111827),
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Quality: $quality • Confidence: $confidence",
                    color = Color(0xFF6B7280),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
private fun DetailsTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = Color(0xFF2563EB),
            unfocusedBorderColor = Color(0xFFE5E7EB)
        )
    )
}

@Composable
private fun SuggestionChip(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(Color.White, CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = Color(0xFF374151),
            style = MaterialTheme.typography.labelSmall
        )
    }
}
