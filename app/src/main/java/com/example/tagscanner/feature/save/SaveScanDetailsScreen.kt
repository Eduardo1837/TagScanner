package com.example.tagscanner.feature.save

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tagscanner.R
import com.example.tagscanner.core.locale.ClassificationLocalizer
import com.example.tagscanner.core.util.qualityScoreFor
import com.example.tagscanner.domain.repository.ActiveScanDetailsRepository
import com.example.tagscanner.domain.repository.PendingScanResultRepository
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
        onNoteChanged = viewModel::onNoteChanged,
        onSuggestionClick = viewModel::applyProviderSuggestion,
        onSaveClick = viewModel::onSaveScanClicked,
        onCancelClick = {
            PendingScanResultRepository.clearPendingScan()
            onCancelClick()
        }
    )

    uiState.pendingReuseDetails?.let { details ->
        ReuseDetailsDialog(
            details = details,
            onConfirmReuse = {
                ActiveScanDetailsRepository.setActiveDetails(details)
                PendingScanResultRepository.clearPendingScan()
                viewModel.clearPendingReuseDetails()
                onFinishSave()
            },
            onDismissReuse = {
                PendingScanResultRepository.clearPendingScan()
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
            text = stringResource(R.string.save_scan_title),
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
            label = stringResource(R.string.save_scan_provider_label),
            placeholder = stringResource(R.string.save_scan_provider_placeholder)
        )

        Spacer(Modifier.height(12.dp))

        DetailsTextField(
            value = uiState.product,
            onValueChange = onProductChanged,
            label = stringResource(R.string.save_scan_product_label),
            placeholder = stringResource(R.string.save_scan_product_placeholder)
        )

        Spacer(Modifier.height(12.dp))

        DetailsTextField(
            value = uiState.batch,
            onValueChange = onBatchChanged,
            label = stringResource(R.string.save_scan_batch_label),
            placeholder = stringResource(R.string.save_scan_batch_placeholder)
        )

        Spacer(Modifier.height(12.dp))

        DetailsTextField(
            value = uiState.category,
            onValueChange = {},
            label = stringResource(R.string.save_scan_category_label),
            placeholder = "",
            enabled = !uiState.categoryLocked
        )

        Spacer(Modifier.height(12.dp))

        DetailsTextField(
            value = uiState.note,
            onValueChange = onNoteChanged,
            label = stringResource(R.string.save_scan_notes_label),
            placeholder = stringResource(R.string.save_scan_notes_placeholder)
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.save_scan_recent_suggestions),
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF6B7280)
        )

        Spacer(Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            uiState.providerSuggestions.forEach { suggestion ->
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
            Text(stringResource(R.string.save_scan_save_button))
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = onCancelClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.save_scan_cancel_button))
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

    val status = if (interpretation != null){
        stringResource(
            R.string.save_scan_status_label_severity,
            ClassificationLocalizer.label(interpretation.label),
            ClassificationLocalizer.severityLabel(interpretation.severity)
        )
    } else {
        stringResource(R.string.save_scan_no_result)
    }
    val quality = uiState.scanResult
        ?.let { "${qualityScoreFor(it)}%" }
        ?: stringResource(R.string.save_scan_dash)
    val confidence = measurement?.confidence
        ?.times(100)
        ?.toInt()
        ?.let { "$it%" }
        ?: stringResource(R.string.save_scan_default_confidence)

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
                    text = stringResource(R.string.save_scan_quality_confidence, quality, confidence),
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
    placeholder: String,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        enabled = enabled,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = Color(0xFF2563EB),
            unfocusedBorderColor = Color(0xFFE5E7EB),
            disabledContainerColor = Color(0xFFF3F4F6),
            disabledBorderColor = Color(0xFFE5E7EB),
            disabledTextColor = Color(0xFF374151),
            disabledLabelColor = Color(0xFF6B7280)
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
