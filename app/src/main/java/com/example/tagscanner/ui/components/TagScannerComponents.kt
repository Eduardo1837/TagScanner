package com.example.tagscanner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tagscanner.core.util.formatTimestamp
import com.example.tagscanner.domain.model.InterpretationSeverity
import com.example.tagscanner.domain.model.ScanResult
import com.example.tagscanner.domain.model.ScanSource
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.OutlinedButton
import com.example.tagscanner.domain.model.ScanDetails

private val AppBlue = Color(0xFF2563EB)
private val PageBackground = Color(0xFFF9FAFB)

fun Modifier.screenBackground() = this.background(PageBackground)

@Composable
fun ActionTitle(
    title: String,
    subtitle: String,
    primary: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val background = if (primary) AppBlue else Color.White
    val titleColor = if (primary) Color.White else Color(0xFF111827)
    val subtitleColor = if (primary) Color(0xFFDBEAFE) else Color(0xFF6B7280)

    Card(
        modifier = modifier
            .height(132.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = background),
        border = if (primary) null else CardDefaults.outlinedCardBorder(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(title, color = titleColor, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text(subtitle, color = subtitleColor, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun ColorSwatch(
    color: Color,
    size: Int = 48
) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .background(color, RoundedCornerShape(10.dp))
            .border(2.dp, Color(0xFFE5E7EB), RoundedCornerShape(10.dp))
    )
}

@Composable
fun StatusBadge(severity: InterpretationSeverity) {
    val background: Color
    val foreground: Color
    val label: String

    when (severity) {
        InterpretationSeverity.NORMAL -> {
            background = Color(0xFFDCFCE7)
            foreground = Color(0xFF166534)
            label = "Normal"
        }
        InterpretationSeverity.WARNING -> {
            background = Color(0xFFFEF3C7)
            foreground = Color(0xFF92400E)
            label = "Warning"
        }
        InterpretationSeverity.CRITICAL -> {
            background = Color(0xFFFEE2E2)
            foreground = Color(0xFF991B1B)
            label = "Critical"
        }
        InterpretationSeverity.UNKNOWN -> {
            background = Color(0xFFF3F4F6)
            foreground = Color(0xFF374151)
            label = "Unknown"
        }
    }

    Surface(
        color = background,
        contentColor = foreground,
        shape = CircleShape
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ScanCard(
    scan: ScanResult,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val measurement = scan.colorMeasurement
    val swatch = Color(measurement.red, measurement.green, measurement.blue)
    val details = scan.details

    val sourceLabel = when (scan.source) {
        ScanSource.LIVE_CAMERA -> "Live Camera"
        ScanSource.GALLERY_IMAGE -> "Gallery Image"
    }

    val sourceIcon = when (scan.source) {
        ScanSource.LIVE_CAMERA -> Icons.Filled.CameraAlt
        ScanSource.GALLERY_IMAGE -> Icons.Filled.Image
    }

    val title = details?.provider ?: scan.interpretation.label
    val subtitle = if (details != null) {
        "${details.product} • ${details.batch}"
    } else {
        scan.interpretation.description
    }

    val qualityText = scan.qualityScore?.let { "$it%" }
        ?: "${(measurement.confidence * 100).toInt()}%"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ColorSwatch(color = swatch)

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            color = Color(0xFF111827),
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(Modifier.height(2.dp))

                        Text(
                            text = subtitle,
                            color = Color(0xFF6B7280),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    StatusBadge(scan.interpretation.severity)
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Quality: $qualityText • Confidence: ${(measurement.confidence * 100).toInt()}%",
                    color = Color(0xFF6B7280),
                    style = MaterialTheme.typography.labelSmall
                )

                Spacer(Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = sourceIcon,
                        contentDescription = sourceLabel,
                        tint = Color(0xFF6B7280),
                        modifier = Modifier.size(14.dp)
                    )

                    Text(
                        text = formatTimestamp(scan.timestampMillis),
                        color = Color(0xFF6B7280),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyState(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(Color(0xFFF3F4F6), CircleShape)
        )

        Spacer(Modifier.height(16.dp))

        Text(title, fontWeight = FontWeight.Medium, color = Color(0xFF111827))
        Spacer(Modifier.height(4.dp))
        Text(description, color = Color(0xFF6B7280), style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun ActiveDetailsCompactCard(
    details: ScanDetails,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
        border = CardDefaults.outlinedCardBorder(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Active Details",
                    color = Color(0xFF111827),
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.labelMedium
                )

                Text(
                    text = "Clear",
                    color = Color(0xFF2563EB),
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.clickable(onClick = onClearClick)
                )
            }

            Spacer(Modifier.height(4.dp))

            Text(
                text = "${details.provider} - ${details.product} - ${details.batch}",
                color = Color(0xFF374151),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun ScannerSaveActions(
    activeDetails: ScanDetails?,
    onSaveResultClick: () -> Unit,
    onSaveWithCurrentClick: () -> Unit,
    onSaveWithNewClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (activeDetails == null) {
            Button(
                onClick = onSaveResultClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text("Save result")
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onSaveWithCurrentClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                ) {
                    Text("Save with current")
                }

                OutlinedButton(
                    onClick = onSaveWithNewClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                ) {
                    Text("Save with new")
                }
            }
        }
    }
}

