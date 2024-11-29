package com.example.tasklist.providers

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiIcono {
    private  val retrofit2 = Retrofit.Builder()
        .baseUrl("https://api.thenounproject.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()


}