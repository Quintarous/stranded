package com.example.stranded.chatpage

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.stranded.R
import com.example.stranded.database.ScriptLine
import com.example.stranded.databinding.ChatAdapterItemBinding
import com.example.stranded.databinding.ChatAdapterUserItemBinding

class ChatRecyclerAdapter(
    val dataset: MutableList<ScriptLine>,
    val viewModel: ChatPageViewModel
    ): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            //inflating a UserLineViewHolder when viewType is a user line
            R.layout.chat_adapter_user_item -> {
                val binding = ChatAdapterUserItemBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                UserLineViewHolder(binding)
            }

            else -> {
                val binding = ChatAdapterItemBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                ScriptLineViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ScriptLineViewHolder -> {
                holder.bind(dataset[position].line)

// auto skipping the animation if the ViewHolder is not the last one in the dataset
                if (position != dataset.size - 1) {

                    holder.skipAnimation()
                } else {

// if it is the last one in the dataset than we check if it's already been animated before and if so skip
                    if (viewModel.chatLastItemAnimated.value == position) { //
                        holder.skipAnimation()
                    } else {
// if it hasn't yet been animated than update the ViewModel LiveData to say that it has
                        viewModel.chatLastItemAnimated.value = position
                    }
                }
            }

            else -> {
                holder as UserLineViewHolder
                holder.bind(dataset[position].line)

                if (position != dataset.size - 1) {
                    holder.skipAnimation()
                }
            }
        }
    }

    override fun getItemCount(): Int = dataset.size

    override fun getItemViewType(position: Int): Int {
        val item = dataset[position]

// checking whether the given line is a script or user line
        return when (item.type) {
            "user" -> R.layout.chat_adapter_user_item
            else -> R.layout.chat_adapter_item
        }
    }

    class ScriptLineViewHolder(val viewBinding: ChatAdapterItemBinding)
        : RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(text: String) {
            viewBinding.lineText.text = text
        }

        fun skipAnimation() {
            viewBinding.lineText.skipAnimation()
        }
    }

    class UserLineViewHolder(val viewBinding: ChatAdapterUserItemBinding)
        : RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(text: String) {
            viewBinding.lineText.text = text
        }

        fun skipAnimation() {
            viewBinding.lineText.skipAnimation()
        }
    }
}