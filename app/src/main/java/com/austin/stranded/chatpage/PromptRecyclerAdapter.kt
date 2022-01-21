package com.austin.stranded.chatpage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.austin.stranded.database.PromptLine
import com.austin.stranded.databinding.PromptAdapterItemBinding

/**
 * Adapter for the promptRecycler recycler view.
 *
 * The promptRecyclers job is to display prompt buttons to the user. These are essentially
 * "dialogue options" they can select from. Most of the time the dataset will be empty and
 * the recycler view will be hidden. When PromptLines get added to the dataset by the
 * ChatPageViewModel the recycler view pops up from the bottom of the screen to display
 * the PromptLines as buttons.
 */
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


    /**
     * The only reason the ChatPageViewModel is passed into every layout is because
     * CustomTextView needs to observe the letterDuration LiveData from the ViewModel.
     */
    class SetLineViewHolder (private val binding: PromptAdapterItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(text: String, viewModel: ChatPageViewModel) {
            binding.promptText.text = text
            binding.promptText.setOnClickListener { viewModel.promptSelected(layoutPosition) }
        }
    }
}