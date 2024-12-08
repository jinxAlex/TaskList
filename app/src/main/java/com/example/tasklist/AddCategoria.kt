package com.example.tasklist

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.SearchView
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

    private lateinit var preferences: Preferences

    private var urlIcono: String = ""

    private lateinit var binding: ActivityAddCategoriaBinding

    private var listaIconos = mutableListOf<Icono>()

    private val adapter = IconosAdapter(listaIconos) { obtenerIcono(it) }


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
        preferences = Preferences(this)
        setRecycler()
        setlisteners()
    }

    private fun setlisteners() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                pintarIconos(query.toString().lowercase().trim())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
        binding.btnAgregar.setOnClickListener {
            if(comprobarDatos()){
                val array: MutableList<String> = preferences.getArray().toMutableList()
                array.add(String.format("%s,%s",binding.etNombreCategoria.text.toString().trim(),urlIcono))
                preferences.setArray(array)
                val i = Intent(this,TareasActivity::class.java)
                startActivity(i)
            }
        }
        binding.btnCancelar.setOnClickListener {
            finish()
        }
    }

    private fun comprobarDatos(): Boolean {
        var esCorrecto = true
        if(binding.etNombreCategoria.text.isEmpty()){
            esCorrecto = false
            binding.etNombreCategoria.error = "El nombre introducido no es valido"
        }
        if(binding.etNombreCategoria.text.length  > 15 ){
            esCorrecto = false
            binding.etNombreCategoria.error = "El nombre introducido no puede ser tan largo"
        }
        if(binding.etNombreCategoria.text.contains(",") ){
            esCorrecto = false
            binding.etNombreCategoria.error = "El nombre introducido no puede contener una ,"
        }
        if(urlIcono.isEmpty()){
            esCorrecto = false
            Toast.makeText(this,"Debes elegir un icono", Toast.LENGTH_SHORT).show()
        }
        return esCorrecto
    }

    private fun obtenerIcono(url: String) {
        urlIcono = url
    }

    private fun setRecycler() {
        val layout = GridLayoutManager(this,4)
        binding.recycler.layoutManager = layout

        binding.recycler.adapter = adapter
    }

    private fun pintarIconos(busqueda: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val iconos = ApiIcono.api.getIconos(getString(R.string.icon_api_key),busqueda, 20)
                val lista = iconos.body()?.listaIconos ?: mutableListOf()
                withContext(Dispatchers.Main) {
                    if (lista.isNotEmpty()) {
                        adapter.lista = lista
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