package com.example.tasklist.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tasklist.R

class CategoriasAdapter(
    private val lista: MutableList<String>,
    private val obtenerCategoria: (Int) -> Unit
): RecyclerView.Adapter<CategoriasViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriasViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.icono_layout,parent,false)
        return CategoriasViewHolder(v)
    }

    override fun getItemCount()  = lista.size

    override fun onBindViewHolder(holder: CategoriasViewHolder, position: Int) {
        val urlImagen = lista[position]

        holder.render(urlImagen)
        holder.itemView.setOnClickListener {
            obtenerCategoria(position)
        }
    }
}