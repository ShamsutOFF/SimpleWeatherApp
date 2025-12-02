package com.example.simpleweatherapp.ui.theme

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign.Companion.Center

@Composable
fun ErrorDialog(
    title: String,
    text: String,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onConfirm,
        title = {
            Text(
                text = title,
                textAlign = Center,
                style = typography.headlineSmall,
            )
        },
        text = {
            Text(
                text = text,
                modifier = Modifier.verticalScroll(state = rememberScrollState()),
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "OK", style = typography.labelLarge)
            }
        },
        containerColor = colorScheme.errorContainer,
    )
}