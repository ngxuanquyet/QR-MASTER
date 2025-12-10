package com.qrmaster.utils

import android.content.Context
import android.content.Intent

object ShareUtils {
    fun shareApp(context: Context) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_TEXT,
                "Check out QR Master – the fastest QR & barcode scanner!\n" +
                        "Download here: https://play.google.com/store/apps/details?id=com.qrmaster"
            )
            putExtra(Intent.EXTRA_SUBJECT, "QR Master – QR Code Scanner & Generator")
        }

        val chooser = Intent.createChooser(shareIntent, "Share QR Master with friends")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}