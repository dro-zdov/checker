package com.codesample.checker.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.codesample.checker.ViewPagerFragmentDirections
import com.codesample.checker.databinding.SearchAdsItemBinding
import com.codesample.checker.entities.Item

class SearchAdsAdapter: PagingDataAdapter<Item, SearchAdsViewHolder>(SearchAdsDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchAdsViewHolder {
        return SearchAdsViewHolder(
            SearchAdsItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SearchAdsViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item!!)
    }
}

class SearchAdsViewHolder(
    private val binding: SearchAdsItemBinding
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.setClickListener { view ->
            binding.item?.let { item ->
                navigateToAd(item, view)
            }
        }
    }

    private fun navigateToAd(item: Item, view: View) {
        val direction = ViewPagerFragmentDirections.actionViewPagerFragmentToAdDetailFragment(item.value.id)
        view.findNavController().navigate(direction)
    }

    fun bind(item: Item) {
        binding.item = item
        binding.executePendingBindings()
    }
}

private class SearchAdsDiffCallback : DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem.value.id == newItem.value.id
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem.value == newItem.value
    }
}