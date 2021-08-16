package com.soten.remotemonster.viedeochat

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.soten.remotemonster.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val auth by lazy { Firebase.auth }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerButton.setOnClickListener {
            val email = binding.inputEmail.text.toString()
            val password = binding.inputPassword.text.toString()
            auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
            }
        }

        binding.loginButton.setOnClickListener {
            saveUserDataToDatabase(auth.currentUser)
        }

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
            0
        )
    }

    override fun onStart() {
        super.onStart()
        auth.currentUser?.let {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun saveUserDataToDatabase(user: FirebaseUser?) {
        val userDTO = UserDTO(email = user?.email)

        FirebaseFirestore.getInstance().collection("users").document(user?.uid.orEmpty()).set(userDTO)
        finish()
        startActivity(Intent(this, MainActivity::class.java))
    }

}