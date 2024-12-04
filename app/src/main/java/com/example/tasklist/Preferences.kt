package com.example.tasklist

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.example.tasklist.models.Categoria

class Preferences(c: Context) {

    val storage = c.getSharedPreferences("DATOS", MODE_PRIVATE)

    fun setArray(categorias: MutableList<String>){
        storage.edit().putStringSet("CATEGORIAS",categorias.toSet()).apply()
    }

    fun getArray(): Set<String>{
        return storage.getStringSet("CATEGORIAS",  emptySet()) ?: emptySet()
    }

    fun borrarTodo(){
        storage.edit().clear().apply()
    }
}