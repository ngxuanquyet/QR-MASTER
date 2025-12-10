package com.qrmaster.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Lưu Bitmap vào thư viện ảnh (Pictures/QRMaster)
 * Trả về Uri của ảnh đã lưu hoặc null nếu thất bại
 */
suspend fun Context.saveQrToGallery(
    bitmap: Bitmap,
    fileName: String = "QR_${System.currentTimeMillis()}.png",
    displayName: String = fileName,
    mimeType: String = "image/png",
    compressionQuality: Int = 100
): Boolean = withContext(Dispatchers.IO) {
    val resolver = applicationContext.contentResolver

    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
        put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
        put(
            MediaStore.MediaColumns.RELATIVE_PATH,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                "${Environment.DIRECTORY_PICTURES}/QRMaster"
            } else {
                Environment.DIRECTORY_PICTURES
            }
        )
    }

    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        ?: return@withContext false

    try {
        resolver.openOutputStream(uri)?.use { outputStream ->
            bitmap.compress(
                if (mimeType == "image/jpeg") Bitmap.CompressFormat.JPEG else Bitmap.CompressFormat.PNG,
                compressionQuality,
                outputStream
            )
            outputStream.flush()
        } ?: return@withContext false

        // Thông báo gallery cập nhật (Android < 10 cần)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DATA, uri.path)
            }
            resolver.update(uri, values, null, null)
        }

        return@withContext true
    } catch (e: Exception) {
        resolver.delete(uri, null, null) // xóa file lỗi
        return@withContext false
    }
}