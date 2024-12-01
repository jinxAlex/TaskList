package com.example.tasklist

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tasklist.databinding.ActivityMainBinding
import com.example.tasklist.fragment.FragmentCategorias
import com.example.tasklist.adapters.TareasAdapter
import com.example.tasklist.providers.db.Crud

class MainActivity : AppCompatActivity() {

    private  lateinit var binding: ActivityMainBinding

    private var listaTareasPorHacer = Crud().readTareas(false)

    private var listaTareasHechas = Crud().readTareas(true)

    private val adapterTareasPorHacer = TareasAdapter(listaTareasPorHacer)

    private val adapterTareasHechas = TareasAdapter(listaTareasHechas)


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
        val i = Intent(this,AddCategoria::class.java)

        //startActivity(i)

    }

    private fun actualizarTablas() {
        listaTareasPorHacer = Crud().readTareas(false)
        listaTareasHechas = Crud().readTareas(true)
    }

    private fun setRecyclers() {
        val layout1 = LinearLayoutManager(this)
        val layout2 = LinearLayoutManager(this)
        actualizarTablas()
        binding.recyclerTareasPorHacer.adapter = TareasAdapter(listaTareasPorHacer)
        binding.recyclerTareasPorHacer.layoutManager = layout1

        binding.recyclerTareasHechas.adapter = TareasAdapter(listaTareasHechas)
        binding.recyclerTareasHechas.layoutManager = layout2
    }

    override fun onRestart() {
        super.onRestart()
        actualizarTablas()
        adapterTareasPorHacer.notifyDataSetChanged()
        adapterTareasHechas.notifyDataSetChanged()
    }

    private fun setListeners() {
        binding.btnAdd.setOnClickListener {
            val i = Intent(this, AddTarea::class.java)
            startActivity(i)
        }
    }

    private fun cargarFragment() {
        val array : ArrayList<String> = arrayListOf("Jinx", "Powder")
        val fg = FragmentCategorias()
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.fg_categoria,fg)
        }
    }
}