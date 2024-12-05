package com.example.tasklist.adapters

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.tasklist.databinding.IconoLayoutBinding
import com.squareup.picasso.Picasso

class CategoriasViewHolder(v: View): RecyclerView.ViewHolder(v) {
    val binding = IconoLayoutBinding.bind(v)

    fun render(urlImagen: String){
        Picasso.get().load(urlImagen).resize(150,150).into(binding.iv)
        Log.d("URLIMAGEN",urlImagen)
    }
}