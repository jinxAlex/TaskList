package com.example.tasklist.providers

import com.example.tasklist.models.ListaIconos
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface InterfazIconos {
    @GET("v4/icons/search")

    suspend fun getIconos(@Header("Authorization") authorization: String, @Query("query") busqueda: String, @Query("count") count: Int): Response<ListaIconos>
}