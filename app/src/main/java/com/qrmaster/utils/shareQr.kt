package com.qrmaster.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

suspend fun Context.shareQrCodeBitmap(bitmap: Bitmap, title: String = "Chia sẻ QR Code") {
    withContext(Dispatchers.IO) {
        val cachePath = File(externalCacheDir, "qr_codes")
        cachePath.mkdirs()
        val file = File(cachePath, "qr_${System.currentTimeMillis()}.png")

        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        val uri: Uri = FileProvider.getUriForFile(
            this@shareQrCodeBitmap,
            "${packageName}.fileprovider",  // <-- phải khai báo trong manifest
            file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, "Đây là mã QR của tôi")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(shareIntent, title)
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(chooser)
    }
}