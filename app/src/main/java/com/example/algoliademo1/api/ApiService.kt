package com.example.algoliademo1.api

import com.example.algoliademo1.model.PincodeModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path


private const val BASE_URL = "https://api.postalpincode.in"

val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()

interface ApiService {
    @GET("/pincode/{pin}")
    suspend fun getPincodeDetails(@Path("pin") pin: String): List<PincodeModel>// add model here
}

object PostalPincodeApi {
    val service: ApiService by lazy { retrofit.create(ApiService::class.java) }
}