package com.example.tasklist

import android.os.Bundle
import android.content.Intent
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

    private val responseLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        when(it.resultCode){
            RESULT_OK ->{
                when(binding.radioGroup.checkedRadioButtonId){
                    R.id.rb_pagina -> {
                        val dato = it.data?.getStringExtra("URL").toString()
                        ponerExtras(String.format(getString(R.string.add_tarea_tv_extras_pagina), dato))
                        pagina = dato
                    }
                    R.id.rb_mapa -> {
                        val dato = it.data?.getStringExtra("COORDENADAS").toString()
                        ponerExtras(String.format(getString(R.string.add_tarea_tv_extras_mapa), dato))
                        localizacion = dato
                    }
                }
            }
            RESULT_CANCELED -> {

            }
        }
    }

    private var editable: Boolean = false

    private lateinit var binding: ActivityAddTareaBinding

    private var pagina: String = ""

    private var localizacion: String = ""

    private lateinit var categorias: ArrayList<String>

    private lateinit var tareaEditar: Tarea

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
        editable = datos?.getBoolean("EDITABLE")?: false
        categorias= datos?.getStringArrayList("CATEGORIAS")?: arrayListOf()
        if(editable) {
            tareaEditar = datos?.getSerializable("TAREA") as Tarea
        }
    }

    private fun ponerDatos() {
        binding.spCategoria.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,categorias)
        if(editable){
            binding.tvTitulo.text = getString(R.string.add_tarea_tv_titulo_editar)
            binding.etNombre.setText(tareaEditar.nombre)
            binding.etDescripcion.setText(tareaEditar.descripcion)
            binding.sbTiempo.progress = tareaEditar.tiempo
            if(tareaEditar.prioridad){
                binding.cbPrioridad.isChecked = true
            }
            binding.spCategoria.setSelection(categorias.indexOf(tareaEditar.categoria))
            binding.btnEnviar.text = getString(R.string.add_tarea_btn_editar)
            id = tareaEditar.id
            if(tareaEditar.localizacion.isEmpty()){
                binding.rbPagina.isChecked = true
                pagina = tareaEditar.pagina
                binding.tvExtras.setText(String.format(getString(R.string.add_tarea_tv_extras_pagina), pagina))
            }else{
                binding.rbMapa.isChecked = true
                localizacion = tareaEditar.localizacion
                binding.tvExtras.setText(String.format(getString(R.string.add_tarea_tv_extras_mapa), localizacion))
            }
        }
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
        if(binding.sbTiempo.progress == 0){
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
            putString("EXTRA",if(tipoFragment== 0) pagina else localizacion)
        }
        val i = Intent(this,ActivityExtras::class.java)
        i.putExtras(bundle)
        responseLauncher.launch(i)
    }

    private fun enviarDatos() {
        if(editable){
            val tarea = Tarea(id,
                binding.etNombre.text.toString(),
                binding.etDescripcion.text.toString(),
                binding.sbTiempo.progress,
                false,
                binding.spCategoria.getItemAtPosition(binding.spCategoria.selectedItemPosition).toString(),
                localizacion,
                pagina,
                binding.cbPrioridad.isChecked)
            if(Crud().update(tarea)){
                Toast.makeText(this,"Se ha modificado la tarea", Toast.LENGTH_LONG).show()
                finish()
            }else{
                Toast.makeText(this,"ERROR: El nombre no puede estar duplicado", Toast.LENGTH_LONG).show()
            }
        }else{
            val tarea = Tarea(id,
                binding.etNombre.text.toString(),
                binding.etDescripcion.text.toString(),
                binding.sbTiempo.progress,
                false,
                binding.spCategoria.getItemAtPosition(binding.spCategoria.selectedItemPosition).toString(),
                localizacion,
                pagina,
                binding.cbPrioridad.isChecked)
            if(Crud().create(tarea)!= -1L){
                Toast.makeText(this,"Se ha añadido la tarea", Toast.LENGTH_LONG).show()
                finish()
            }else{
                Toast.makeText(this,"ERROR: El nombre no puede estar duplicado", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun ponerExtras(string: String){
        binding.tvExtras.text = string
    }

    private fun ponerMinutos() {
        binding.tvTiempo.text = String.format(getString(R.string.add_tarea_tv_tiempo),binding.sbTiempo.progress)
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        ponerMinutos()
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

    override fun onStopTrackingTouch(seekBar: SeekBar?) {}


}