package com.example.tasklist.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tasklist.R
import com.example.tasklist.models.Tarea

class TareasAdapter(
    var lista: MutableList<Tarea>,
    private val actualizarEstado: (Int) -> Unit
): RecyclerView.Adapter<TareasViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TareasViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.tareas_layout,parent,false)
        return TareasViewHolder(v)
    }

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: TareasViewHolder, position: Int) {
        val tarea = lista[position]
        holder.render(tarea, actualizarEstado)
    }

}