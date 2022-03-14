package com.gredicer.videoedit.util

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.gredicer.videoedit.R


class TestAdapter : RecyclerView.Adapter<TestAdapter.ViewHolder>() {
    private val list: MutableList<Bitmap> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.media_bitmap_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bitmap = list[position]
        holder.mIv.setImageBitmap(bitmap)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateList(list: ArrayList<Bitmap>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mIv: ImageView

        init {
            mIv = itemView.findViewById(R.id.mIv)
        }
    }
}