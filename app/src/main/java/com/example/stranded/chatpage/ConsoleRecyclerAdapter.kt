package com.example.stranded.chatpage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stranded.databinding.ConsoleAdapterItemBinding

class ConsoleRecyclerAdapter(val dataset: MutableList<String>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ConsoleAdapterItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ConsoleLineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ConsoleLineViewHolder
        holder.bind(dataset[position])
    }

    override fun getItemCount(): Int = dataset.size

    class ConsoleLineViewHolder(val binding: ConsoleAdapterItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

            fun bind(text: String) {
                binding.consoleLine.text = text
            }
        }
}