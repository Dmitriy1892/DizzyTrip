package com.coldfier.feature_countries.ui.country_detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.coldfier.core_data.repository.models.Vaccination
import com.coldfier.feature_countries.databinding.RvItemVaccinationBinding

class CountryVaccinationAdapter : ListAdapter<Vaccination, CountryVaccinationAdapter.CountryVaccinationHolder>(
    CountryVaccinationDiffUtil()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryVaccinationHolder {
        return CountryVaccinationHolder(
            RvItemVaccinationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CountryVaccinationHolder, position: Int) {
        holder.bind(currentList[position])
    }

    class CountryVaccinationDiffUtil : DiffUtil.ItemCallback<Vaccination>() {
        override fun areItemsTheSame(oldItem: Vaccination, newItem: Vaccination): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Vaccination, newItem: Vaccination): Boolean {
            return oldItem == newItem
        }
    }

    inner class CountryVaccinationHolder(
        private val binding: RvItemVaccinationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(vaccination: Vaccination) {
            binding.tvVaccinationName.text = vaccination.name ?: ""
            binding.tvVaccinationMessage.text = vaccination.message ?: ""
        }
    }
}