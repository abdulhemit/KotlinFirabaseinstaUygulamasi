package com.example.kotlininstagram

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.kotlininstagram.databinding.ActivityAddPostBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.*
import kotlin.collections.HashMap

class AddPostActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAddPostBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedPicture : Uri? =  null
    private lateinit var db : FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPostBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        mAuth = Firebase.auth
        db = Firebase.firestore
        storage = Firebase.storage
        registerLauncher()
        binding.imageView.setOnClickListener {
            selectedimage(view)
        }
        binding.btnSave.setOnClickListener {
            savepost()
        }
    }
    private fun savepost(){

        val uuid = UUID.randomUUID()
        val imageName ="$uuid.jpg"
       val reference = storage.reference
        val imagereference = reference.child("images").child(imageName)
        if (selectedPicture != null){
            imagereference.putFile(selectedPicture!!).addOnSuccessListener {
                val uploadPictureReference = imagereference.child("images").child(imageName)
                uploadPictureReference.downloadUrl.let {
                    val downloadUrl = it.toString()
                    val postHashmap = HashMap<String,Any>()
                    postHashmap.put("downloadUrl",downloadUrl)
                    postHashmap.put("PostName",binding.editText.text.toString())
                    postHashmap.put("date",Timestamp.now())
                    db.collection("user").add(postHashmap).addOnSuccessListener {
                        Toast.makeText(this@AddPostActivity,"saved post",Toast.LENGTH_LONG).show()
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(this@AddPostActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
                    }
                }
            }.addOnFailureListener{
                Toast.makeText(this@AddPostActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }
    }



    private fun selectedimage(view : View){
        if (ContextCompat.checkSelfPermission(applicationContext,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Permission needed ",Snackbar.LENGTH_INDEFINITE).setAction("give permission"){
                    // request permission
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }.show()
            }else{
                // request permission
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }else{
            // permission granted
            val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            // start activity for result
            activityResultLauncher.launch(intentToGallery)
        }
    }
    private fun registerLauncher(){
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
            if (result.resultCode == RESULT_OK){
                val intentForResult = result.data
                if (intentForResult != null){
                    selectedPicture = intentForResult.data
                    selectedPicture.let {
                        binding.imageView.setImageURI(selectedPicture)
                    }
                }
            }
        }


        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ result->
            if (result){
                // permission granted
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)


            }else{
                // permission denied
                Toast.makeText(this@AddPostActivity,"Permission needed!!",Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding == null
    }
}