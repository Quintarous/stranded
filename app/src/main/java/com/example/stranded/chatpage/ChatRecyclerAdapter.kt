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

class ChatRecyclerAdapter(val dataset: MutableList<ScriptLine>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            //inflating a UserLineViewHolder when viewType is a user line
            R.layout.chat_adapter_user_item -> {
                val binding = ChatAdapterUserItemBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                UserLineViewHolder(binding)
            }

            //inflating a UserLineViewHolder when viewType is a script line
            else -> {
                val binding = ChatAdapterItemBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                ScriptLineViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ScriptLineViewHolder -> holder.bind(dataset[position].line)

            else -> {
                holder as UserLineViewHolder
                holder.bind(dataset[position].line)
            }
        }
    }

    override fun getItemCount(): Int = dataset.size

    override fun getItemViewType(position: Int): Int {
        val item = dataset[position]

        //checking whether the given line is a script or user line
        return when (item.type) {
            "user" -> R.layout.chat_adapter_user_item
            else -> R.layout.chat_adapter_item
        }
    }

    class ScriptLineViewHolder(private val viewBinding: ChatAdapterItemBinding)
        : RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(text: String) {
            viewBinding.lineText.text = text
        }
    }

    class UserLineViewHolder(private val viewBinding: ChatAdapterUserItemBinding)
        : RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(text: String) {
            viewBinding.lineText.text = text
        }
    }
}