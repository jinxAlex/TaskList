package com.example.tasklist.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import com.example.tasklist.R
import com.squareup.picasso.Picasso


class FragmentCategorias(
    private val arrayCategorias: MutableList<String>
) : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_categorias, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val contexto = requireContext()

        val layoutManager = view.findViewById<LinearLayout>(R.id.linearLayout)

        super.onViewCreated(view, savedInstanceState)
        for(categoria in arrayCategorias){
            val url = categoria.trim().split(" ")

            val boton = ImageButton(contexto)

            Picasso.get().load(url[1]).resize(100,100).into(boton)
            layoutManager.addView(boton)
        }
    }

}