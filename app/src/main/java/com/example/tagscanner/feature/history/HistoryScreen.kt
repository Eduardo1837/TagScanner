package com.example.tagscanner.feature.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tagscanner.domain.model.ScanResult
import com.example.tagscanner.ui.components.EmptyState
import com.example.tagscanner.ui.components.ScanCard
import com.example.tagscanner.ui.components.screenBackground

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    HistoryContent(
        uiState = uiState,
        onFilterSelected = viewModel::onFilterSelected,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onScanClick = {
            //TODO
        }
    )
}

@Composable
private fun HistoryContent(
    uiState: HistoryUiState,
    onFilterSelected: (HistoryFilter) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onScanClick: (ScanResult) -> Unit
) {
    val filters = listOf(
        HistoryFilter.All,
        HistoryFilter.Normal,
        HistoryFilter.Warning,
        HistoryFilter.Critical,
        HistoryFilter.Unknown
    )

    val filteredScans = uiState.scans
        .filter { scan ->
            uiState.selectedFilter.severity == null ||
                    scan.interpretation.severity == uiState.selectedFilter.severity
        }
        .filter { scan ->
            val query = uiState.searchQuery

            query.isBlank() ||
                    scan.interpretation.label.contains(query, ignoreCase = true) ||
                    scan.interpretation.description.contains(query, ignoreCase = true) ||
                    scan.details?.provider.orEmpty().contains(query, ignoreCase = true) ||
                    scan.details?.product.orEmpty().contains(query, ignoreCase = true) ||
                    scan.details?.batch.orEmpty().contains(query, ignoreCase = true) ||
                    scan.details?.category.orEmpty().contains(query, ignoreCase = true) ||
                    scan.note.orEmpty().contains(query, ignoreCase = true)
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .screenBackground()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        SearchField(
            value = uiState.searchQuery,
            onValueChange = onSearchQueryChanged
        )

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.FilterList,
                contentDescription = "Filter",
                tint = Color(0xFF6B7280),
                modifier = Modifier.size(22.dp)
            )

            filters.forEach {filter ->
                val count = if (filter.severity == null){
                    uiState.scans.size
                } else {
                    uiState.scans.count { it.interpretation.severity == filter.severity}
                }

                FilterChip(
                    label = "${filter.label} ($count)",
                    selected = uiState.selectedFilter == filter,
                    onClick = { onFilterSelected(filter) }
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        if(filteredScans.isEmpty()){
            EmptyState(
                title = "No scans found",
                description = if (uiState.selectedFilter == HistoryFilter.All && uiState.searchQuery.isBlank()) {
                    "No scans saved yet."
                } else {
                    "No scans match your filters"
                }
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredScans) {scan ->
                    ScanCard(
                        scan = scan,
                        onClick = { onScanClick(scan)}
                    )
                }
            }
        }
    }

}

@Composable
private fun SearchField(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text("Search scans...")
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                tint = Color(0xFF9CA3Af)
            )
        },
        singleLine = true,
        shape = MaterialTheme.shapes.large,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = Color(0xFF2563EB),
            unfocusedBorderColor = Color(0xFFE5E7EB)
        )
    )
}

@Composable
private fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val background = if (selected) Color(0xFF2563EB) else Color(0xFFF3F4F6)
    val foreground = if (selected) Color.White else Color(0xFF374151)

    Box(
        modifier = Modifier
            .background(background, CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            color = foreground,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

