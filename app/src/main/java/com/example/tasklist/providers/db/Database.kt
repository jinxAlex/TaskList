package com.example.tasklist.providers.db

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.tasklist.Aplicacion

class Database: SQLiteOpenHelper(Aplicacion.appContext, Aplicacion.DB, null, Aplicacion.version) {

    private val tabla = "CREATE TABLE ${Aplicacion.TABLA}( ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "NOMBRE TEXT NOT NULL," +
            "DESCRIPCION TEXT NOT NULL," +
            "TIEMPO INT NOT NULL," +
            "FINALIZADO INT NOT NULL," +
            "CATEGORIA TEXT NOT NULL," +
            "LOCALIZACION TEXT," +
            "PAGINA TEXT," +
            "PRIORIDAD INT NOT NULL)"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(tabla)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if(newVersion > oldVersion){
            db?.execSQL("drop table ${Aplicacion.TABLA}")
            onCreate(db)
        }
    }
}