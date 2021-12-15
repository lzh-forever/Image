package com.example.image.ui.display

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.image.databinding.FragmentDisplayBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class DisplayFragment : Fragment() {
    lateinit var binding: FragmentDisplayBinding
    lateinit var viewModel: DisplayViewModel
    lateinit var photoAdapter: PhotoAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDisplayBinding.inflate(inflater,container,false)
        viewModel = ViewModelProvider(this).get(DisplayViewModel::class.java)

        photoAdapter = PhotoAdapter(requireContext())
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = photoAdapter
        }

        lifecycleScope.launch {
            viewModel.getPagingData().collect { pagingData ->
                photoAdapter.submitData(pagingData)
            }
        }

        return binding.root
    }
}