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

    private val PHONE_NUMBER = "0904556455"
    private val PASSWORD = "!23123qweR"

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
        val name = response.data?.basicInfo?.name?.takeIf { it.isNotBlank() }
            ?: "Không tìm thấy tên sản phẩm"
        val origin = response.data?.basicInfo?.country?.name ?: "Không rõ nguồn gốc xuất xứ"
        Result.success(ICheckProductInfo(name = name, origin = origin))
    } catch (e: Exception) {
        token = null
        try {
            val authToken = loginAndGetToken()
            val response =
                apiService.scanBarcode("Bearer $authToken", ScanRequest(barcode = barcode))
            val name = response.data?.basicInfo?.name?.takeIf { it.isNotBlank() }
                ?: "Không tìm thấy tên sản phẩm"
            val origin = response.data?.basicInfo?.country?.name ?: "Không rõ xuất xứ"
            Result.success(ICheckProductInfo(name, origin))
        } catch (e2: Exception) {
            Result.failure(e2)
        }
    }
}