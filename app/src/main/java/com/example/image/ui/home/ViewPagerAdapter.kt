package com.example.image.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.image.databinding.PhotoItemBinding

class ViewPagerAdapter(val list: List<Int>, private val context:Context):RecyclerView.Adapter<ViewPagerAdapter.viewHolder>() {

    inner class viewHolder(binding: PhotoItemBinding):RecyclerView.ViewHolder(binding.root){
        val imageView = binding.imageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val binding = PhotoItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return viewHolder(binding)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        list[position].let { id->
            Glide.with(context).load(id).into(holder.imageView)
        }
    }

    override fun getItemCount()= list.size

}