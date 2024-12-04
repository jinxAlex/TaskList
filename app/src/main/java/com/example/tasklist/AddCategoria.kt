package com.example.tasklist

import android.os.Bundle
import android.util.Log
import android.widget.Toast
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

    private var listaIconos = mutableListOf<Icono>()

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
        val layout = GridLayoutManager(this,4)
        binding.recycler.layoutManager = layout

        binding.recycler.adapter = adapter
    }

    private fun pintarIconos() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val iconos = ApiIcono.api.getIconos(getString(R.string.icon_api_key),"book", 10 )

                val lista = iconos.body()?.listaIconos ?: mutableListOf()

                withContext(Dispatchers.Main) {
                    if (lista.isNotEmpty()) {
                        adapter.lista = lista
                        Log.d("LISTA",lista.toString())
                        adapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this@AddCategoria, "No se encontraron iconos", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddCategoria, "Error al cargar iconos: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}