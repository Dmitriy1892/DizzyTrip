package com.coldfier.feature_map.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.coldfier.core_data.repository.models.CountryShort
import com.coldfier.feature_map.databinding.RvItemCountryBinding

internal class MapCountriesAdapter(
    private val loadImage: (countryName: String, imageView: ImageView, progressBar: ProgressBar) -> Unit,
) : ListAdapter<CountryShort, MapCountriesAdapter.MapCountryViewHolder>(MapCountriesDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapCountryViewHolder {
        return MapCountryViewHolder(
            RvItemCountryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MapCountryViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    internal inner class MapCountryViewHolder(
        private val binding: RvItemCountryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(country: CountryShort) {
            binding.tvCountryName.text = country.name ?: ""
            loadImage(country.name ?: "", binding.ivCountryPhoto, binding.pbLoading)
        }
    }

    internal class MapCountriesDiffUtil : DiffUtil.ItemCallback<CountryShort>() {
        override fun areItemsTheSame(oldItem: CountryShort, newItem: CountryShort): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: CountryShort, newItem: CountryShort): Boolean {
            return oldItem == newItem
        }
    }
}