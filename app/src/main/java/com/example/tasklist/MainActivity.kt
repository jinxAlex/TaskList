package com.example.tasklist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tasklist.databinding.ActivityMainBinding
import com.example.tasklist.fragment.FragmentCategorias
import com.example.tasklist.adapters.TareasAdapter
import com.example.tasklist.models.Tarea
import com.example.tasklist.providers.db.Crud

class MainActivity : AppCompatActivity() {

    private  lateinit var binding: ActivityMainBinding

    private var listaTareasPorHacer = Crud().readTareas(false)

    private var listaTareasHechas = Crud().readTareas(true)

    private val adapterTareasPorHacer = TareasAdapter(listaTareasPorHacer) { actualizarEstado(it) }

    private val adapterTareasHechas = TareasAdapter(listaTareasHechas) { actualizarEstado(it) }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        cargarFragment()
        setListeners()
        setRecyclers()
    }

    private fun actualizarTablas() {
        listaTareasPorHacer = Crud().readTareas(false)
        listaTareasHechas = Crud().readTareas(true)
    }

    private fun actualizarEstado(position: Int) {
        if (position >= 0 && position < listaTareasHechas.size) {
            val tarea = listaTareasHechas.get(position)
            Crud().update(tarea)
            listaTareasHechas.removeAt(position)
            adapterTareasHechas.notifyItemRemoved(position)

            listaTareasPorHacer.add(tarea)
            adapterTareasPorHacer.notifyItemInserted(listaTareasPorHacer.size)
            actualizarTablas()
        }

        Log.e("MainActivity", "Índice fuera de rango: $position")
    }

    private fun setRecyclers() {
        val layout1 = LinearLayoutManager(this)
        val layout2 = LinearLayoutManager(this)
        actualizarTablas()
        binding.recyclerTareasPorHacer.adapter = TareasAdapter(listaTareasPorHacer) {
            actualizarEstado(it)
        }
        binding.recyclerTareasPorHacer.layoutManager = layout1

        binding.recyclerTareasHechas.adapter = TareasAdapter(listaTareasHechas) {
            actualizarEstado(it)
        }
        binding.recyclerTareasHechas.layoutManager = layout2
    }

    override fun onRestart() {
        super.onRestart()
        setRecyclers()
    }

    private fun setListeners() {
        binding.btnAdd.setOnClickListener {
            val i = Intent(this, AddTarea::class.java)
            startActivity(i)
        }
    }

    private fun cargarFragment() {
        val array : ArrayList<String> = arrayListOf("Categoria1")
        val fg = FragmentCategorias()
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.fg_categoria,fg)
        }
    }
}