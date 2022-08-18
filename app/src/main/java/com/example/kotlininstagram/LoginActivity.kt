package com.example.kotlininstagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.kotlininstagram.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mAuth = Firebase.auth
        if (mAuth.currentUser != null){
            val intent = Intent(this@LoginActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnSingup.setOnClickListener {
            goToSingUPActivity()
        }
        if( binding.edtEmail.text == null || binding.edtPassword.text == null){
            Toast.makeText(this@LoginActivity,"fill in the texts!!", Toast.LENGTH_LONG).show()
        }else{
            binding.btnSignin.setOnClickListener {
                signinUserToFirabase(binding.edtEmail.text.toString(),binding.edtPassword.text.toString())
            }
        }
    }

    private fun signinUserToFirabase(email:String,password:String){
        mAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener {
            val intent = Intent(this@LoginActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
        }.addOnFailureListener {
            Toast.makeText(this@LoginActivity,it.localizedMessage.toString(),Toast.LENGTH_LONG).show()
        }
    }


    private fun goToSingUPActivity(){
        val intent = Intent(this@LoginActivity,SingUpActivity::class.java)
        startActivity(intent)
    }
    override fun onDestroy() {
        super.onDestroy()
        binding == null
    }
}