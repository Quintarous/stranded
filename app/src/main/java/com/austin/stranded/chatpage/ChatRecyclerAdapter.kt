package com.austin.stranded.chatpage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.austin.stranded.R
import com.austin.stranded.database.ScriptLine
import com.austin.stranded.databinding.ChatAdapterItemBinding
import com.austin.stranded.databinding.ChatAdapterUserItemBinding

/**
 * Adapter for the chatRecycler recycler view.
 *
 * Takes ScriptLine objects and displays their text in CustomTextView views. These CustomTextViews
 * automatically animate when created so this adapter also determines which items need their
 * animation skipped immediately. This is every single one besides the most recent one by the way.
 *
 * NOTE: The only difference between UserLineViewHolder and ScriptLineViewHolder (and by extension
 * the difference between normal ScriptLines and user ScriptLines) is that user lines are stuck
 * to the right side of the screen. And normal lines are on the left.
 */
class ChatRecyclerAdapter(
    val dataset: MutableList<ScriptLine>,
    val viewModel: ChatPageViewModel
    ): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {

            // when the ScriptLine we're displaying is a "user" line (ie: it's a response to a prompt)
            R.layout.chat_adapter_user_item -> {
                val binding = ChatAdapterUserItemBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                UserLineViewHolder(binding) // put it in the UserLineViewHolder class
            }

            // otherwise it must be a "script" line
            else -> {
                val binding = ChatAdapterItemBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                ScriptLineViewHolder(binding) // put it in the ScriptLineViewHolder class
            }
        }
    }


    /**
     * Aside from calling each ViewHolders bind() method we are also running the logic for
     * automatically skipping animations here.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is ScriptLineViewHolder -> {

                holder.bind(dataset[position].line, viewModel) // binding the data

                // auto skipping the animation if the ViewHolder is not the last one in the dataset
                if (position != dataset.size - 1) {
                    holder.skipAnimation()

                } else {
                    // if it is the last one in the dataset than we check if it's already been
                    // animated before and if so skip
                    if (viewModel.chatLastItemAnimated.value == position) {
                        holder.skipAnimation()

                    } else {
                        // if it hasn't yet been animated then update the ViewModel
                        // LiveData to reflect that it now has
                        viewModel.chatLastItemAnimated.value = position
                    }
                }
            }

            // same as above just for the UserLineViewHolder instead
            else -> {

                holder as UserLineViewHolder
                holder.bind(dataset[position].line, viewModel)

                if (position != dataset.size - 1) {
                    holder.skipAnimation()

                } else {
                    if (viewModel.chatLastItemAnimated.value == position) {
                        holder.skipAnimation()
                    } else {
                        viewModel.chatLastItemAnimated.value = position
                    }
                }
            }
        }
    }


    override fun getItemCount(): Int = dataset.size


    /**
     * User lines need a different layout so they can be displayed on the right side of the screen.
     * This method determines which type of ScriptLine it is and reports back with the correct layout.
     */
    override fun getItemViewType(position: Int): Int {
        val item = dataset[position]

        // checking whether the given line is a script or user line
        return when (item.type) {
            "user" -> R.layout.chat_adapter_user_item
            else -> R.layout.chat_adapter_item
        }
    }


    /**
     * The only reason the ChatPageViewModel is passed into every layout is because
     * CustomTextView needs to observe the letterDuration LiveData from the ViewModel.
     */
    class ScriptLineViewHolder(val viewBinding: ChatAdapterItemBinding)
        : RecyclerView.ViewHolder(viewBinding.root) {

        // giving our CustomTextView it's text and a reference to the ViewModel
        fun bind(text: String, viewModel: ChatPageViewModel) {
            viewBinding.viewModel = viewModel
            viewBinding.lineText.text = text
        }

        fun skipAnimation() {
            viewBinding.lineText.skipAnimation()
        }
    }


    class UserLineViewHolder(val viewBinding: ChatAdapterUserItemBinding)
        : RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(text: String, viewModel: ChatPageViewModel) {
            viewBinding.viewModel = viewModel
            viewBinding.lineText.text = text
        }

        fun skipAnimation() {
            viewBinding.lineText.skipAnimation()
        }
    }
}