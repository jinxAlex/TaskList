package com.example.tasklist.models

import com.google.gson.annotations.SerializedName

// icons --> raster_sizes --> formats --> preview_url

data class Icono(
    @SerializedName("raster_sizes") val rasterSizes: List<RasterSize>
)

data class RasterSize(
    @SerializedName("formats") val formats: List<Format>
)

data class ListaIconos(
    @SerializedName("icons") val listaIconos: MutableList<Icono>
)

data class Format(
    @SerializedName("preview_url") val previewUrl: String
)