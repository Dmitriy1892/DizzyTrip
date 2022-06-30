package com.coldfier.feature_countries.ui.countries_list

import android.graphics.drawable.AnimationDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.coldfier.core_data.repository.models.CountryShort
import com.coldfier.feature_countries.R
import com.coldfier.feature_countries.databinding.RvItemCountryBinding

class CountriesAdapter(
    private val onItemClick: (CountryShort) -> Unit,
    private val onBookmarkClick: (CountryShort) -> Unit,
    private val loadImage: (countryName: String, imageView: ImageView, progressBar: ProgressBar) -> Unit
): ListAdapter<CountryShort, CountriesAdapter.CountryHolder>(CountryDiffUtil()) {

    private var isNeedToShowSkeletons: Boolean = false

    private val skeletonList: List<CountryShort> by lazy {
        val list = mutableListOf<CountryShort>()
        repeat(20) {
            list.add(CountryShort())
        }
        list
    }

    fun showLoadingSkeletons() {
        isNeedToShowSkeletons = true
        if (currentList.isEmpty()) submitList(skeletonList)
        notifyDataSetChanged()
    }

    fun showLoadedData(data: List<CountryShort>) {
        isNeedToShowSkeletons = false
        submitList(data)
    }

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
            if (isNeedToShowSkeletons) showSkeleton() else showCountryData(item)
        }

        private fun showSkeleton() {
            binding.ivMarker.visibility = View.GONE

            binding.tvCountryName.text = "                               "
            binding.tvCountryName.foreground = ContextCompat.getDrawable(binding.root.context, R.drawable.gradient_list)
            val animDrawableTv = binding.tvCountryName.foreground as AnimationDrawable
            animDrawableTv.setEnterFadeDuration(500)
            animDrawableTv.setExitFadeDuration(500)
            animDrawableTv.start()

            binding.ivCountryPhoto.setImageResource(R.drawable.gradient_list)
            val animDrawableImage = binding.ivCountryPhoto.drawable as AnimationDrawable
            animDrawableImage.setEnterFadeDuration(500)
            animDrawableImage.setExitFadeDuration(500)
            animDrawableImage.start()

            binding.fabBookmark.visibility = View.GONE
        }

        private fun showCountryData(item: CountryShort) {
            binding.ivMarker.visibility = View.VISIBLE
            binding.tvCountryName.text = item.name ?: ""
            binding.tvCountryName.foreground = null

            loadImage.invoke(item.name ?: "", binding.ivCountryPhoto, binding.pbLoading)

            binding.fabBookmark.visibility = View.VISIBLE

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