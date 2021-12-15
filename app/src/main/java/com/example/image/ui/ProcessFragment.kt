package com.example.image.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.image.databinding.FragmentProcessBinding


class ProcessFragment : Fragment() {
    lateinit var binding: FragmentProcessBinding
    lateinit var viewmodel: ProcessViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProcessBinding.inflate(inflater,container,false)
        viewmodel = ViewModelProvider(this).get(ProcessViewModel::class.java)


        return binding.root
    }
}