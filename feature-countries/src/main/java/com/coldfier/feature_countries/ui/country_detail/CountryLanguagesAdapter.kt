package com.coldfier.feature_countries.ui.country_detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.coldfier.core_data.repository.models.Language
import com.coldfier.feature_countries.databinding.RvItemLanguageBinding

class CountryLanguagesAdapter : ListAdapter<Language, CountryLanguagesAdapter.CountryLanguagesViewHolder>(
    CountryLanguageDiffUtil()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryLanguagesViewHolder {
        return CountryLanguagesViewHolder(
            RvItemLanguageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CountryLanguagesViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    class CountryLanguageDiffUtil : DiffUtil.ItemCallback<Language>() {
        override fun areItemsTheSame(oldItem: Language, newItem: Language): Boolean {
            return oldItem.language == newItem.language
        }

        override fun areContentsTheSame(oldItem: Language, newItem: Language): Boolean {
            return oldItem == newItem
        }
    }

    inner class CountryLanguagesViewHolder(
        private val  binding: RvItemLanguageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(language: Language) {
            binding.tvLanguage.text = language.language ?: ""
            language.isOfficial?.let {
                if (it) {
                    binding.tvIsOfficial.visibility = View.VISIBLE
                } else {
                    binding.tvIsOfficial.visibility = View.GONE
                }
            } ?: kotlin.run {
                binding.tvIsOfficial.visibility = View.GONE
            }
        }
    }
}