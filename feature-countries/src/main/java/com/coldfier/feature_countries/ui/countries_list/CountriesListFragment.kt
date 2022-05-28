package com.coldfier.feature_countries.ui.countries_list

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.coldfier.core_data.domain.models.CountryShort
import com.coldfier.core_utils.di.ViewModelFactory
import com.coldfier.core_utils.di.findDependencies
import com.coldfier.core_utils.ui.observeInCoroutine
import com.coldfier.feature_countries.databinding.FragmentCountriesListBinding
import com.coldfier.feature_countries.di.CountriesComponent
import com.coldfier.feature_countries.di.DaggerCountriesComponent
import javax.inject.Inject

class CountriesListFragment : Fragment() {

    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: CountriesListViewModel by viewModels { viewModelFactory }

    private var _binding: FragmentCountriesListBinding? = null
    private val binding: FragmentCountriesListBinding
        get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val component: CountriesComponent = DaggerCountriesComponent.builder()
            .deps(findDependencies())
            .context(context)
            .build()
        component.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCountriesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        binding.rvCountries.adapter = CountriesAdapter(
            onItemClick = { viewModel.sendEvent(CountriesScreenEvent.OpenCountryFullInfo(it)) },
            onBookmarkClick = { viewModel.sendEvent(CountriesScreenEvent.ChangeIsBookmark(it)) }
        )

        viewModel.navigateFlow.observeInCoroutine {
            // TODO
        }

        viewModel.countriesScreenStateFlow.observeInCoroutine { screenState ->
            when (screenState) {
                is CountriesScreenState.Loading -> {
                    showLoadingState()
                }

                is CountriesScreenState.Complete -> {
                    showCompleteState(screenState.countryShortList)
                }

                is CountriesScreenState.Error -> {
                    showErrorState()
                }
            }
        }
    }

    private fun showLoadingState() {
        // TODO
    }

    private fun showCompleteState(countryShortList: List<CountryShort>) {
        // TODO
        (binding.rvCountries.adapter as CountriesAdapter).submitList(countryShortList)
    }

    private fun showErrorState() {
        // TODO
    }

}