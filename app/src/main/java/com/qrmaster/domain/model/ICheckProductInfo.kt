package com.qrmaster.domain.model

data class ICheckProductInfo(
    val id: String? = null,
    val name: String? = null,
    val origin: String? = null,
    val country: String? = null,
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val media: List<String> = emptyList(),
    val ownerName: String? = null,
    val ownerAddress: String? = null,
    val shortContent: String? = null,
    val content: String? = null
)