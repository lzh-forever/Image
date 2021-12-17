package com.example.image.ui.detail

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.image.databinding.ActivityAddBinding
import com.example.image.model.Record
import com.example.image.util.getBitmapFromUri
import com.example.image.util.saveBitmapInternal
import java.io.File

class AddActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddBinding
    lateinit var viewModel: AddViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        viewModel = ViewModelProvider(this).get(AddViewModel::class.java)

        val update = intent.getBooleanExtra("edit", false)
        viewModel.update =update
        if (!update) {
            binding.delete.visibility = View.GONE
        } else {

            val content = intent.getStringExtra("content") ?: ""
            val photoName = intent.getStringExtra("photoName")
            val date = intent.getStringExtra("date")!!
            val id = intent.getLongExtra("id",0)
            binding.content.setText(content)
            viewModel.setDate(date)
            photoName?.let {
                Glide.with(this).load(getCachedBitmap(it)).into(binding.image)
                viewModel.photoName = it
            }
            viewModel.record = Record(content).apply {
                this.id = id
                this.photoName =photoName
                this.date = date
            }


        }



        viewModel.date.observe(this) { date ->
            binding.date.text = date
        }


        val dialog = DatePickerDialog(this)
        dialog.setOnDateSetListener { datePicker, year, month, day ->
            viewModel.setDate(year, month, day)
        }

        binding.datePick.setOnClickListener {
            dialog.show()
        }
        binding.image.setOnClickListener {
            albumLauncher.launch("image/*")
        }
        binding.save.setOnClickListener {
           viewModel.uri?.let { uri ->
                getBitmapFromUri(contentResolver, uri)?.let {
                    viewModel.photoName = saveBitmapInternal(it, this)
                }
            }
            val content = binding.content.text.toString()
            viewModel.saveRecord(content)
            finish()
        }

        binding.delete.setOnClickListener {
            viewModel.deleteRecord()
            finish()
        }
    }

    private fun getCachedBitmap(name:String) = File(filesDir,name)

    private val albumLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                Glide.with(this).load(uri).into(binding.image)
                viewModel.uri = uri
            }
        }


}