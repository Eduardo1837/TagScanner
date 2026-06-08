package com.example.tagscanner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tagscanner.domain.model.LabelProfile
import com.example.tagscanner.domain.repository.ActiveLabelProfileRepository

/**
 * A compact, self-contained row that shows the active label profile and lets
 * the user switch it via a dropdown.  It reads from and writes to
 * [ActiveLabelProfileRepository] directly, so it works in any screen header.
 *
 * Usage: drop it inside any Column-based header section.
 */
@Composable
fun ProfileSelectorRow(modifier: Modifier = Modifier) {
    val activeProfile by ActiveLabelProfileRepository
        .observeActiveProfile()
        .collectAsState(initial = LabelProfile.default())

    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Profile:",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF6B7280)
        )

        Spacer(Modifier.width(6.dp))

        Box {
            // Active profile chip — tapping opens the dropdown
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        width = 1.dp,
                        color = Color(0xFF2563EB).copy(alpha = 0.35f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .background(Color(0xFFEFF6FF))
                    .clickable { expanded = true }
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${activeProfile.emoji}  ${activeProfile.displayName}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1D4ED8)
                )
                Spacer(Modifier.width(2.dp))
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Change profile",
                    tint = Color(0xFF1D4ED8),
                    modifier = Modifier.size(16.dp)
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                LabelProfile.all.forEach { profile ->
                    val isSelected = profile.id == activeProfile.id
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = profile.emoji,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = profile.displayName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (isSelected) Color(0xFF1D4ED8) else Color(0xFF111827)
                                )
                            }
                        },
                        onClick = {
                            ActiveLabelProfileRepository.setProfile(profile)
                            expanded = false
                        },
                        trailingIcon = if (isSelected) {
                            {
                                Text(
                                    text = "✓",
                                    color = Color(0xFF1D4ED8),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        } else null
                    )
                }
            }
        }
    }
}
