package com.example.image.ui.display

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.image.databinding.ItemBinding
import com.example.image.databinding.ItemTestBinding
import com.example.image.model.Record
import com.example.image.ui.detail.AddActivity
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

//    private val map = HashMap<String,Boolean>()

    class ViewHolder(binding: ItemTestBinding) : RecyclerView.ViewHolder(binding.root) {
        val content = binding.contentTextView
        val date = binding.dateView
        val photo = binding.photoImageView

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { record ->
            holder.content.text = record.content
            holder.date.date =record.date
            record.photoName?.let {
                Glide.with(context).load(getCachedBitmap(it)).into(holder.photo)
            }
//            val year = record.date.split("-")[0]
//
//            if (map.get(year) == null){
//                map.put(year,true)
//                holder.year.text = year
//                holder.year.visibility = View.VISIBLE
//            } else if (map.get(year) == true)

            holder.itemView.setOnClickListener {
                val intent = Intent(context, AddActivity::class.java).apply {
                    putExtra("edit",true)
                    putExtra("content",record.content)
                    putExtra("photoName",record.photoName)
                    putExtra("date",record.date)
                    putExtra("id",record.id)
                }
                context.startActivity(intent)
            }

        }


    }


    private fun getCachedBitmap(name:String) = File(context.filesDir,name)


}