package com.qrmaster.domain.repository

import android.graphics.Bitmap

interface VisionRepository {
    suspend fun analyzeImage(bitmap: Bitmap, qrContent: String): Result<String>
}