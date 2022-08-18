package com.example.kotlininstagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.kotlininstagram.databinding.ActivitySingUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SingUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySingUpBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        mAuth = Firebase.auth
        db = Firebase.firestore
        if (mAuth.currentUser != null){
            val intent = Intent(this@SingUpActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        if(binding.edtName.text == null || binding.edtEmail.text == null || binding.edtPassword.text == null){
            Toast.makeText(this@SingUpActivity,"fill in the texts!!",Toast.LENGTH_LONG).show()
        }else{
            binding.btnSingup.setOnClickListener {
                singupUserToFirabase(binding.edtName.text.toString(),binding.edtEmail.text.toString(),binding.edtPassword.text.toString())
            }
        }

    }
    private fun singupUserToFirabase( name:String, email:String,password:String){
        mAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener {
            // kullaniciyi kaytetmek
            val uid = mAuth.uid
            val userHashmap =HashMap<String,Any>()
            userHashmap.put("name",name)
            userHashmap.put("email",email)
            userHashmap.put("uid",uid.toString())
            db.collection("user").add(userHashmap).addOnSuccessListener {
                Toast.makeText(this@SingUpActivity,"user saved",Toast.LENGTH_LONG).show()
            }.addOnFailureListener {
                Toast.makeText(this@SingUpActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
            val intent = Intent(this@SingUpActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
        }.addOnFailureListener {
            Toast.makeText(this@SingUpActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        binding == null
    }
}