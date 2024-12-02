package com.example.tasklist.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.tasklist.databinding.TareasLayoutBinding
import com.example.tasklist.models.Tarea

class TareasViewHolder(v: View): RecyclerView.ViewHolder(v) {
    val binding = TareasLayoutBinding.bind(v)

    fun render(tarea: Tarea, actualizarEstado: (Int) -> Unit) {
        binding.cb.setText(tarea.nombre)
        binding.cb.setOnClickListener{
            actualizarEstado(adapterPosition)
        }
    }
}