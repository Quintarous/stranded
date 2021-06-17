package com.example.stranded.chatpage

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stranded.databinding.PromptAdapterItemBinding

class PromptRecyclerAdapter(val dataset: MutableList<Line>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = PromptAdapterItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return SetLineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as SetLineViewHolder
        holder.bind(dataset[position].line)
    }

    override fun getItemCount(): Int = dataset.size

    class SetLineViewHolder (private val binding: PromptAdapterItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(text: String) {
            binding.promptText.text = text
        }
    }
}