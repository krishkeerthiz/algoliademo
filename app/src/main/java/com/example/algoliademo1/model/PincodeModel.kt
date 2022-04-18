package com.example.algoliademo1.model

import com.squareup.moshi.Json
import java.io.Serializable

data class Pincode(
    var pincode: List<PincodeModel>

) : Serializable

data class PincodeModel(
    @Json(name = "Message")
    var message: String?,
    @Json(name = "Status")
    val status: String?,
    @Json(name = "PostOffice")
    val postOffice: List<PincodeInfo>?

) : Serializable

data class PincodeInfo(
    @Json(name = "Name")
    var name: String?,
    @Json(name = "Description")
    var description: String?,
    @Json(name = "BranchType")
    var branchType: String?,
    @Json(name = "DeliveryStatus")
    var deliveryStatus: String?,
    @Json(name = "Circle")
    var circle: String?,
    @Json(name = "District")
    var district: String?,
    @Json(name = "Division")
    var division: String?,
    @Json(name = "Region")
    var region: String?,
    @Json(name = "Block")
    var block: String?,
    @Json(name = "State")
    var state: String?,
    @Json(name = "Country")
    var country: String?,
    @Json(name = "Pincode")
    var pincode: String?

) : Serializable
