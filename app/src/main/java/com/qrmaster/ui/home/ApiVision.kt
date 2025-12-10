package com.qrmaster.ui.home

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

// ApiVision.kt – ĐÃ SỬA ĐỂ CHẠY VỚI v98store.com
object ApiVision {

    private const val API_URL = "https://v98store.com/v1/chat/completions"
    private const val API_KEY = "sk-uDVpBlsKWTkjLeIkkMZ9vkrgFCfk3njZ1fdS8juwaOU9KSK4"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(180, TimeUnit.SECONDS)
        .writeTimeout(180, TimeUnit.SECONDS)
        .build()

    suspend fun analyzeImageWithQrContext(bitmap: Bitmap, qrContent: String): String =
        suspendCancellableCoroutine { cont ->
            Log.d("api", "analyzeImageWithQrContext")

            // Chuyển bitmap → base64
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos)
            val base64Image = Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP)
            Log.d("api", "base64Image: $base64Image")

            val jsonPayload = JSONObject().apply {
                put("model", "gpt-4o")
                put("max_tokens", 1500)
                put("temperature", 0.7)
                put("stream", false)
                put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", JSONArray().apply {
                            put(JSONObject().apply {
                                put("type", "text")
                                put(
                                    "text",
                                    """
    Trong ảnh có một mã QR/Barcode với nội dung sau: "$qrContent"

    Hãy phân tích và trả lời BẰNG TIẾNG VIỆT theo đúng trường hợp sau:

    1. Nếu "$qrContent" là link (bắt đầu bằng http:// hoặc https://):
       → Truy cập link đó và mô tả chi tiết nội dung trang web (hoặc thông tin được hiển thị).

    2. Nếu "$qrContent" là dãy số 8–13 chữ số (EAN-8, EAN-13, UPC-A, ISBN, v.v.):
       → Đây là mã vạch sản phẩm. Hãy tra cứu và liệt kê đầy đủ thông tin sản phẩm có thể tìm được, bao gồm:
          • Tên sản phẩm
          • Thương hiệu / Nhà sản xuất
          • Dung tích / Trọng lượng / Kích thước
          • Nơi sản xuất / Quốc gia xuất xứ
          • Ngày sản xuất / Hạn sử dụng (nếu có)
          • Giá tham khảo (nếu có)
          • Mô tả ngắn gọn về sản phẩm
       Nếu không tìm được thông tin chính xác, hãy nói rõ và đưa ra thông tin gần nhất có thể.

    3. Nếu là các loại mã khác (Wi-Fi, danh thiếp vCard, SMS, email, địa chỉ, v.v.):
       → Giải thích rõ nội dung và ý nghĩa của mã đó.

    4. Đồng thời, hãy đọc và mô tả toàn bộ nội dung hiển thị trong ảnh (hóa đơn, biển báo, bảng giá, văn bản, logo thương hiệu, bao bì sản phẩm, v.v.) một cách chi tiết và có tổ chức.

    Trả lời rõ ràng, dễ hiểu, dùng gạch đầu dòng khi liệt kê thông tin.
    """.trimIndent()
                                )
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

            val requestBody = jsonPayload.toString().toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(API_URL)
                .header("Authorization", "Bearer $API_KEY")
                .header("Content-Type", "application/json")
                .post(requestBody)
                .build()

            val call = client.newCall(request)
            cont.invokeOnCancellation { call.cancel() }

            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    if (cont.isActive) cont.resume("Lỗi mạng: ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        val body = it.body.string()
                        if (!cont.isActive) return

                        if (it.isSuccessful) {
                            try {
                                val json = JSONObject(body)
                                val content = json.getJSONArray("choices")
                                    .getJSONObject(0)
                                    .getJSONObject("message")
                                    .getString("content")
                                cont.resume(content.trim())
                            } catch (e: Exception) {
                                cont.resume("Lỗi parse JSON:\n$body")
                            }
                        } else {
                            cont.resume("Lỗi ${it.code}\n$body")
                        }
                    }
                }
            })
        }
}