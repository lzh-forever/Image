package com.example.image.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.image.databinding.ActivitySecondBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SecondActivity : AppCompatActivity() {

    lateinit var binding: ActivitySecondBinding
    lateinit var viewModel: SecondViewModel
    val photoAdapter = PhotoAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(SecondViewModel::class.java)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@SecondActivity)
            adapter = photoAdapter
        }

        lifecycleScope.launch {
            viewModel.getPagingData().collect { pagingData ->
                photoAdapter.submitData(pagingData)
            }
        }

    }
}