package com.example.image.ui.process

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.image.NcnnBodyseg
import com.example.image.NcnnCartoon
import com.example.image.databinding.FragmentProcessBinding
import com.example.image.util.getBitmapFromUri
import com.example.image.util.saveBitmapInMedia
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ProcessFragment : Fragment() {
    lateinit var binding: FragmentProcessBinding
    lateinit var viewModel: ProcessViewModel
    lateinit var ncnnBodyseg: NcnnBodyseg
    lateinit var ncnncartoon: NcnnCartoon

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProcessBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(ProcessViewModel::class.java)
        viewModel.state.value=viewModel.STATE_TO_LOAD
        binding.floatingActionButton.setOnClickListener {
            albumLauncher.launch("image/*")
        }

        binding.saveButton.setOnClickListener {
            viewModel.processBitmap?.let { bitmap ->
                activity?.let { activity ->
                    viewModel.savedUri = saveBitmapInMedia(bitmap, activity.contentResolver)
                    viewModel.saved = true
                }
            }
        }

        binding.shareButton.setOnClickListener {
            if (viewModel.saved){
                viewModel.savedUri?.let { uri ->
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_STREAM, uri)
                        type = "image/*"
                    }
                    startActivity(Intent.createChooser(intent, "share photo"))
                }
            } else{
                viewModel.processBitmap?.let { bitmap ->
                    activity?.let { activity ->
                        viewModel.savedUri = saveBitmapInMedia(bitmap, activity.contentResolver)
                        viewModel.saved = true
                        viewModel.savedUri?.let { uri ->
                            val intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_STREAM, uri)
                                type = "image/*"
                            }
                            startActivity(Intent.createChooser(intent, "share photo"))
                        }
                    }
                }
            }
        }

        binding.process1.setOnClickListener {
            lifecycleScope.launch {
                withContext(Dispatchers.Default) {
                    if (!this@ProcessFragment::ncnnBodyseg.isInitialized) {
                        ncnnBodyseg = NcnnBodyseg().apply {
                            loadModel(this@ProcessFragment.activity?.assets, 0, 0)
                        }
                    }
                    val bitmap = getBitmapFromUri(
                        this@ProcessFragment.activity!!.contentResolver,
                        viewModel.uri!!
                    )
                    viewModel.processBitmap = ncnnBodyseg.matting(bitmap)
                    withContext(Dispatchers.Main){
                        Glide.with(this@ProcessFragment).load(viewModel.processBitmap).into(binding.image)
                    }
                    viewModel.state.postValue(viewModel.STATE_PROCESSED)
                }

            }
        }

        binding.process2.setOnClickListener {
            lifecycleScope.launch {
                withContext(Dispatchers.Default) {
                    if (!this@ProcessFragment::ncnncartoon.isInitialized) {
                        ncnncartoon = NcnnCartoon().apply {
                            loadModel(this@ProcessFragment.activity?.assets, 0, 0)
                        }
                    }
                    val bitmap = getBitmapFromUri(
                        this@ProcessFragment.activity!!.contentResolver,
                        viewModel.uri!!
                    )
                    viewModel.processBitmap = ncnncartoon.cartoon(bitmap)
                    withContext(Dispatchers.Main){
                        Glide.with(this@ProcessFragment).load(viewModel.processBitmap).into(binding.image)
                    }
                    viewModel.state.postValue(viewModel.STATE_PROCESSED)

                }
            }
        }

        viewModel.state.observe(this) { state ->
            Log.d("process","$state")
            when (state) {
                viewModel.STATE_TO_LOAD -> {
                    binding.shareButton.visibility = View.GONE
                    binding.saveButton.visibility = View.GONE
                    binding.process1.visibility = View.GONE
                    binding.process2.visibility = View.GONE
                    viewModel.savedUri = null
                    viewModel.saved = false
                }
                viewModel.STATE_TO_PROCESS -> {
                    binding.shareButton.visibility = View.GONE
                    binding.saveButton.visibility = View.GONE
                    binding.process1.visibility = View.VISIBLE
                    binding.process2.visibility = View.VISIBLE
                    viewModel.savedUri = null
                    viewModel.saved = false
                }
                viewModel.STATE_PROCESSED -> {
                    binding.shareButton.visibility = View.VISIBLE
                    binding.saveButton.visibility = View.VISIBLE
                    binding.process1.visibility = View.GONE
                    binding.process2.visibility = View.GONE
                }
            }

        }




        return binding.root
    }

    private val albumLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                Glide.with(this).load(uri).into(binding.image)
                viewModel.uri = uri
                viewModel.state.value = viewModel.STATE_TO_PROCESS
            }
        }
}