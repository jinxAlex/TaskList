package com.example.tasklist

import android.app.Application
import android.content.Context
import com.example.tasklist.providers.db.Database

class Aplicacion: Application() {
    companion object{
        const val version = 1
        const val DB = "BD_Task"
        const val TABLA = "tareas"
        lateinit var appContext: Context
        lateinit var llave: Database
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        llave = Database()
    }
}