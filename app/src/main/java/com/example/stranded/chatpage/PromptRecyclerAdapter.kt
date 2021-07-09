package com.example.stranded.chatpage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stranded.database.PromptLine
import com.example.stranded.databinding.PromptAdapterItemBinding

class PromptRecyclerAdapter(var dataset: MutableList<PromptLine>, val viewModel: ChatPageViewModel)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = PromptAdapterItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return SetLineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as SetLineViewHolder
        holder.bind(dataset[position].line, viewModel)
    }

    override fun getItemCount(): Int = dataset.size

    class SetLineViewHolder (private val binding: PromptAdapterItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(text: String, viewModel: ChatPageViewModel) {
            binding.promptText.text = text
            binding.promptText.setOnClickListener { viewModel.promptSelected(adapterPosition) }
        }
    }
}