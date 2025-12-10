package com.qrmaster.domain.usecase

import android.graphics.Bitmap
import com.qrmaster.domain.repository.VisionRepository
import javax.inject.Inject

class AnalyzeImageUseCase @Inject constructor(
    private val visionRepository: VisionRepository
) {
    suspend operator fun invoke(bitmap: Bitmap, qrContent: String): Result<String> =
        visionRepository.analyzeImage(bitmap, qrContent)
}