package com.qrmaster.data.repository

import android.graphics.Bitmap
import android.util.Base64
import com.qrmaster.domain.repository.VisionRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Response
import okio.IOException
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.resume

class VisionRepositoryImpl @Inject constructor(
    @Named("VisionClient") private val client: OkHttpClient,
    @Named("vision_base_url") private val baseUrl: String,
    @Named("vision_api_key") private val apiKey: String
) : VisionRepository {

    override suspend fun analyzeImage(
        bitmap: Bitmap,
        qrContent: String
    ): Result<String> = suspendCancellableCoroutine { cont ->
        val base64Image = bitmap.toBase64()
        val payload = JSONObject().apply {
            put("model", "gpt-4o")
            put("max_tokens", 1000)
            put("temperature", 1)
            put("stream", true)
            put("stream_options[include_usage]", true)
            put("message", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", JSONArray().apply {
                        put(JSONObject().apply {
                            put("type", "text")
                            put("text", buildPrompt(qrContent))
                        })
                        put(JSONObject().apply {
                            put("type", "image_url")
                            put("image_url", JSONObject().apply {
                                put("url", "data:image/jpeg;base64,$base64Image")
                            })
                        })
                    })
                })
            })
        }

        val request = Request.Builder()
            .url(baseUrl)
            .header("Authorization", "Bearer $apiKey")
            .post(payload.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val call = client.newCall(request)
        cont.invokeOnCancellation { call.cancel() }  // coroutine bị hủy -> cancel request HTTP

        call.enqueue(object : okhttp3.Callback{
            override fun onFailure(call: Call, e: IOException) {
                cont.resume(Result.failure(e))
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if(it.isSuccessful){
                        try {
                            val content = JSONObject(it.body.string()).getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content")
                            cont.resume(Result.success(content.trim()))
                        } catch (e: Exception) {
                            cont.resume(Result.failure(e))
                        }
                    }else{
                        cont.resume(Result.failure(Exception("HTTP ${it.code}: ${it.message}")))
                    }
                }
            }
        })

    }

    private fun Bitmap.toBase64(): String {
        val stream = java.io.ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 90, stream)
        return Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
    }

    private fun buildPrompt(qrContent: String) = """
        Trong ảnh có một mã QR/Barcode với nội dung sau: "$qrContent"

        Hãy phân tích và trả lời BẰNG TIẾNG VIỆT theo đúng trường hợp sau:

        1. Nếu "$qrContent" là link (bắt đầu bằng http:// hoặc https://):
           → Truy cập link đó và mô tả chi tiết nội dung trang web.

        2. Nếu "$qrContent" là dãy số 8–13 chữ số:
           → Đây là mã vạch sản phẩm. Hãy tra cứu và liệt kê đầy đủ thông tin sản phẩm...

        3. Nếu là các loại mã khác (Wi-Fi, vCard, SMS, email...):
           → Giải thích rõ nội dung và ý nghĩa.

        4. Đồng thời, hãy đọc và mô tả toàn bộ nội dung hiển thị trong ảnh một cách chi tiết.

        Trả lời rõ ràng, dùng gạch đầu dòng khi cần.
    """.trimIndent()
}