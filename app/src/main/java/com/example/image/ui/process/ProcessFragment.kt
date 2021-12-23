package com.example.image.ui.process

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.image.NcnnUtils
import com.example.image.NcnnCartoon
import com.example.image.databinding.FragmentProcessBinding
import com.example.image.util.getBitmapFromUri
import com.example.image.util.saveBitmapInMedia
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ProcessFragment : Fragment() {
    lateinit var binding: FragmentProcessBinding
    lateinit var viewModel: ProcessViewModel
     val ncnnBodyseg = NcnnUtils()

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
                    Toast.makeText(context,"保存成功",Toast.LENGTH_SHORT).show()
                }
            }
        }

        // if try to share without having saved the photo
        // save and share
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

        // two kinds of way to process photo
        binding.process1.setOnClickListener {
            lifecycleScope.launch {
                withContext(Dispatchers.Default) {
                    val bitmap = getBitmapFromUri(
                        this@ProcessFragment.activity!!.contentResolver,
                        viewModel.uri!!
                    )
                    ncnnBodyseg.loadModel1(activity?.assets,0,0)
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
                    val bitmap = getBitmapFromUri(
                        this@ProcessFragment.activity!!.contentResolver,
                        viewModel.uri!!
                    )
                    ncnnBodyseg.loadModel2(activity?.assets,0,0)
                    viewModel.processBitmap = ncnnBodyseg.cartoon(bitmap)
                    withContext(Dispatchers.Main){
                        Glide.with(this@ProcessFragment).load(viewModel.processBitmap).into(binding.image)
                    }
                    viewModel.state.postValue(viewModel.STATE_PROCESSED)

                }
            }
        }

        // three states -- before load, after load, get the processed to save and share
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

    override fun onDestroyView() {
        ncnnBodyseg.clear()
        super.onDestroyView()
    }

    // select photo
    private val albumLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                Glide.with(this).load(uri).into(binding.image)
                viewModel.uri = uri
                viewModel.state.value = viewModel.STATE_TO_PROCESS
            }
        }
}