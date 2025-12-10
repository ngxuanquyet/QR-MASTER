package com.qrmaster.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.qrmaster.ui.theme.QRMasterColors

@Composable
fun ColorPickerRow(
    label: String,
    currentHex: String,
    onColorSelected: (String) -> Unit
) {
    val palette = listOf(
        "#000000", "#ffffff", "#cf3434", "#2563eb", "#7c3aed", "#ec4899",
        "#f97316", "#84cc16", "#0891b2", "#06b6d4", "#8b5cf6",
        "#d946ef", "#f43f5e", "#f59e0b", "#10b981"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = Color(0xFFB3B3B3), style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(12.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(palette) { hex ->
                val selected = hex.equals(currentHex, ignoreCase = true)
                Box(
                    modifier = Modifier
                        .size(if (selected) 56.dp else 48.dp)
                        .clip(CircleShape)
                        .background(Color(hex.toColorInt()))
                        .border(
                            width = if (selected) 5.dp else 3.dp,
                            color = if (selected) QRMasterColors.Accent else Color(0x33FFFFFF),
                            shape = CircleShape
                        )
                        .clickable { onColorSelected(hex) }
                )
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String, isAddress: Boolean = false) {
    if (value.isBlank()) return
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text("$label : ", color = Color(0xFFB3B3B3), style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.width(2.dp))
        Text(
            text = value,
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = if (isAddress) 2 else 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}