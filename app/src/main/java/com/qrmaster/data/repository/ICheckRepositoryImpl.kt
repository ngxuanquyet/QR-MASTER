package com.qrmaster.data.repository

import com.qrmaster.data.remote.ICheckApiService
import com.qrmaster.data.remote.LoginRequest
import com.qrmaster.data.remote.ScanRequest
import com.qrmaster.domain.model.ICheckProductInfo
import com.qrmaster.domain.repository.ICheckRepository
import javax.inject.Inject

class ICheckRepositoryImpl @Inject constructor(
    private val apiService: ICheckApiService,
) : ICheckRepository {

    companion object {
        private const val PHONE_NUMBER = "0904556455"
        private const val PASSWORD = "!23123qweR"
    }

    private var token: String? = null

    private suspend fun loginAndGetToken(): String {
        if (token != null) return token!!

        val response = apiService.loginWithAccount(
            LoginRequest(PHONE_NUMBER, PASSWORD)
        )
        token = response.data?.token
            ?: throw Exception("Đăng nhập iCheck thất bại – kiểm tra lại phone/password")
        return token!!
    }

    override suspend fun scanBarcode(barcode: String): Result<ICheckProductInfo> = try {
        val authToken = loginAndGetToken()
        val response = apiService.scanBarcode("Bearer $authToken", ScanRequest(barcode = barcode))
        if (response.statusCode != "200" || response.data == null) {
            throw Exception("API response invalid: status=${response.statusCode}")
        }
        val productData = response.data
        val basicInfo = productData.basicInfo ?: throw Exception("Missing basicInfo")
        val country = basicInfo.country
        val information =
            productData.information?.firstOrNull { it.title?.contains("Thông tin sản phẩm") == true }
                ?: productData.information?.firstOrNull()
        val info = ICheckProductInfo(
            id = productData.id.toString(),
            name = basicInfo.name?.takeIf { it.isNotBlank() } ?: "Không tìm thấy tên sản phẩm",
            origin = if (country?.name.isNullOrBlank()) "Không rõ nguồn gốc xuất xứ" else country.name,
            country = country?.name ?: "Không rõ nguồn gốc xuất xứ",
            rating = basicInfo.rating ?: 0.0,
            reviewCount = basicInfo.reviewCount ?: 0,
            media = productData.media?.mapNotNull { it.content } ?: emptyList(),
            ownerName = productData.owner?.name?.takeIf { it.isNotBlank() }
                ?: "Không có thông tin nhà sản xuất",
            ownerAddress = productData.owner?.address?.takeIf { it.isNotBlank() }
                ?: "Không có thông tin địa chỉ",
            shortContent = information?.shortContent,
            content = information?.detail ?: information?.content  // Fallback
        )
        Result.success(info)
    } catch (e: Exception) {
        token = null
        Result.failure(e)
    }
}