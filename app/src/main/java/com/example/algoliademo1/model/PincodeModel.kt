package com.example.algoliademo1.model

import com.squareup.moshi.Json
import java.io.Serializable

data class PincodeModel(
    @Json(name = "Message")
    val message: String?,
    @Json(name = "Status")
    val status: String?,
    @Json(name = "PostOffice")
    val postOffice: List<PincodeInfo>?

) : Serializable

data class PincodeInfo(
    @Json(name = "Name")
    val name: String?,
    @Json(name = "Description")
    val description: String?,
    @Json(name = "BranchType")
    val branchType: String?,
    @Json(name = "DeliveryStatus")
    val deliveryStatus: String?,
    @Json(name = "Circle")
    val circle: String?,
    @Json(name = "District")
    val district: String?,
    @Json(name = "Division")
    val division: String?,
    @Json(name = "Region")
    val region: String?,
    @Json(name = "Block")
    val block: String?,
    @Json(name = "State")
    val state: String?,
    @Json(name = "Country")
    val country: String?,
    @Json(name = "Pincode")
    val pincode: String?

) : Serializable
