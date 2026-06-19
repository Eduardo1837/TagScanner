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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.tagscanner.R
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
            Text(stringResource(R.string.reuse_dialog_title))
        },
        text = {
            Column {
                Text(stringResource(R.string.reuse_dialog_message))

                Spacer(Modifier.height(12.dp))

                Text(stringResource(R.string.reuse_dialog_provider, details.provider))
                Text(stringResource(R.string.reuse_dialog_product, details.product))
                Text(stringResource(R.string.reuse_dialog_batch, details.batch))

                details.category?.let {
                    Text(stringResource(R.string.reuse_dialog_category, it))
                }
            }
        },
        confirmButton = {
            Button(onClick = onConfirmReuse) {
                Text(stringResource(R.string.reuse_dialog_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissReuse) {
                Text(stringResource(R.string.reuse_dialog_dismiss))
            }
        }
    )
}
