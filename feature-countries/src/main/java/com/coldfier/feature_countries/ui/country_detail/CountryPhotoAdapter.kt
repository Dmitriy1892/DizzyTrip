package com.coldfier.feature_countries.ui.country_detail

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.load
import coil.request.ErrorResult
import coil.request.ImageRequest
import com.coldfier.feature_countries.R
import com.coldfier.feature_countries.databinding.VpCountryPhotoBinding

internal class CountryPhotoAdapter : ListAdapter<Uri, CountryPhotoAdapter.CountryPhotoViewHolder>(
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
            val ctx = binding.root.context
            var repeatCounter = 0
            val request = ImageRequest.Builder(ctx)
                .data(uri)
                .listener(
                    onStart = {
                        showPlaceholder(true)
                    },
                    onCancel = {
                        showPlaceholder(false)
                    },
                    onError = { request, _ ->
                        if (repeatCounter >= 2) {
                            showPlaceholder(false)
                        } else {
                            repeatCounter++
                            ImageLoader(ctx).enqueue(request)
                        }
                    },
                    onSuccess = { _, result ->
                        binding.ivCountryPhoto.setImageDrawable(result.drawable)
                        binding.pbLoading.visibility = View.GONE
                    }
                )
                .build()

            ImageLoader(ctx).enqueue(request)
        }

        private fun showPlaceholder(isLoading: Boolean) {
            val placeholder = ContextCompat.getDrawable(
                binding.root.context, com.coldfier.core_res.R.drawable.bg_country_photo_placeholder
            )

            binding.ivCountryPhoto.setImageDrawable(placeholder)
            binding.pbLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
}