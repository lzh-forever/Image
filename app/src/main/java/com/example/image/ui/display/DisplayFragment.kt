package com.example.image.ui.display

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.image.Repository
import com.example.image.databinding.FragmentDisplayBinding
import com.example.image.ui.detail.AddActivity

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
        // using paging3 lib to get flow<PagingData>, in case not requesting all records at one time
        // collect data in the flow and pass it to adapter
        lifecycleScope.launch {
            viewModel.getPagingData().collect { pagingData ->
                photoAdapter.submitData(pagingData)
            }
        }

        binding.addButton.setOnClickListener {
            val intent = Intent(activity, AddActivity::class.java).apply {
                putExtra("edit",false)
            }
            startActivity(intent)
        }

        lifecycleScope.launch {
            Repository.getCount().collect { count ->
                if (count==0){
                    binding.init.visibility=View.VISIBLE
                } else{
                    binding.init.visibility = View.GONE
                }
            }
        }

        return binding.root
    }
}