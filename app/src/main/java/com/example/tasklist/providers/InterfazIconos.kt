package com.example.tasklist.providers

import com.example.tasklist.models.ListaIconos
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface InterfazIconos {
    @GET("v4/icons/")

    suspend fun getIconos( @Query("query") busqueda: String, @Query("count") count: Int = 10, @Query("Authorization") apiKey: String): Response<ListaIconos>

}