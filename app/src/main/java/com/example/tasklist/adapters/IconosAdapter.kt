package com.example.tasklist.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tasklist.R
import com.example.tasklist.models.Icono

class IconosAdapter(
    var lista: MutableList<Icono>,
    val obtenerIcono: (String) -> Unit
): RecyclerView.Adapter<IconosViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconosViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.icono_layout,parent,false)
        return IconosViewHolder(v)
    }

    private var iconoSeleccionado: String = ""

    override fun getItemCount(): Int = lista.size

    override fun onBindViewHolder(holder: IconosViewHolder, position: Int) {
        val icono = lista[position]
        val url = "${icono.rasterSizes.lastOrNull()?.formats?.firstOrNull()?.previewUrl}?size=large"
        holder.render(icono,obtenerIcono,url == iconoSeleccionado)
        Log.d("URL",url)
        holder.itemView.setOnClickListener {
            iconoSeleccionado = url
            Log.d("URL",iconoSeleccionado)
            notifyItemChanged(position)
        }
    }
}