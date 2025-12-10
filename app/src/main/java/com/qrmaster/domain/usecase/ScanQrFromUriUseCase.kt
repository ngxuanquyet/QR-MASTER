// ScanQrFromUriUseCase.kt
package com.qrmaster.domain.usecase

import android.app.Application
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume

class ScanQrFromUriUseCase @Inject constructor(
    private val application: Application
) {
    suspend operator fun invoke(uri: Uri): String? = withContext(Dispatchers.IO) {
        application.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream)?.let { bitmap ->
                val image = InputImage.fromBitmap(bitmap, 0)
                suspendCancellableCoroutine<String?> { cont ->
                    BarcodeScanning.getClient().process(image)
                        .addOnSuccessListener { cont.resume(it.firstOrNull()?.rawValue) }
                        .addOnFailureListener { cont.resume(null) }
                }
            }
        }
    }
}