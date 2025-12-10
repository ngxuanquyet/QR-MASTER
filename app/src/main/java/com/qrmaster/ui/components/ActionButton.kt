package com.qrmaster.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.qrmaster.R

@Composable
fun ActionButtons(
    onCopyClick: (() -> Unit)? = null,
    onShareClick: (() -> Unit)? = null,
    onSaveClick: (() -> Unit)? = null,
    onWebSearchClick: (() -> Unit)? = null,
) {
    // Tạo danh sách button động
    val buttons = remember(onCopyClick, onShareClick, onSaveClick, onWebSearchClick) {
        listOfNotNull(
            onCopyClick?.let { ActionItem(R.drawable.icon_copy, "Copy", it) },
            onWebSearchClick?.let { ActionItem(R.drawable.ic_search, "Search", it) },
            onShareClick?.let { ActionItem(R.drawable.ic_share_dark, "Share", it) },
            onSaveClick?.let { ActionItem(R.drawable.ic_save, "Save", it) },
        )
    }

    FlowRow(
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.Center,
        maxItemsInEachRow = 4, // tùy chỉnh nếu muốn
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
    ) {
        buttons.forEachIndexed { index, item ->
            ActionButton(
                drawableRes = item.icon,
                text = item.text,
                onClick = item.onClick,
                modifier = Modifier
                    .padding(
                        end = if ((index + 1) % 4 != 0 && index != buttons.lastIndex) 24.dp else 0.dp,
                        bottom = 16.dp
                    )
            )
        }
    }
}

private data class ActionItem(
    @DrawableRes val icon: Int,
    val text: String,
    val onClick: () -> Unit
)

@Composable
private fun ActionButton(
    @DrawableRes drawableRes: Int,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable { onClick() }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(64.dp)
                .background(
                    color = Color(0xFFFFB800),
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            Icon(
                painter = painterResource(id = drawableRes),
                contentDescription = text,
                tint = Color.Black,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.labelLarge
        )
    }
}