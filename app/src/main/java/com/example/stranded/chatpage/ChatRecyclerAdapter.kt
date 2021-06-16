package com.example.stranded.chatpage

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stranded.R
import com.example.stranded.databinding.ChatAdapterItemBinding
import kotlinx.android.synthetic.main.chat_adapter_item.view.*

class ChatRecyclerAdapter(val dataset: MutableList<String>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ChatAdapterItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ScriptLineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ScriptLineViewHolder
        holder.bind(dataset[position])
    }

    override fun getItemCount(): Int = dataset.size

    class ScriptLineViewHolder(private val viewBinding: ChatAdapterItemBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(text: String) {
            viewBinding.lineText.text = text
        }
    }
}