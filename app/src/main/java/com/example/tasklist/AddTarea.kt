package com.example.tasklist

import android.os.Bundle
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import com.example.tasklist.databinding.ActivityAddTareaBinding
import com.example.tasklist.fragment.FragmentPagina
import com.example.tasklist.models.Tarea
import com.example.tasklist.providers.db.Crud
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

class AddTarea : AppCompatActivity(), SeekBar.OnSeekBarChangeListener, OnMapReadyCallback {

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()){
            permisos -> if(permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true || permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
                mostrarUbicacion()
            }else{
                Toast.makeText(this,"ERROR: El usuario ha denegado los permisos", Toast.LENGTH_SHORT).show()
            }
    }

    private lateinit var map: GoogleMap

    private lateinit var fgMapa: SupportMapFragment

    private lateinit var fgPagina: FragmentPagina

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
        ponerMinutos()
        binding.rbPagina.isChecked = true
        ponerPagina()
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
            ponerPagina()
        }
        binding.rbMapa.setOnClickListener{
            ponerMapa()
        }
        binding.sbTiempo.setOnSeekBarChangeListener(this)
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


    private fun ponerMapa() {
        val fgMapa = SupportMapFragment()
        fgMapa.getMapAsync(this)
        supportFragmentManager.commit {
            setReorderingAllowed(false)
            replace(R.id.fg_extra,fgMapa)
        }
    }

    private fun mostrarUbicacion(){
        if(::map.isInitialized){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    map.isMyLocationEnabled = true
                    map.uiSettings.isMyLocationButtonEnabled = true
            }else{
                solicitarPermisos()
            }
        }
    }

    private fun solicitarPermisos() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
            ||
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)){
            mostrarExplicacion()
        }else{
            escogerPermisos()
        }
    }

    private fun mostrarExplicacion() {
        AlertDialog.Builder(this)
            .setTitle("Permisos de Ubicación")
            .setMessage("Para poder establecer un punto para la tarea es necesario el permiso de ubicación")
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .setPositiveButton("Aceptar") { dialog, _ ->
                startActivity(Intent(Settings.ACTION_APPLICATION_SETTINGS))
                dialog.dismiss()

            }
            .create()
            .show()
    }

    private fun escogerPermisos(){
        locationPermissionRequest.launch(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        )
    }

    private fun ponerPagina() {
        fgPagina = FragmentPagina()
        supportFragmentManager.commit {
            setReorderingAllowed(false)
            replace(R.id.fg_extra,fgPagina)
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        ponerMinutos()
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

    override fun onStopTrackingTouch(seekBar: SeekBar?) {}

    override fun onMapReady(p0: GoogleMap) {
        map = p0
        map.uiSettings.isZoomControlsEnabled = true
        mostrarUbicacion()
    }
}