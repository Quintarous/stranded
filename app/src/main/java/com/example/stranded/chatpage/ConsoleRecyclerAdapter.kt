package com.example.stranded.chatpage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stranded.databinding.ConsoleAdapterItemBinding


/**
 * ConsoleRecyclerAdapter is very similar to the ChatRecyclerAdapter. The main difference is
 * it doesn't need to worry about user lines. So we can simply give it strings instead of full on
 * ScriptLines for it's dataset. We also get to skip all the logic for discerning between user
 * lines and regular lines because of this!
 */
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
        holder.bind(dataset[position], viewModel) // binding the data

        // skipping the animation if this item is the last one in the list
        if (position != dataset.size - 1) {

            holder.skipAnimation()
        } else {

            // also skip if it's already been animated before
            if (viewModel.consoleLastItemAnimated.value == position) {
                holder.skipAnimation()

            // if it doesn't get skipped update the viewModel to reflect that it's been animated now
            } else {
                viewModel.consoleLastItemAnimated.value = position
            }
        }
    }


    override fun getItemCount(): Int = dataset.size


    /**
     * The only reason the ChatPageViewModel is passed into every layout is because
     * CustomTextView needs to observe the letterDuration LiveData from the ViewModel.
     */
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