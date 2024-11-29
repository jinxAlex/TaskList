package com.example.tasklist

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.tasklist.adapters.IconosAdapter
import com.example.tasklist.databinding.ActivityAddCategoriaBinding
import com.example.tasklist.models.Icono
import com.example.tasklist.providers.ApiIcono
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddCategoria : AppCompatActivity() {

    private lateinit var binding: ActivityAddCategoriaBinding

    private lateinit var listaIconos : MutableList<Icono>

    private val adapter = IconosAdapter(listaIconos)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAddCategoriaBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setRecycler()
        pintarIconos()
    }

    private fun setRecycler() {
        val layout = GridLayoutManager(this,6)
        binding.recycler.layoutManager = layout

        binding.recycler.adapter = adapter
    }

    private fun pintarIconos() {
        lifecycleScope.launch(Dispatchers.IO){
            val iconos = ApiIcono.api.getIconos("clock")
            val lista = iconos.body()?.listaIconos ?: emptyList<Icono>().toMutableList()
            withContext(Dispatchers.Main){
                adapter.lista = lista
                adapter.notifyDataSetChanged()
            }
        }
    }
}