package com.example.tagscanner.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tagscanner.domain.model.ScanDetails

@Composable
fun ReuseDetailsDialog(
    details: ScanDetails,
    onConfirmReuse: () -> Unit,
    onDismissReuse: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissReuse,
        title = {
            Text("Reuse these details?")
        },
        text = {
            Column {
                Text("Use this provider, product, and batch for the next scans?")

                Spacer(Modifier.height(12.dp))

                Text("Provider: ${details.provider}")
                Text("Product: ${details.product}")
                Text("Batch: ${details.batch}")

                details.category?.let {
                    Text("Category: $it")
                }
            }
        },
        confirmButton = {
            Button(onClick = onConfirmReuse) {
                Text("Yes, reuse")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissReuse) {
                Text("No, only this scan")
            }
        }
    )
}
