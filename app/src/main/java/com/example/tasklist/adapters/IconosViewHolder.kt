package com.example.tasklist.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.tasklist.databinding.IconoLayoutBinding
import com.example.tasklist.models.Icono
import com.squareup.picasso.Picasso

class IconosViewHolder(v: View): RecyclerView.ViewHolder(v) {
    val binding = IconoLayoutBinding.bind(v)

    fun render(icono: Icono){
        val url = "${icono.rasterSizes.lastOrNull()?.formats?.firstOrNull()?.previewUrl}?size=large"

        Picasso.get().load(url).resize(50, 50).into(binding.iv)
    }
}