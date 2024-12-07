package com.example.tasklist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tasklist.adapters.CategoriasAdapter
import com.example.tasklist.adapters.TareasAdapter
import com.example.tasklist.databinding.ActivityTareasBinding
import com.example.tasklist.models.Tarea
import com.example.tasklist.providers.db.Crud
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class TareasActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private var categoriaActual = 0

    private var nombreCategoria = ""

    private  lateinit var binding: ActivityTareasBinding

    private lateinit var preferences: Preferences

    private lateinit var categorias: MutableList<String>

    private var listaTareasPorHacer = Crud().readTareas(false,nombreCategoria)

    private var listaTareasHechas = Crud().readTareas(true,nombreCategoria)

    private val adapterTareasPorHacer = TareasAdapter(listaTareasPorHacer,{ actualizarEstado(it) }, {borrarTarea(it)}, {actualizarTarea(it)})

    private val adapterTareasHechas = TareasAdapter(listaTareasHechas, { actualizarEstado(it) }, { borrarTarea(it) },{actualizarTarea(it)})

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityTareasBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = Firebase.auth
        preferences = Preferences(this)

        binding.tvEmailIniciadoSesion.setText(auth.currentUser?.email)
        categorias = preferences.getArray().toMutableList()
        setListeners()
        setRecyclers()
        cambiarCategoria(categoriaActual)
    }

    private fun actualizarTablas() {
        listaTareasPorHacer.clear()
        listaTareasPorHacer.addAll(Crud().readTareas(false,nombreCategoria))
        adapterTareasPorHacer.notifyDataSetChanged()

        listaTareasHechas.clear()
        listaTareasHechas.addAll(Crud().readTareas(true,nombreCategoria))
        adapterTareasHechas.notifyDataSetChanged()
    }

    private fun actualizarEstado(tarea: Tarea) {
        when(tarea.finalizado ){
            true -> {
                tarea.finalizado = false
                Toast.makeText(this,String.format("La tarea %s se ha movido a la lista de no terminadas.", tarea.nombre),Toast.LENGTH_SHORT).show()
            }
            false -> {
                tarea.finalizado = true
                Toast.makeText(this,String.format("Se ha completado la tarea %s.", tarea.nombre),Toast.LENGTH_SHORT).show()
            }
        }
        if (Crud().update(tarea)) {
            setRecyclers()
        }
    }

    private fun borrarTarea(tarea: Tarea) {
        if(Crud().borrar(tarea)){
            setRecyclers()
        }
    }

    private fun cambiarCategoria(posicion: Int) {
        categoriaActual = posicion

        if(categorias.isEmpty()){
            binding.tvCategoria.setText("Aún no hay categorías")
        }else{
            nombreCategoria = descomponerCategorias(0)[posicion]
            binding.tvCategoria.setText(nombreCategoria)
        }
        setRecyclers()
    }

    private fun borrarCategoria(posicion: Int) {
        categorias.removeAt(posicion)
        preferences.setArray(categorias)
        setRecyclers()
    }

    private fun actualizarTarea(tarea: Tarea) {
        val bundle = Bundle().apply {
            val nombreCategorias = descomponerCategorias(0)
            putStringArrayList("CATEGORIAS",ArrayList(nombreCategorias))
            putBoolean("EDITABLE",true)
            putSerializable("TAREA",tarea)
        }
        val i = Intent(this,AddTarea::class.java)
        i.putExtras(bundle)
        startActivity(i)
    }


    private fun setRecyclers() {
        val layout1 = LinearLayoutManager(this)
        val layout2 = LinearLayoutManager(this)
        val layout3 = LinearLayoutManager(this)
        layout3.orientation = RecyclerView.HORIZONTAL
        actualizarTablas()
        binding.recyclerTareasPorHacer.adapter = TareasAdapter(listaTareasPorHacer,{actualizarEstado(it)}, {borrarTarea(it)}, {actualizarTarea(it)})
        binding.recyclerTareasPorHacer.layoutManager = layout1

        binding.recyclerTareasHechas.adapter = TareasAdapter(listaTareasHechas,{actualizarEstado(it)}, {borrarTarea(it)}, {actualizarTarea(it)})
        binding.recyclerTareasHechas.layoutManager = layout2

        val iconosCategorias = descomponerCategorias(1)
        val adapterCategorias = CategoriasAdapter(iconosCategorias,{cambiarCategoria(it)})
        binding.recyclerCategorias.adapter = adapterCategorias
        binding.recyclerCategorias.layoutManager = layout3
        adapterCategorias.notifyDataSetChanged()
    }

    override fun onRestart() {
        super.onRestart()
        setRecyclers()
    }

    private fun setListeners() {
        binding.btnAdd.setOnClickListener {
            val nombreCategorias = descomponerCategorias(0)
            if(nombreCategorias.isNotEmpty()){
                val i = Intent(this, AddTarea::class.java)
                val bundle = Bundle().apply {
                    putStringArrayList("CATEGORIAS",ArrayList(nombreCategorias))
                }
                i.putExtras(bundle)
                startActivity(i)
            }else{
                Toast.makeText(this,"Debes de añadir una categoría primero",Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnCerrarSesion.setOnClickListener{
            auth.signOut()
            finish()
        }
        binding.btnAddCategoria.setOnClickListener {
            val i = Intent(this, AddCategoria::class.java)
            startActivity(i)
        }
        binding.btnBorrarCategoria.setOnClickListener {
            borrarCategoria(categoriaActual)
            setRecyclers()
        }
    }

    private fun descomponerCategorias(posicion: Int): MutableList<String> {
        var nombresCategorias: MutableList<String> = mutableListOf()
        for(categoriaLista in categorias){
            val categoria = categoriaLista.trim().split(" ")
            nombresCategorias.add(categoria[posicion]) //0 -> nombre, 1-> urlImagen
        }
        return nombresCategorias
    }
}