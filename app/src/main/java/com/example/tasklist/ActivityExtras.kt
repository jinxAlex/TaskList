package com.example.tasklist

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
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
import com.example.tasklist.databinding.ActivityExtrasBinding
import com.example.tasklist.fragment.FragmentPagina
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions

class ActivityExtras : AppCompatActivity(), OnMapReadyCallback {

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos ->
        if (permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true || permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            mostrarUbicacion()
        } else {
            Toast.makeText(this, "ERROR: El usuario ha denegado los permisos", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private lateinit var map: GoogleMap

    private var localizacion = ""

    private var urlPagina = ""

    private lateinit var binding: ActivityExtrasBinding
    
    private var tipoFragment = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityExtrasBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        recogerDatos()
        ponerFragment(tipoFragment)
        setListeners()
    }

    private fun setListeners() {
        binding.btnVolver.setOnClickListener {
            enviarDatos()
            finish()
        }
    }

    private fun enviarDatos() {
        val i = Intent(this,ActivityExtras::class.java)
        val bundle = Bundle()
        when(tipoFragment){
            0 -> {
                bundle.putString("URL",urlPagina)
            }
            1 ->{
                bundle.putString("COORDENADAS",localizacion)
            }
        }
        i.putExtras(bundle)
        setResult(RESULT_OK,i)
        finish()
    }

    private fun recogerDatos() {
        val datos = intent.extras
        tipoFragment = datos?.getInt("TIPO")?: 0
    }

    private fun ponerFragment(tipoFragment: Int) {
        when(tipoFragment){
            0 ->{
                val fg = FragmentPagina({recibirPaginaExtra(it)})
                supportFragmentManager.commit {
                    setReorderingAllowed(false)
                    replace(R.id.fg_extra,fg)
                }
            }
            1 ->{
                val fgMapa = SupportMapFragment()
                fgMapa.getMapAsync(this)
                supportFragmentManager.commit {
                    setReorderingAllowed(false)
                    replace(R.id.fg_extra,fgMapa)
                }
            }
        }
    }

    private fun recibirPaginaExtra(pagina: String) {
        Log.d("URLMETODO",pagina)
        urlPagina = pagina
    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0
        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMapClickListener { latLng ->
            map.clear()
            map.addMarker(MarkerOptions().position(latLng))
            localizacion = String.format("%.6f %.6f", latLng.latitude, latLng.longitude)
        }
        mostrarUbicacion()
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


}