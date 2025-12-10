package com.qrmaster.ui.generatecode

import androidx.compose.runtime.Composable
import com.qrmaster.R
import com.qrmaster.ui.components.FormGenerateCode

@Composable
fun TextGenerate(
    onNavigateToBack: () -> Unit,
    onGenerateClick: (String) -> Unit
) {
    FormGenerateCode(
        iconRes = R.drawable.icon_text,
        title = "Text",
        hint = "Enter text here",
        onNavigateToBack = onNavigateToBack,
        onGenerateClick = onGenerateClick
    )
}