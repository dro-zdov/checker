package com.codesample.checker.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.codesample.checker.ViewPagerFragmentDirections
import com.codesample.checker.databinding.TrackedListItemBinding
import com.codesample.checker.entities.db.AdDetailsContainer
import com.codesample.checker.entities.details.AdDetails
import com.codesample.checker.entities.search.Item

class TrackedListAdapter: PagingDataAdapter<AdDetailsContainer, TrackedListViewHolder>(TrackedListDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackedListViewHolder {
        return TrackedListViewHolder(
            TrackedListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TrackedListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item!!)
    }
}

class TrackedListViewHolder(
    private val binding: TrackedListItemBinding
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.setClickListener { view ->
            binding.adDetails?.let { item ->
                navigateToAd(item.details, view)
            }
        }
    }

    private fun navigateToAd(item: AdDetails, view: View) {
        val direction = ViewPagerFragmentDirections.actionViewPagerFragmentToAdDetailFragment(item.id)
        view.findNavController().navigate(direction)
    }

    fun bind(item: AdDetailsContainer) {
        binding.adDetails = item
        binding.executePendingBindings()
    }
}

private class TrackedListDiffCallback : DiffUtil.ItemCallback<AdDetailsContainer>() {
    override fun areItemsTheSame(oldItem: AdDetailsContainer, newItem: AdDetailsContainer): Boolean {
        return oldItem.rowId == newItem.rowId
    }

    override fun areContentsTheSame(oldItem: AdDetailsContainer, newItem: AdDetailsContainer): Boolean {
        return oldItem.details == newItem.details
    }
}