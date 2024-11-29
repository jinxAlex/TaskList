package com.example.tasklist.models

import com.google.gson.annotations.SerializedName

data class Icono(
    @SerializedName("url") val imagen: String
)

data class ListaIconos(
    @SerializedName("icons") val listaIconos: MutableList<Icono>
)