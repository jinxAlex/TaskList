package com.example.tasklist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tasklist.adapters.IconosAdapter
import com.example.tasklist.fragment.FragmentCategorias
import com.example.tasklist.adapters.TareasAdapter
import com.example.tasklist.databinding.ActivityTareasBinding
import com.example.tasklist.models.Tarea
import com.example.tasklist.providers.db.Crud
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class TareasActivity : AppCompatActivity() {

    private val responseLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        when(it.resultCode){
            RESULT_OK -> {

            }
            RESULT_CANCELED ->{

            }
        }
    }

    private lateinit var auth: FirebaseAuth

    private  lateinit var binding: ActivityTareasBinding

    private lateinit var preferences: Preferences

    private lateinit var categorias: MutableList<String>

    private var listaTareasPorHacer = Crud().readTareas(false)

    private var listaTareasHechas = Crud().readTareas(true)

    private lateinit var adapterCategorias: IconosAdapter

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
    }

    private fun actualizarTablas() {
        listaTareasPorHacer.clear()
        listaTareasPorHacer.addAll(Crud().readTareas(false))
        adapterTareasPorHacer.notifyDataSetChanged()

        listaTareasHechas.clear()
        listaTareasHechas.addAll(Crud().readTareas(true))
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
            onRestart()
        }
    }

    private fun borrarTarea(tarea: Tarea) {
        if(Crud().borrar(tarea)){
            onRestart()
        }
    }

    private fun actualizarTarea(tarea: Tarea) {
        val bundle = Bundle().apply {
            putBoolean("EDITABLE",true)
            putSerializable("TAREA",tarea)
        }
        val i = Intent(this,AddTarea::class.java)
        i.putExtras(bundle)
        responseLauncher.launch(i)
    }


    private fun setRecyclers() {
        val layout1 = LinearLayoutManager(this)
        val layout2 = LinearLayoutManager(this)
        val layout3 = LinearLayoutManager(this)
        actualizarTablas()
        binding.recyclerTareasPorHacer.adapter = TareasAdapter(listaTareasPorHacer,{actualizarEstado(it)}, {borrarTarea(it)}, {actualizarTarea(it)})
        binding.recyclerTareasPorHacer.layoutManager = layout1

        binding.recyclerTareasHechas.adapter = TareasAdapter(listaTareasHechas,{actualizarEstado(it)}, {borrarTarea(it)}, {actualizarTarea(it)})
        binding.recyclerTareasHechas.layoutManager = layout2

        //binding.recyclerCategorias.adapter = IconosAdapter(obtenerIconos())
        binding.recyclerCategorias.layoutManager = layout3
    }

    override fun onRestart() {
        super.onRestart()
        setRecyclers()
    }

    private fun setListeners() {
        binding.btnAdd.setOnClickListener {
            val nombreCategorias = obtenerCategorias()
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
    }

    private fun obtenerCategorias(): MutableList<String> {
        var nombresCategorias: MutableList<String> = mutableListOf()
        for(categoriaLista in categorias){
            val categoria = categoriaLista.trim().split(" ")
            nombresCategorias.add(categoria[0])
        }

        return nombresCategorias
    }

/*    private fun obtenerIconos(): MutableList<Icono> {
        var iconosCategorias: MutableList<Icono> = mutableListOf()
        for(categoriaLista in categorias){
            val categoria = categoriaLista.trim().split(" ")
            iconosCategorias.add(categoria[0])
        }
        return iconosCategorias
    }*/

    private fun cargarFragment() {
        //val array: MutableList<String> = preferences.getArray().toMutableList()
        val fg = FragmentCategorias(categorias)
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.fg_categoria,fg)
        }
    }
}