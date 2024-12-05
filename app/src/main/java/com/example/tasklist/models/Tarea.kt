package com.example.tasklist.models

import java.io.Serializable

data class Tarea(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val tiempo: Int,
    var finalizado: Boolean,
    val categoria: String,
    val localizacion: String,
    val pagina: String,
    val prioridad: Boolean
): Serializable