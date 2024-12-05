package com.example.tasklist.adapters

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.tasklist.databinding.IconoLayoutBinding
import com.example.tasklist.models.Icono
import com.squareup.picasso.Picasso

class IconosViewHolder(v: View): RecyclerView.ViewHolder(v) {
    private val binding = IconoLayoutBinding.bind(v)

    fun render(icono: Icono, obtenerIcono:(String) -> Unit,estaSeleccionado: Boolean){
        val url = "${icono.rasterSizes.lastOrNull()?.formats?.firstOrNull()?.previewUrl}?size=large"

        Picasso.get().load(url).resize(150,150).into(binding.iv)
       if (estaSeleccionado) {
            binding.iv.setBackgroundColor(Color.parseColor("#ebdbd4"))
            obtenerIcono(url)
        } else {
            binding.iv.setBackgroundColor(Color.TRANSPARENT)
        }
    }
}