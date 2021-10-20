package com.example.stranded.chatpage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stranded.databinding.ConsoleAdapterItemBinding

class ConsoleRecyclerAdapter(
    val dataset: MutableList<String>,
    val viewModel: ChatPageViewModel
    ): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ConsoleAdapterItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ConsoleLineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ConsoleLineViewHolder
        holder.bind(dataset[position], viewModel)

// same code as in the ChatRecyclerAdapter look there for explanation
        if (position != dataset.size - 1) {

            holder.skipAnimation()
        } else {

            if (viewModel.consoleLastItemAnimated.value == position) {
                holder.skipAnimation()
            } else {
                viewModel.consoleLastItemAnimated.value = position
            }
        }
    }

    override fun getItemCount(): Int = dataset.size

    class ConsoleLineViewHolder(val viewBinding: ConsoleAdapterItemBinding)
        : RecyclerView.ViewHolder(viewBinding.root) {

            fun bind(text: String, viewModel: ChatPageViewModel) {
                viewBinding.viewModel = viewModel
                viewBinding.consoleLine.text = text
            }

            fun skipAnimation() {
                viewBinding.consoleLine.skipAnimation()
            }
        }
}