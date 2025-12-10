package com.qrmaster.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qrmaster.R
import com.qrmaster.ui.theme.QRMasterTheme

/**
 * Settings screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {

    var vibrateState by remember { mutableStateOf(true) }
    var beepState by remember { mutableStateOf(false) }

    QRMasterTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            IconButton(
                onClick = {},
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 32.dp, top = 16.dp)
                    .size(48.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = Color(0xFFFFA500)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                Spacer(Modifier.height(80.dp))
                Text(
                    "Settings",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFFFFA500),
                    modifier = Modifier.padding(start = 16.dp)
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(32.dp)
                ) {

                    SettingItem(
                        iconRes = R.drawable.img_vibrate,
                        title = "Vibrate",
                        desc = "Vibrate when scan is done.",
                        showSwitch = true,
                        switchChecked = vibrateState,
                        onSwitchChange = {vibrateState = it}
                    )

                    SettingItem(
                        iconRes = R.drawable.img_beep,
                        title = "Beep",
                        desc = "Beep when scan is done.",
                        showSwitch = true,
                        switchChecked = beepState,
                        onSwitchChange = {beepState = it}
                    )
                    Spacer(Modifier.height(20.dp))

                    Text(
                        "Support",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFFFFA500),
                        modifier = Modifier.padding(start = 16.dp)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(32.dp)
                    ) {

                        SettingItem(
                            iconRes = R.drawable.img_rate_us,
                            title = "Rate Us",
                            desc = "Your best reward to us.",
                            showSwitch = false,
                            switchChecked = true
                        )

                        SettingItem(
                            iconRes = R.drawable.img_share,
                            title = "Share",
                            desc = "Share app with other.",
                            showSwitch = false,
                            switchChecked = false
                        )

                        SettingItem(
                            iconRes = R.drawable.img_private,
                            title = "Privacy Policy",
                            desc = "Flow our policies that benefits you.",
                            showSwitch = false,
                            switchChecked = false
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingItem(
    iconRes: Int,
    title: String,
    desc: String,
    showSwitch: Boolean = false,
    switchChecked: Boolean = false,
    onSwitchChange: ((Boolean) -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    ListItem(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                enabled = onClick != null,
                onClick = { onClick?.invoke() }
            )
            .padding(horizontal = 8.dp, vertical = 6.dp),
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        headlineContent = {
            Text(
                text = title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        },
        supportingContent = {
            Text(
                text = desc,
                color = Color(0xFFB3B3B3),
                fontSize = 14.sp
            )
        },
        leadingContent = {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = Color(0xFFFFA500),
                modifier = Modifier.size(24.dp)
            )
        },
        trailingContent = if (showSwitch) {
            {
                Switch(
                    checked = switchChecked,
                    onCheckedChange = onSwitchChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFFFFA500),
                        uncheckedThumbColor = Color(0xFF999999),
                        uncheckedTrackColor = Color(0xFF555555)
                    ),
                    modifier = Modifier.scale(0.9f)
                )
            }
        } else null
    )
}
