package com.coldfier.feature_countries.ui

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.ImageLoader
import coil.request.ImageRequest
import com.coldfier.core_data.repository.models.Country
import com.coldfier.core_mvi.changeImageDrawable
import com.coldfier.core_mvi.changeTextColor
import com.coldfier.core_mvi.changeVisibility
import com.coldfier.core_res.R
import com.coldfier.core_utils.di.DepsMap
import com.coldfier.core_utils.di.HasDependencies
import com.coldfier.core_utils.di.ViewModelFactory
import com.coldfier.core_utils.di.findDependencies
import com.coldfier.core_utils.ui.observeWithLifecycle
import com.coldfier.feature_countries.CountriesDeps
import com.coldfier.feature_countries.databinding.FragmentCountriesListBinding
import com.coldfier.feature_countries.di.CountriesComponent
import com.coldfier.feature_countries.di.DaggerCountriesComponent
import com.coldfier.feature_countries.ui.mvi.CountriesSideEffect
import com.coldfier.feature_countries.ui.mvi.CountriesState
import com.coldfier.feature_countries.ui.mvi.CountriesUiEvent
import com.coldfier.feature_search_country.SearchCountryDeps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CountriesListFragment : Fragment(), HasDependencies {

    private val searchCountryDeps = object : SearchCountryDeps {
        override fun foundCountryClicked(country: Country) {
            viewModel.sendUiEvent(CountriesUiEvent.OpenSearchedCountry(country))
        }
    }

    override val depsMap: DepsMap = mapOf(SearchCountryDeps::class.java to searchCountryDeps)

    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory

    @Inject
    internal lateinit var deps: CountriesDeps

    private val viewModel: CountriesListViewModel by viewModels { viewModelFactory }

    private var _binding: FragmentCountriesListBinding? = null
    private val binding: FragmentCountriesListBinding
        get() = _binding!!

    private var countriesAdapter: CountriesAdapter? = null

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
        countriesAdapter = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initClickers()
        initObservers()
    }

    private fun initViews() {
        countriesAdapter = CountriesAdapter(
            onItemClick = { viewModel.sendUiEvent(CountriesUiEvent.OpenCountryFullInfo(it)) },
            onBookmarkClick = { viewModel.sendUiEvent(CountriesUiEvent.ChangeIsBookmark(it)) },
            loadImage = { countryName, imageView, progressBar ->
                viewLifecycleOwner.lifecycleScope.launch {
                    val imageUri = withContext(Dispatchers.IO) {
                        viewModel.loadImageForCountry(countryName)
                    }

                    var repeatCounter = 0
                    val request = ImageRequest.Builder(requireContext())
                        .data(imageUri)
                        .listener(
                            onStart = {
                                showImagePlaceholder(imageView, progressBar, true)
                            },
                            onCancel = {
                                showImagePlaceholder(imageView, progressBar, false)
                            },
                            onError = { request, _ ->
                                if (repeatCounter >= 2) {
                                    showImagePlaceholder(imageView, progressBar, false)
                                } else {
                                    repeatCounter++
                                    ImageLoader(requireContext()).enqueue(request)
                                }
                            },
                            onSuccess = { _, result ->
                                imageView.setImageDrawable(result.drawable)
                                progressBar.visibility = View.GONE
                            }
                        )
                        .build()

                    ImageLoader(requireContext()).enqueue(request)
                }
            }
        )
        binding.rvCountries.adapter = countriesAdapter
    }

    private fun initClickers() {
        binding.cvUserAvatarContainer.setOnClickListener {
            viewModel.sendUiEvent(CountriesUiEvent.OpenUserProfile)
        }
    }

    private fun initObservers() {
        viewModel.countriesStateFlow.observeWithLifecycle(::renderState)
        viewModel.countriesSideEffectFlow.observeWithLifecycle(::renderSideEffect)
    }

    private fun renderState(countriesState: CountriesState) {
        /**
         * If need to show skeletons, block below shows that
         * and returns from renderScreen() function.
         *
         * It need for prevent incorrect data displaying
         */
        if (countriesState.isShowLoadingSkeleton) {
            showLoadingState()
            return
        } else {
            hideLoadingState()
            countriesAdapter?.showLoadedData(countriesState.countryShortList)
        }

        binding.pbLoading.changeVisibility(
            if (countriesState.isShowProgress) View.VISIBLE else View.GONE
        )

        if (countriesState.userAvatar != null) {
            binding.ivUserAvatar.changeImageDrawable(countriesState.userAvatar)
        } else {
            val emptyAvatar = ContextCompat.getDrawable(
                requireContext(), R.drawable.ic_user_avatar_placeholder
            )

            emptyAvatar?.let { binding.ivUserAvatar.changeImageDrawable(it) }
        }
    }

    private fun renderSideEffect(sideEffect: CountriesSideEffect) {
        when (sideEffect) {

            is CountriesSideEffect.ShowErrorDialog -> showErrorDialog()

            is CountriesSideEffect.NavigateToDetailScreen ->
                deps.navigateToCountryDetailFragment(sideEffect.country)
        }
    }

    private fun showLoadingState() {
        countriesAdapter?.showLoadingSkeletons()

        with(binding) {
            skeletonPlaceholder.changeVisibility(View.VISIBLE)
            searchContainer.changeVisibility(View.INVISIBLE)
            tvHead.changeTextColor(0xffffff)
            ivUserAvatar.changeVisibility(View.GONE)
            tvTitle.changeTextColor(0xffffff)
            showSkeletons(tvHead, cvUserAvatarContainer, skeletonPlaceholder, tvTitle)
        }
    }

    private fun hideLoadingState() {
        with(binding) {
            skeletonPlaceholder.changeVisibility(View.GONE)
            searchContainer.changeVisibility(View.VISIBLE)
            val textColor = ContextCompat.getColor(requireContext(), R.color.title_text_color)
            tvHead.changeTextColor(textColor)
            ivUserAvatar.changeVisibility(View.VISIBLE)
            tvTitle.changeTextColor(textColor)
            hideSkeletons(tvHead, cvUserAvatarContainer, skeletonPlaceholder, tvTitle)
        }
    }

    private fun showSkeletons(vararg views: View) {
        views.forEach { view ->
            val animDrawableTv = ContextCompat.getDrawable(
                binding.root.context,
                com.coldfier.feature_countries.R.drawable.gradient_list
            ) as AnimationDrawable
            view.foreground = animDrawableTv
            animDrawableTv.setEnterFadeDuration(500)
            animDrawableTv.setExitFadeDuration(500)
            animDrawableTv.start()
        }
    }

    private fun hideSkeletons(vararg views: View) {
        views.forEach { view -> view.foreground = null }
    }

    private fun showErrorDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.error_country_loading)
            .setCancelable(false)
            .setPositiveButton(R.string.error_dialog_button_ok) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showImagePlaceholder(
        imageView: ImageView, progressBar: ProgressBar, showProgress: Boolean
    ) {
        val drawable = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.bg_country_photo_placeholder
        )
        drawable?.let { imageView.changeImageDrawable(it) }
        progressBar.changeVisibility(if (showProgress) View.VISIBLE else View.GONE)
    }
}