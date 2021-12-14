package com.example.image.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.DatabaseUtils
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.image.NcnnBodyseg
import com.example.image.R
import com.example.image.databinding.ActivityMainBinding
import com.example.image.util.getBitmapFromUri

import com.example.image.util.saveBitmapInMedia
import com.example.image.util.saveBitmapInternal
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private val tag = "MainActivity"
    private val ncnncartoon = NcnnBodyseg()
    lateinit var viewModel: MainViewModel
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ncnncartoon.loadModel(assets,0,0)
        binding =ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel=ViewModelProvider(this).get(MainViewModel::class.java)
        checkAndAskPermission()


        viewModel.snackbar.observe(this){ text->
            text?.let {
                Snackbar.make(binding.root,text,Snackbar.LENGTH_LONG).show()
                Log.d("main",text)
                viewModel.onSnackbarShown()
            }
        }
        binding.button.setOnClickListener {
            albumLauncher.launch("image/*")
        }

        binding.button2.setOnClickListener {
//            viewModel.uri?.let { uri ->
//                val intent = Intent().apply {
//                    action =Intent.ACTION_SEND
//                    putExtra(Intent.EXTRA_STREAM,uri)
//                    type = "image/*"
//                }
//                startActivity(Intent.createChooser(intent, "share photo"))
//            }

            val intent = Intent(this,SecondActivity::class.java)
            startActivity(intent)

        }

        binding.button3.setOnClickListener {
            viewModel.uri?.let { uri ->
                viewModel.bitmap = getBitmapFromUri(contentResolver,uri)
                binding.imageView.setImageResource(R.drawable.ic_launcher_background)

            }
        }

        binding.button4.setOnClickListener {
//            viewModel.bitmap?.let { bitmap ->
//                binding.imageView.setImageBitmap(bitmap)
//                ncnncartoon.matting(bitmap)?.let {
//                    saveBitmapInMedia(it,contentResolver)
//                    saveBitmapInternal(it,this)
//                }
//            }
            viewModel.addPhoto(System.currentTimeMillis().toString())
        }



    }

    private val albumLauncher = registerForActivityResult(ActivityResultContracts.GetContent()){ uri->
        uri?.let {
            Glide.with(this).load(uri).into(binding.imageView)
            Log.d(tag,"$uri")
            val cursor = contentResolver.query(uri,null,null,null,null)
            DatabaseUtils.dumpCursor(cursor)
            cursor?.close()
            viewModel.uri=uri }

    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            granted ->
        if (granted){

        } else {
            Toast.makeText(this,"permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkAndAskPermission(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            askPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (Build.VERSION.SDK_INT<=28){
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                askPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    private fun askPermission(permission : String){
        requestPermissionLauncher.launch(permission)
    }
}