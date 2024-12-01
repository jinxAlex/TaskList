package com.example.tasklist

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import com.example.tasklist.databinding.ActivityAddTareaBinding
import com.example.tasklist.fragment.FragmentPagina
import com.example.tasklist.models.Tarea
import com.example.tasklist.providers.db.Crud

class AddTarea : AppCompatActivity() {

    private var editable: Boolean = false

    private lateinit var binding: ActivityAddTareaBinding

    private var id= -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAddTareaBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setListeners()
    }


    private fun comprobarDatos(): Boolean {
        var esCorrecto = true
        if(binding.etNombre.text.isEmpty()){
            esCorrecto = false
        }
        if(binding.etDescripcion.text.isEmpty()){
            esCorrecto = false
        }
        return esCorrecto
    }

    private fun setListeners() {
        binding.btnEnviar.setOnClickListener{
            if(comprobarDatos()){
                enviarDatos()
            }

        }
    }

    private fun enviarDatos() {
        val tarea = Tarea(id,
            binding.etNombre.text.toString(),
            binding.etDescripcion.text.toString(),
            binding.sbTiempo.progress,
            false,
            "",
            "",
            "",
            binding.cbPrioridad.isChecked)
        if(Crud().create(tarea)!= -1L){
            Toast.makeText(this,"Se ha añadido la tarea", Toast.LENGTH_LONG)
            finish()
        }else{
            Toast.makeText(this,"ERROR: El nombre no puede estar duplicado", Toast.LENGTH_LONG)
        }
    }

    private fun ponerMapa() {

    }

    private fun ponerPagina() {

    }
}