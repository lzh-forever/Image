package com.example.image.ui.display

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.image.databinding.ItemBinding
import com.example.image.model.Record
import java.io.File

class PhotoAdapter(private val context:Context) : PagingDataAdapter<Record, PhotoAdapter.ViewHolder>(
    COMPARATOR
) {

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<Record>() {
            override fun areItemsTheSame(oldItem: Record, newItem: Record): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Record, newItem: Record): Boolean {
                return oldItem == newItem
            }

        }
    }

    private val map = HashMap<Int,Boolean>()

    class ViewHolder(binding: ItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val content = binding.contentTextView
        val month = binding.monthTextView
        val day =binding.dayTextView
        val photo = binding.photoImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { record ->
            holder.content.text = record.content
            holder.month.text = record.month.toString()
            holder.day.text = record.day.toString()
            record.photoName?.let {
                Glide.with(context).load(getCachedBitmap(it)).into(holder.photo)
            }
            if (position==2){
                holder.photo.visibility = View.GONE
            }

        }

    }


    private fun getCachedBitmap(name:String) = File(context.filesDir,name)


}