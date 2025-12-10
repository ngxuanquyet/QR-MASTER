package com.qrmaster.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.qrmaster.domain.model.QrCodeData
import com.qrmaster.domain.model.QrCodeType
import com.qrmaster.domain.model.buildContent
import java.net.URL

/**
 * Utility class for generating QR codes
 */
object QrCodeGenerator {

    fun generateQrCode(request: QrCodeData): Result<Bitmap> = runCatching {
        val content = buildContent(request)
        val hints = hashMapOf<EncodeHintType, Any>().apply {
            put(
                EncodeHintType.ERROR_CORRECTION,
                getErrorCorrectionLevel(request.errorCorrectionLevel)
            )
            put(EncodeHintType.MARGIN, request.margin)
            put(EncodeHintType.CHARACTER_SET, "UTF-8")
        }

        val bitMatrix =
            QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, request.size, request.size, hints)
        val bitmap = createStyledBitmap(bitMatrix, request)

        request.logoPath?.let { overlayLogo(bitmap, it, request.size) } ?: bitmap
    }

    private fun overlayLogo(bitmap: Bitmap, logoPath: String, size: Int): Bitmap {
        val logoBitmap = when {
            logoPath.startsWith("http") -> {
                URL(logoPath).openStream().use { BitmapFactory.decodeStream(it) }
            }

            else -> BitmapFactory.decodeFile(logoPath)
        } ?: return bitmap

        val result = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(result)

        // Logo size = 20% của QR
        val logoSize = (size * 0.2f).toInt()
        val scaledLogo = Bitmap.createScaledBitmap(logoBitmap, logoSize, logoSize, true)

        // Viền trắng quanh logo
        val whiteBorder = 8
        val borderPaint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
        val borderRect = Rect(
            size / 2 - logoSize / 2 - whiteBorder,
            size / 2 - logoSize / 2 - whiteBorder,
            size / 2 + logoSize / 2 + whiteBorder,
            size / 2 + logoSize / 2 + whiteBorder
        )
        canvas.drawRoundRect(RectF(borderRect), 20f, 20f, borderPaint)

        // Vẽ logo
        canvas.drawBitmap(
            scaledLogo,
            (size - logoSize) / 2f,
            (size - logoSize) / 2f,
            null
        )

        return result
    }

    private fun createStyledBitmap(
        matrix: com.google.zxing.common.BitMatrix,
        request: QrCodeData
    ): Bitmap {
        val width = matrix.width
        val height = matrix.height
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        val paintFg =
            Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor(request.colorForeground) }
        val paintBg = Paint().apply { color = Color.parseColor(request.colorBackground) }

        canvas.drawPaint(paintBg)

        val cellSize = request.size.toFloat() / width

        when (request.patternStyle.lowercase()) {
            "dot" -> drawDotPattern(canvas, matrix, paintFg, cellSize)
            "rounded" -> drawRoundedPattern(canvas, matrix, paintFg, cellSize)
            else -> drawSquarePattern(canvas, matrix, paintFg, cellSize) // square (default)
        }

        return bmp
    }

    private fun drawSquarePattern(
        canvas: Canvas,
        matrix: com.google.zxing.common.BitMatrix,
        paint: Paint,
        cellSize: Float
    ) {
        for (x in 0 until matrix.width) {
            for (y in 0 until matrix.height) {
                if (matrix[x, y]) {
                    canvas.drawRect(
                        x * cellSize,
                        y * cellSize,
                        (x + 1) * cellSize,
                        (y + 1) * cellSize,
                        paint
                    )
                }
            }
        }
    }

    private fun drawDotPattern(
        canvas: Canvas,
        matrix: com.google.zxing.common.BitMatrix,
        paint: Paint,
        cellSize: Float
    ) {
        val radius = cellSize * 0.4f
        val cxOffset = cellSize / 2f
        val cyOffset = cellSize / 2f

        for (x in 0 until matrix.width) {
            for (y in 0 until matrix.height) {
                if (matrix[x, y]) {
                    canvas.drawCircle(
                        x * cellSize + cxOffset,
                        y * cellSize + cyOffset,
                        radius,
                        paint
                    )
                }
            }
        }
    }

    private fun drawRoundedPattern(
        canvas: Canvas,
        matrix: com.google.zxing.common.BitMatrix,
        paint: Paint,
        cellSize: Float
    ) {
        val cornerRadius = cellSize * 0.25f
        val rect = RectF()

        for (x in 0 until matrix.width) {
            for (y in 0 until matrix.height) {
                if (matrix[x, y]) {
                    rect.set(
                        x * cellSize, y * cellSize,
                        (x + 1) * cellSize, (y + 1) * cellSize
                    )
                    canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
                }
            }
        }
    }

    private fun getErrorCorrectionLevel(level: String): ErrorCorrectionLevel {
        return when (level.uppercase()) {
            "L" -> ErrorCorrectionLevel.L
            "M" -> ErrorCorrectionLevel.M
            "Q" -> ErrorCorrectionLevel.Q
            "H" -> ErrorCorrectionLevel.H
            else -> ErrorCorrectionLevel.M
        }
    }
}

