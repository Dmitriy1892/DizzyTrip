package com.coldfier.feature_countries.ui.country_detail

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.coldfier.core_data.repository.models.Advice
import com.coldfier.core_data.repository.models.AdviceType
import com.coldfier.core_utils.di.findDependencies
import com.coldfier.core_utils.ui.observeWithLifecycle
import com.coldfier.feature_countries.R
import com.coldfier.feature_countries.databinding.FragmentCountryDetailBinding
import com.coldfier.feature_countries.di.CountriesComponent
import com.coldfier.feature_countries.di.DaggerCountriesComponent
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.tabs.TabLayoutMediator
import javax.inject.Inject
import kotlin.text.StringBuilder

class CountryDetailFragment : Fragment() {

    @Inject
    internal lateinit var viewModelFactory:
            CountryDetailViewModel.CountryDetailViewModelFactory.CountryDetailViewModelAssistedFactory

    private val viewModel: CountryDetailViewModel by viewModels {
        viewModelFactory.create(countryArgs.country)
    }

    private val countryArgs: CountryDetailFragmentArgs by navArgs()

    private var _binding: FragmentCountryDetailBinding? = null
    private val binding: FragmentCountryDetailBinding
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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCountryDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val backPressedDispatcher = OnBackPressedDispatcher().apply {
        addCallback {
            findNavController().popBackStack()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vpImageHolder.adapter = CountryPhotoAdapter()
        TabLayoutMediator(binding.tabLayout, binding.vpImageHolder) { tab, position ->
        }.attach()

        binding.rvLanguages.adapter = CountryLanguagesAdapter()

        binding.rvVaccination.adapter = CountryVaccinationAdapter()

        binding.buttonBack.setOnClickListener {
            backPressedDispatcher.onBackPressed()
        }

        binding.buttonBookmark.setOnClickListener {
            // TODO - SAVE/DELETE BOOKMARK
        }

        viewModel.screenStateFlow.observeWithLifecycle {
            updateScreenState(it)
        }
    }

    private fun updateScreenState(screenState: CountryDetailScreenState) {
        with(binding) {
            (vpImageHolder.adapter as CountryPhotoAdapter).submitList(screenState.imageUriList)

            val country = screenState.country

            tvCountryName.text = country.fullName ?: ""
            tvMapLink.text = "${country.name}, ${country.continent}, ${country.iso2}"

            mapView.getMapAsync { googleMap ->
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    LatLng(country.lat ?: 0.0, country.lon ?: 0.0),
                    country.zoom?.toFloat() ?: 18.0F
                ))
            }

            (rvLanguages.adapter as CountryLanguagesAdapter).submitList(country.languages)

            val weatherList = country.weatherByMonth?.map {
                ViewWeather(
                    it.key,
                    it.value.temperatureAverage
                )
            } ?: listOf()

            viewWeatherDiagram.setWeather(weatherList)

            (rvVaccination.adapter as CountryVaccinationAdapter).submitList(country.vaccinations)

            val waterInfo = if (!country.waterFull.isNullOrBlank()) {
                country.waterShort?.let { "$it: ${country.waterFull}" } ?: country.waterFull!!
            } else {
                country.waterShort ?: getString(R.string.no_water_info)
            }

            tvWater.text = waterInfo

            tvCaAdvice.text = country.advices?.getOrDefault(AdviceType.CA, Advice())?.advise ?: ""

            tvUaAdvice.text = country.advices?.getOrDefault(AdviceType.UA, Advice())?.advise ?: ""

            tvVoltage.text =
                String.format(getString(R.string.voltage), country.voltage?.toString() ?: "-")

            val plugTypesStringBuilder = StringBuilder()
            country.plugTypes?.forEachIndexed { index, plugType ->
                val str = if ((index - 1) != (country.plugTypes?.size ?: 0)) {
                    plugType.name + ", "
                } else {
                    plugType.name
                }
                plugTypesStringBuilder.append(str)
            }
            val plugTypesString = plugTypesStringBuilder.toString()
            tvPlugTypes.text = String.format(getString(R.string.plug_types), plugTypesString)

            tvCurrencyName.text = "${country.currencyName}, ${country.currencyCode}"

            tvCallingCode.text = String
                .format(getString(R.string.calling_code), country.callingCode?.toString() ?: "")

            tvAmbulance.text = String
                .format(getString(R.string.ambulance), country.ambulanceNumber?.toString() ?: "")

            tvPolice.text = String
                .format(getString(R.string.police), country.policeNumber?.toString() ?: "")

            tvFire.text = String
                .format(getString(R.string.fire), country.fireNumber?.toString() ?: "")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}