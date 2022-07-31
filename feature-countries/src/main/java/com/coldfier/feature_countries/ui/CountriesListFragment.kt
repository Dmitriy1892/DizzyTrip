package com.coldfier.feature_countries.ui

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.ImageLoader
import coil.request.ImageRequest
import com.coldfier.core_mvi.*
import com.coldfier.core_res.R
import com.coldfier.core_utils.di.ViewModelFactory
import com.coldfier.core_utils.di.findDependencies
import com.coldfier.core_utils.ui.observeWithLifecycle
import com.coldfier.core_utils.ui.setAfterTextChangedListenerWithDebounce
import com.coldfier.feature_countries.CountriesDeps
import com.coldfier.feature_countries.databinding.FragmentCountriesListBinding
import com.coldfier.feature_countries.di.CountriesComponent
import com.coldfier.feature_countries.di.DaggerCountriesComponent
import com.coldfier.feature_countries.ui.mvi.CountriesSideEffect
import com.coldfier.feature_countries.ui.mvi.CountriesState
import com.coldfier.feature_countries.ui.mvi.CountriesUiEvent
import com.coldfier.feature_countries.ui.mvi.SearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class CountriesListFragment : Fragment() {

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

        binding.etSearch.setAfterTextChangedListenerWithDebounce(
            debounceMillis = 700L,
            coroutineScope = viewLifecycleOwner.lifecycleScope,
            actionBeforeDebounce = {
                if (binding.etSearch.text?.isNotBlank() == true) {
                    viewModel.sendUiEvent(
                        CountriesUiEvent.ShowSearchLoadingState(
                            binding.etSearch.text?.toString() ?: ""
                        )
                    )
                } else {
                    viewModel.sendUiEvent(CountriesUiEvent.SetEmptySearchRequest)
                }
            }
        ) {
            viewModel.sendUiEvent(CountriesUiEvent.SearchCountryByName(it))
        }

        binding.etSearch.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    if (binding.tvSearchResult.visibility == View.VISIBLE) hideSearchResultView()

                    val imm: InputMethodManager = requireActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(
                        requireActivity().findViewById<View>(android.R.id.content).windowToken,
                        0
                    )
                } else if (
                    binding.tvSearchResult.visibility == View.INVISIBLE
                    && binding.tvSearchResult.text.isNotBlank()
                    && binding.tvSearchResult.text != getString(R.string.no_search_result_text)
                ) {
                    showSearchResultView()
                }
            }

        binding.tvSearchResult.setOnClickListener {
            viewModel.countriesStateFlow.value.searchResult?.let { searchResult ->
                if (searchResult is SearchResult.Complete) {
                    viewModel.sendUiEvent(
                        CountriesUiEvent.OpenSearchedCountry(searchResult.searchResult)
                    )
                }
            }
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

        binding.etSearch.changeText(countriesState.searchRequest)

        updateSearchResultView(countriesState.searchResult)
    }

    private fun renderSideEffect(sideEffect: CountriesSideEffect) {
        when (sideEffect) {

            is CountriesSideEffect.ShowErrorDialog -> showErrorDialog()

            is CountriesSideEffect.NavigateToDetailScreen ->
                deps.navigateToCountryDetailFragment(sideEffect.country)
        }
    }

    private fun showLoadingState() {
        if (binding.tvSearchResult.visibility == View.VISIBLE)  hideSearchResultView()

        countriesAdapter?.showLoadingSkeletons()

        with(binding) {
            tvHead.changeTextColor(0xffffff)
            ivUserAvatar.changeVisibility(View.GONE)
            etSearch.changeHintTextColor(0xffffff)
            etSearch.changeTextColor(0xffffff)
            etSearch.setCompoundDrawablesWithIntrinsicBounds(
                null, null, null, null
            )
            tvTitle.changeTextColor(0xffffff)
            showSkeletons(tvHead, cvUserAvatarContainer, etSearch, tvTitle)
        }
    }

    private fun hideLoadingState() {
        with(binding) {
            val textColor = ContextCompat.getColor(requireContext(), R.color.title_text_color)
            val hintColor = ContextCompat.getColor(requireContext(), R.color.text_hint_color)
            val searchDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_search)
            tvHead.changeTextColor(textColor)
            ivUserAvatar.changeVisibility(View.VISIBLE)
            etSearch.changeHintTextColor(hintColor)
            etSearch.changeTextColor(textColor)
            etSearch.setCompoundDrawablesWithIntrinsicBounds(
                searchDrawable, null, null, null
            )
            tvTitle.changeTextColor(textColor)
            hideSkeletons(tvHead, cvUserAvatarContainer, etSearch, tvTitle)
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

    private fun updateSearchResultView(searchResult: SearchResult?) {
        when (searchResult) {
            is SearchResult.Loading -> {
                binding.tvSearchResult.changeText("")
                if (binding.tvSearchResult.visibility == View.INVISIBLE) {
                    showSearchResultView()

                    try {
                        Handler(Looper.getMainLooper()).postDelayed(
                            { binding.pbSearch.changeVisibility(View.VISIBLE) },
                            500
                        )
                    } catch (e: Exception) {
                        Timber.tag(CountriesListFragment::class.simpleName ?: "").e(e)
                    }
                } else {
                    binding.pbSearch.changeVisibility(View.VISIBLE)
                }
            }

            is SearchResult.Complete -> {
                binding.pbSearch.changeVisibility(View.GONE)
                binding.tvSearchResult.changeText(
                    searchResult.searchResult.name ?: getString(R.string.no_search_result_text)
                )
                if (binding.tvSearchResult.visibility == View.INVISIBLE) showSearchResultView()
            }

            is SearchResult.Error -> {
                binding.pbSearch.changeVisibility(View.GONE)
                binding.tvSearchResult.changeText(getString(R.string.no_search_result_text))
                if (binding.tvSearchResult.visibility == View.INVISIBLE) showSearchResultView()
            }

            null -> {
                binding.pbSearch.changeVisibility(View.GONE)
                binding.tvSearchResult.changeText(getString(R.string.no_search_result_text))
                if (binding.tvSearchResult.visibility == View.VISIBLE) hideSearchResultView()
            }
        }
    }

    private fun showErrorDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.error_country_loading)
            .setCancelable(false)
            .setPositiveButton(R.string.error_dialog_button_ok) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showSearchResultView() {
        binding.tvSearchResult.changeVisibility(View.VISIBLE)

        val animation = TranslateAnimation(
            0f,
            0f,
            -7f - binding.tvSearchResult.height.toFloat(),
            0f
        )

        animation.duration = 500
        animation.fillAfter = true
        binding.tvSearchResult.startAnimation(animation)
    }

    private fun hideSearchResultView() {
        val animation = TranslateAnimation(
            0f,
            0f,
            0f,
            -7f - binding.tvSearchResult.height.toFloat()
        )

        animation.duration = 500
        animation.fillAfter = true
        binding.tvSearchResult.startAnimation(animation)
        binding.tvSearchResult.changeVisibility(View.INVISIBLE)
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