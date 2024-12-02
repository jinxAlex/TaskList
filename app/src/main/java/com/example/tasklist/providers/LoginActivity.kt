package com.example.tasklist.providers

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tasklist.MainActivity
import com.example.tasklist.R
import com.example.tasklist.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)


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

        //responseLauncher.launch(googleClient.signInIntent)
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
                if(it.isSuccessful) irMainActivity()
            }
            .addOnFailureListener {
                Toast.makeText(this,it.message.toString(),Toast.LENGTH_LONG).show()
            }
    }

    private fun login() {
        auth.signInWithEmailAndPassword(binding.etEmail.text.toString(),binding.etPass.text.toString())
            .addOnCompleteListener {
                if(it.isSuccessful) irMainActivity()
            }
            .addOnFailureListener {
                Toast.makeText(this,it.message.toString(),Toast.LENGTH_LONG).show()
            }
    }

    override fun onStart() {
        super.onStart()
        val usuario = auth.currentUser
        if(usuario != null) irMainActivity()
    }

    private fun irMainActivity() {
        val i = Intent(this,MainActivity::class.java)
        startActivity(i)
    }


}