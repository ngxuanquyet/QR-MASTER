package com.qrmaster.data.remote

import com.google.gson.annotations.SerializedName

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
    @SerializedName("barcodeType") val barcodeType: String = "EAN_13",
    @SerializedName("layout") val layout: String = "product-detail",
    @SerializedName("isScan") val isScan: Boolean = true
)

data class ICheckScanResponse(
    @SerializedName("statusCode") val statusCode: String? = null,
    val data: ProductData? = null
)

data class ProductData(
    val id: Long? = null,
    val basicInfo: BasicInfo? = null,
    val media: List<Media>? = emptyList(),
    val owner: Owner? = null,
    val information: List<Information>? = emptyList()
)

data class BasicInfo(
    val name: String? = null,
    val country: Country? = null,
    val rating: Double? = null,
    @SerializedName("reviewCount") val reviewCount: Int? = null
)

data class Country(
    val name: String? = null
)

data class Media(
    val content: String? = null,
    val type: String? = null
)

data class Owner(
    val name: String? = null,
    val address: String? = null
)

data class Information(
    val title: String? = null,
    @SerializedName("shortContent") val shortContent: String? = null,
    val detail: String? = null,
    val content: String? = null
)

interface ICheckApiService {
    @androidx.annotation.WorkerThread
    @retrofit2.http.POST("/login")
    suspend fun loginWithAccount(@retrofit2.http.Body request: LoginRequest): LoginResponse

    @androidx.annotation.WorkerThread
    @retrofit2.http.POST("/social/api/products/scan")
    suspend fun scanBarcode(
        @retrofit2.http.Header("Authorization") auth: String,
        @retrofit2.http.Body request: ScanRequest
    ): ICheckScanResponse
}