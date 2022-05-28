package com.coldfier.feature_countries.ui.countries_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.coldfier.core_data.domain.models.CountryShort
import com.coldfier.feature_countries.databinding.RvItemCountryBinding

class CountriesAdapter(
    private val onItemClick: (CountryShort) -> Unit,
    private val onBookmarkClick: (CountryShort) -> Unit
): ListAdapter<CountryShort, CountriesAdapter.CountryHolder>(CountryDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryHolder {
        return CountryHolder(
            RvItemCountryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CountryHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class CountryHolder(
        private val binding: RvItemCountryBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CountryShort) {
            binding.tvCountryName.text = item.name ?: ""
            binding.ivCountryPhoto // TODO - LOAD PHOTO

            binding.root.setOnClickListener { onItemClick(item) }
            binding.fabBookmark.setOnClickListener { onBookmarkClick(item) }
        }
    }

    class CountryDiffUtil: DiffUtil.ItemCallback<CountryShort>() {
        override fun areItemsTheSame(oldItem: CountryShort, newItem: CountryShort): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: CountryShort, newItem: CountryShort): Boolean {
            return oldItem == newItem
        }

    }
}