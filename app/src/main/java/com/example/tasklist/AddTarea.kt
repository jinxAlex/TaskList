package com.example.tasklist

import android.os.Bundle
import android.content.Intent
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tasklist.databinding.ActivityAddTareaBinding
import com.example.tasklist.models.Tarea
import com.example.tasklist.providers.db.Crud

class AddTarea : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

    val responseLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        when(it.resultCode){
            RESULT_OK ->{
                when(binding.radioGroup.checkedRadioButtonId){
                    R.id.rb_pagina -> {
                        val dato = it.data?.getStringExtra("URL").toString()
                        Log.d("URL MAIN",dato)
                        binding.tvExtras.setText(dato)
                    }
                    R.id.rb_mapa -> {
                        val dato = it.data?.getStringExtra("COORDENADAS").toString()
                        binding.tvExtras.setText(dato)
                    }
                }
            }
            RESULT_CANCELED -> {

            }
        }
    }

    private var editable: Boolean = false

    private lateinit var binding: ActivityAddTareaBinding

    private lateinit var categorias: ArrayList<String>

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
        recogerDatos()
        ponerDatos()
        setListeners()
    }

    private fun recogerDatos() {
        val datos = intent.extras
        categorias= datos?.getStringArrayList("CATEGORIAS")?: arrayListOf()
    }

    private fun ponerDatos() {
        Log.d("DATOS",categorias.toString())
        binding.spCategoria.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,categorias)
        ponerMinutos()
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
        binding.rbPagina.setOnClickListener{
            irActivityExtra(0)
        }
        binding.rbMapa.setOnClickListener{
            irActivityExtra(1)
        }
        binding.sbTiempo.setOnSeekBarChangeListener(this)
    }

    private fun irActivityExtra(tipoFragment: Int) {
        val bundle = Bundle().apply {
            putInt("TIPO",tipoFragment)
        }
        val i = Intent(this,ActivityExtras::class.java)
        i.putExtras(bundle)
        responseLauncher.launch(i)
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

    private fun ponerMinutos() {
        binding.tvTiempo.setText(String.format(getString(R.string.add_tarea_tv_tiempo),binding.sbTiempo.progress))
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        ponerMinutos()
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

    override fun onStopTrackingTouch(seekBar: SeekBar?) {}


}