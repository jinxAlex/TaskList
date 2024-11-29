package com.example.tasklist.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import com.example.tasklist.R


class FragmentCategorias(
    private val arrayCategorias: ArrayList<String>
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
            val btn = Button(contexto)
            btn.setText(categoria)
            layoutManager.addView(btn)
        }
    }

}