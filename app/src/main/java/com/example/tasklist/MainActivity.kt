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

import com.example.tasklist.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {

    private val responseLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == RESULT_OK){
            val datos = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val cuenta = datos.getResult(ApiException::class.java)
                if(cuenta != null) {
                    val credenciales = GoogleAuthProvider.getCredential(cuenta.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credenciales)
                        .addOnCompleteListener {
                            if (it.isSuccessful)
                                irTareasActivity()
                        }
                        .addOnFailureListener{
                            Toast.makeText(this,it.message.toString(),Toast.LENGTH_SHORT)
                        }
                    }

                }catch (e: Exception){
                    Toast.makeText(this,"ERROR",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private  lateinit var binding: ActivityMainBinding

    private lateinit var auth: FirebaseAuth


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

        auth = Firebase.auth
        setListeners()

    }

    private fun setListeners() {
        binding.btnRegistrar.setOnClickListener{
            registrar()
        }
        binding.btnLogin.setOnClickListener{
            if(datosCorrectos()){
                login()
            }
        }
        binding.btnGoogle.setOnClickListener {
            if (datosCorrectos()){
                loginGoogle()
            }
        }
    }

    private fun loginGoogle() {
        val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_cliente_id))
            .requestEmail()
            .build()
        val googleClient = GoogleSignIn.getClient(this,googleConf)

        googleClient.signOut()

        responseLauncher.launch(googleClient.signInIntent)
    }

    private fun datosCorrectos(): Boolean{
        var esCorrecto = true
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text).matches()){
            binding.etPass.error = "ERROR: El email no es valido"
            esCorrecto = false
        }

        if(binding.etPass.text.length < 9){
            binding.etPass.error = "ERROR: La contraseña debe tener al menos 9 carácteres"
            return false
        }

        return esCorrecto
    }

    private fun registrar() {
        auth.createUserWithEmailAndPassword(binding.etEmail.text.toString(),binding.etPass.text.toString())
            .addOnCompleteListener {
                if(it.isSuccessful) irTareasActivity()
            }
            .addOnFailureListener {
                Toast.makeText(this,it.message.toString(), Toast.LENGTH_LONG).show()
            }
    }

    private fun login() {
        auth.signInWithEmailAndPassword(binding.etEmail.text.toString(),binding.etPass.text.toString())
            .addOnCompleteListener {
                if(it.isSuccessful) irTareasActivity()
            }
            .addOnFailureListener {
                Toast.makeText(this,it.message.toString(), Toast.LENGTH_LONG).show()
            }
    }



    override fun onStart() {
        super.onStart()
        val usuario = auth.currentUser
        if(usuario != null) irTareasActivity()
    }

    private fun irTareasActivity() {
        val i = Intent(this,TareasActivity::class.java)
        startActivity(i)
    }
}