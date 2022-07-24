package com.coldfier.feature_bookmarks.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.coldfier.core_data.repository.models.CountryShort
import com.coldfier.core_res.R
import com.coldfier.feature_bookmarks.databinding.RvItemBookmarkBinding

internal class BookmarksAdapter(
    val onItemClick: (CountryShort) -> Unit,
    private val onBookmarkClick: (CountryShort) -> Unit,
    val loadImage: (countryName: String, imageView: ImageView, progressBar: ProgressBar) -> Unit
) : ListAdapter<CountryShort, BookmarksAdapter.BookmarkViewHolder>(BookmarkDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        return BookmarkViewHolder(
            RvItemBookmarkBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    internal inner class BookmarkViewHolder(
        private val binding: RvItemBookmarkBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(countryShort: CountryShort) {
            binding.tvCountryName.text = countryShort.name ?: ""
            loadImage(countryShort.name ?: "", binding.ivCountryPhoto, binding.pbLoading)

            binding.root.setOnClickListener { onItemClick(countryShort) }
            val imageRes =
                if (countryShort.isAddedToBookmark == true) R.drawable.ic_bookmark_on
                else R.drawable.ic_bookmark_off

            binding.fabBookmark.setImageResource(imageRes)
            binding.fabBookmark.setOnClickListener {
                onBookmarkClick(countryShort)
            }
        }
    }

    internal class BookmarkDiffUtil : DiffUtil.ItemCallback<CountryShort>() {
        override fun areItemsTheSame(oldItem: CountryShort, newItem: CountryShort): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: CountryShort, newItem: CountryShort): Boolean {
            return oldItem == newItem
        }
    }
}