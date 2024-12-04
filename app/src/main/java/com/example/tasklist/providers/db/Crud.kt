package com.example.tasklist.providers.db

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.tasklist.Aplicacion
import com.example.tasklist.models.Tarea

class Crud {
    fun create(tarea: Tarea): Long{
        val con = Aplicacion.llave.writableDatabase
        return try{
            con.insertWithOnConflict(
                Aplicacion.TABLA,null,tarea.toContentValues(),SQLiteDatabase.CONFLICT_IGNORE
            )
        }catch (e: Exception){
            e.printStackTrace()
            -1L
        }finally {
            con.close()
        }
    }

    fun borrar(tarea: Tarea): Boolean{
        val con = Aplicacion.llave.writableDatabase
        val tareaBorrada = con.delete(Aplicacion.TABLA,"id = ?", arrayOf(tarea.id.toString()))
        con.close()
        return tareaBorrada != 0
    }

    fun update(tarea: Tarea):Boolean{
        val con = Aplicacion.llave.writableDatabase
        val tareaValues = tarea.toContentValues()
        var filasAfectadas = 0
        val q = "select id from ${Aplicacion.TABLA} where id <> ? AND nombre = ?"
        val cursor = con.rawQuery(q, arrayOf(tarea.id.toString(),tarea.nombre))
        if(!cursor.moveToFirst()){
            filasAfectadas = con.update(Aplicacion.TABLA, tareaValues, "id=?", arrayOf(tarea.id.toString()))
        }
        cursor.close()
        con.close()
        return filasAfectadas != 0
    }

    fun readTareas(finalizado: Boolean): MutableList<Tarea>{
        val lista = mutableListOf<Tarea>()
        val con = Aplicacion.llave.readableDatabase
        try{
            val cursor = con.query(
                Aplicacion.TABLA,
                arrayOf("id","nombre","descripcion", "tiempo","finalizado","categoria","localizacion","pagina","prioridad"),
                "finalizado = ?",
                if(finalizado) arrayOf("1") else arrayOf("0"),
                null,
                null,
                "prioridad DESC"
            )
            while (cursor.moveToNext()){
                val tarea = Tarea(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(3),
                    cursor.getInt(4) == 1,
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getInt(8) == 1
                )
                lista.add(tarea)
            }
        }catch(e: Exception){
            e.printStackTrace()
        }finally {
            con.close()
        }
        return lista
    }

    private fun Tarea.toContentValues(): ContentValues{
        return ContentValues().apply {
            put("NOMBRE", nombre)
            put("DESCRIPCION", descripcion)
            put("TIEMPO", tiempo)
            put("FINALIZADO", if(finalizado) 1 else 0)
            put("CATEGORIA",categoria)
            put("LOCALIZACION", localizacion)
            put("PAGINA", pagina)
            put("PRIORIDAD", if(prioridad) 1 else 0)
        }
    }
}