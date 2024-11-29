package com.example.tasklist.providers

import com.example.tasklist.models.ListaIconos
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface InterfazIconos {
    @GET("v1/")

    suspend fun getIconos(@Query("search?term=") busqueda: String): Response<ListaIconos>

}