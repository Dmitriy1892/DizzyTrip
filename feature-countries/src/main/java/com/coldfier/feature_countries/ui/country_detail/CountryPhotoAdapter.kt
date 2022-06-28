package com.coldfier.feature_countries.ui.country_detail

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.coldfier.feature_countries.databinding.VpCountryPhotoBinding

class CountryPhotoAdapter : ListAdapter<Uri, CountryPhotoAdapter.CountryPhotoViewHolder>(
    CountryPhotoDiffUtil()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryPhotoViewHolder {
        return CountryPhotoViewHolder(
            VpCountryPhotoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CountryPhotoViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    class CountryPhotoDiffUtil : DiffUtil.ItemCallback<Uri>() {
        override fun areItemsTheSame(oldItem: Uri, newItem: Uri): Boolean {
            return oldItem.toString() == newItem.toString()
        }

        override fun areContentsTheSame(oldItem: Uri, newItem: Uri): Boolean {
            return oldItem.toString() == newItem.toString()
        }
    }

    inner class CountryPhotoViewHolder(
        private val binding: VpCountryPhotoBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(uri: Uri) {
            binding.ivCountryPhoto.load(uri)
        }
    }
}