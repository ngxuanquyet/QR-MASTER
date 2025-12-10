package com.qrmaster.data.remote

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class LoginRequest(
    val userName: String,
    val password: String
)

data class LoginResponse(
    val data: TokenData?
)

data class TokenData(
    val token: String?
)

data class ScanRequest(
    val barcode: String,
    val barcodeType: String = "EAN_13",
    val layout: String = "product-detail",
    val isScan: Boolean = true
)

data class ICheckScanResponse(
    val data: ProductData?
)

data class ProductData(
    val basicInfo: BasicInfo?
)

data class BasicInfo(
    val name: String?,
    val country: Country?
)

data class Country(
    val name: String?
)

interface ICheckApiService {

    @POST("/login")
    suspend fun loginWithAccount(@Body request: LoginRequest): LoginResponse

    @POST("/social/api/products/scan")
    suspend fun scanBarcode(
        @Header("Authorization") auth: String,
        @Body request: ScanRequest
    ): ICheckScanResponse
}