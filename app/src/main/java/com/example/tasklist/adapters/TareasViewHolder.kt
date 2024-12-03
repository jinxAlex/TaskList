package com.example.tasklist.adapters

import android.graphics.Paint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.tasklist.databinding.TareasLayoutBinding
import com.example.tasklist.models.Tarea


class TareasViewHolder(v: View): RecyclerView.ViewHolder(v) {
    val binding = TareasLayoutBinding.bind(v)

    fun render(tarea: Tarea, actualizarEstado: (Tarea) -> Unit) {
        binding.cb.text = tarea.nombre
        if(tarea.finalizado){
            binding.cb.isChecked = true
            binding.cb.paintFlags  = Paint.STRIKE_THRU_TEXT_FLAG
        }
        binding.cb.setOnClickListener{
            actualizarEstado(tarea)
        }
    }
}