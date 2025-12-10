package com.qrmaster.utils

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException

suspend fun uploadTempImage(file: File): Unit = withContext(Dispatchers.IO) {
    val client = OkHttpClient()

    val requestBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("model", "gpt-4o")
        .addFormDataPart("messages[0][role]", "user")
        .addFormDataPart("messages[0][content][0][type]", "input_image")
        .addFormDataPart(
            "messages[0][content][0][image]",
            file.name,
            file.asRequestBody("image/png".toMediaType())
        )
        .addFormDataPart("messages[0][content][1][type]", "text")
        .addFormDataPart("messages[0][content][1][text]", "Mô tả chi tiết ảnh này.")
        .build()

    val request = Request.Builder()
        .url("http://v98store.com/v1/chat/completions")
        .header("Authorization", "Bearer sk-uDVpBlsKWTkjLeIkkMZ9vkrgFCfk3njZ1fdS8juwaOU9KSK4")
        .post(requestBody)
        .build()

    val response = client.newCall(request).execute()

    if (!response.isSuccessful) {
        Log.e("UploadError", "Upload failed: ${response.code} ${response.message}")
        throw IOException("Upload failed: ${response.code} ${response.message}")
    }
}