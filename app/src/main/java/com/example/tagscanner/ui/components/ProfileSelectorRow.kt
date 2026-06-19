package com.example.tagscanner.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.tagscanner.R
import com.example.tagscanner.domain.model.LabelProfile
import com.example.tagscanner.domain.repository.ActiveLabelProfileRepository

// ── Icon mapping (UI layer — keeps domain model free of Compose deps) ────────

@Composable
private fun LabelProfile.ProfileIcon(modifier: Modifier = Modifier, tint: Color) {
    when (this) {
        is LabelProfile.ProduseLactate ->
            Icon(imageVector = Icons.Filled.Opacity, contentDescription = null, tint = tint, modifier = modifier)
        is LabelProfile.PesteSiFructeDeMare ->
            Icon(painter = painterResource(R.drawable.ic_fish), contentDescription = null, tint = tint, modifier = modifier)
        is LabelProfile.CarneRosie ->
            Icon(painter = painterResource(R.drawable.ic_cow_head), contentDescription = null, tint = tint, modifier = modifier)
        is LabelProfile.CarneDePassare ->
            Icon(painter = painterResource(R.drawable.ic_chicken_head), contentDescription = null, tint = tint, modifier = modifier)
    }
}

// ── Colors per profile ───────────────────────────────────────────────────────

private val LabelProfile.accentColor: Color
    get() = when (this) {
        is LabelProfile.ProduseLactate      -> Color(0xFF0284C7) // sky-600
        is LabelProfile.PesteSiFructeDeMare -> Color(0xFF0891B2) // cyan-600
        is LabelProfile.CarneRosie          -> Color(0xFFDC2626) // red-600
        is LabelProfile.CarneDePassare      -> Color(0xFF7C3AED) // violet-600
    }

private val LabelProfile.accentBackground: Color
    get() = when (this) {
        is LabelProfile.ProduseLactate      -> Color(0xFFE0F2FE) // sky-100
        is LabelProfile.PesteSiFructeDeMare -> Color(0xFFCFFAFE) // cyan-100
        is LabelProfile.CarneRosie          -> Color(0xFFFEE2E2) // red-100
        is LabelProfile.CarneDePassare      -> Color(0xFFEDE9FE) // violet-100
    }

// ── Public composable ────────────────────────────────────────────────────────

/**
 * Shows the active label profile as a tappable chip.
 * Tapping opens a full picker dialog with a 2-column category grid.
 */
@Composable
fun ProfileSelectorRow(modifier: Modifier = Modifier) {
    val activeProfile by ActiveLabelProfileRepository
        .observeActiveProfile()
        .collectAsState(initial = LabelProfile.default())

    var showPicker by remember { mutableStateOf(false) }

    val chevronAngle by animateFloatAsState(
        targetValue = if (showPicker) 180f else 0f,
        label = "chevron"
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Category:",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF6B7280)
        )

        Spacer(Modifier.width(6.dp))

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(activeProfile.accentBackground)
                .border(
                    width = 1.dp,
                    color = activeProfile.accentColor.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable { showPicker = true }
                .padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            activeProfile.ProfileIcon(
                tint = activeProfile.accentColor,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = activeProfile.displayName,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = activeProfile.accentColor
            )
            Icon(
                imageVector = Icons.Filled.ExpandMore,
                contentDescription = "Change category",
                tint = activeProfile.accentColor,
                modifier = Modifier
                    .size(16.dp)
                    .rotate(chevronAngle)
            )
        }
    }

    if (showPicker) {
        ProfilePickerDialog(
            activeProfile = activeProfile,
            onSelect = {
                ActiveLabelProfileRepository.setProfile(it)
                showPicker = false
            },
            onDismiss = { showPicker = false }
        )
    }
}

// ── Picker dialog ────────────────────────────────────────────────────────────

@Composable
private fun ProfilePickerDialog(
    activeProfile: LabelProfile,
    onSelect: (LabelProfile) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    // Header
                    Text(
                        text = "Product Type",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF111827)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Select the category of the scanned product",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF6B7280)
                    )

                    Spacer(Modifier.height(16.dp))

                    // 2-column grid
                    val profiles = LabelProfile.all
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        profiles.chunked(2).forEach { rowProfiles ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                rowProfiles.forEach { profile ->
                                    ProfileCard(
                                        profile = profile,
                                        isSelected = profile.id == activeProfile.id,
                                        onClick = { onSelect(profile) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                // Fill remaining space if row has only 1 item
                                if (rowProfiles.size < 2) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileCard(
    profile: LabelProfile,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) profile.accentColor else Color(0xFFE5E7EB)
    val bgColor = if (isSelected) profile.accentBackground else Color(0xFFF9FAFB)
    val contentColor = if (isSelected) profile.accentColor else Color(0xFF374151)
    val borderWidth = if (isSelected) 2.dp else 1.dp

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(
                width = borderWidth,
                color = borderColor,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (isSelected) profile.accentColor.copy(alpha = 0.15f)
                        else Color(0xFFEEEEEE)
                    ),
                contentAlignment = Alignment.Center
            ) {
                profile.ProfileIcon(
                    tint = contentColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = profile.displayName,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = contentColor,
                maxLines = 2,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }

        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(18.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(profile.accentColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(11.dp)
                )
            }
        }
    }
}
