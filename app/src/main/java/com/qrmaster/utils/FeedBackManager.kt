// utils/FeedbackManager.kt – PHIÊN BẢN HOÀN HẢO (dùng âm thanh hệ thống)

package com.qrmaster.utils

import android.content.Context
import android.media.ToneGenerator
import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedbackManager @Inject constructor(
    private val context: Context
) {
    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80)

    fun playScanSuccess(vibrate: Boolean = true, beep: Boolean = true) {
        if (vibrate) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(150)
            }
        }

        if (beep) {
            toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
            // Hoặc dùng các âm khác:
            // TONE_PROP_ACK        → tiếng "ding" nhẹ
            // TONE_PROP_BEEP       → tiếng beep ngắn (chuẩn nhất)
            // TONE_CDMA_ALERT_CALL_GUARD → tiếng beep mạnh hơn
        }
    }

    fun release() {
        toneGenerator.release()
    }
}