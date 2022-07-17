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
import com.coldfier.feature_countries.R
import com.coldfier.core_utils.di.ViewModelFactory
import com.coldfier.core_utils.di.findDependencies
import com.coldfier.core_utils.ui.observeWithLifecycle
import com.coldfier.core_utils.ui.setAfterTextChangedListenerWithDebounce
import com.coldfier.feature_countries.CountriesDeps
import com.coldfier.feature_countries.databinding.FragmentCountriesListBinding
import com.coldfier.feature_countries.di.CountriesComponent
import com.coldfier.feature_countries.di.DaggerCountriesComponent
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
            onBookmarkClick = { viewModel.sendEvent(CountriesScreenEvent.ChangeIsBookmark(it)) },
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

        binding.cvUserAvatarContainer.setOnClickListener {
            viewModel.sendEvent(CountriesScreenEvent.OpenUserProfile)
        }

        binding.etSearch.setAfterTextChangedListenerWithDebounce(
            debounceMillis = 700L,
            coroutineScope = viewLifecycleOwner.lifecycleScope,
            actionBeforeDebounce = {
                if (binding.etSearch.text?.isNotBlank() == true) {
                    viewModel.sendEvent(
                        CountriesScreenEvent.ShowSearchLoadingState(
                            binding.etSearch.text?.toString() ?: ""
                        )
                    )
                } else {
                    viewModel.sendEvent(CountriesScreenEvent.SetEmptySearchRequest)
                }
            }
        ) {
            viewModel.sendEvent(CountriesScreenEvent.SearchCountryByName(it))
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
                } else {
                    if (
                        binding.tvSearchResult.visibility == View.INVISIBLE
                        && binding.tvSearchResult.text.isNotBlank()
                        && binding.tvSearchResult.text != getString(com.coldfier.core_res.R.string.no_search_result_text)
                    ) { showSearchResultView() }
                }
            }

        binding.tvSearchResult.setOnClickListener {

            viewModel.countriesScreenStateFlow.value.searchResult?.let { searchResult ->
                if (searchResult is SearchResult.Complete) {
                    viewModel.sendEvent(
                        CountriesScreenEvent.OpenSearchedCountry(searchResult.searchResult)
                    )
                }
            }
        }

        viewModel.countriesScreenStateFlow.observeWithLifecycle { screenState ->
            renderScreen(screenState)
        }
    }

    private fun renderScreen(screenState: CountriesScreenState) {
        with(binding) {

            /**
             * If need to show skeletons, block below shows that
             * and returns from renderScreen() function.
             *
             * It need for prevent incorrect data displaying
             */
            if (screenState.isShowLoadingSkeleton) {
                showLoadingState()
                return
            } else {
                hideLoadingState()
            }

            screenState.userAvatar?.let {
                ivUserAvatar.setImageDrawable(it)
            } ?: ivUserAvatar.setImageResource(
                com.coldfier.core_res.R.drawable.ic_user_avatar_placeholder
            )

            if (
                screenState.searchRequest != etSearch.text.toString()
            ) {
                etSearch.setText(screenState.searchRequest)
            }

            updateSearchResultView(screenState.searchResult)

            (rvCountries.adapter as CountriesAdapter).submitList(screenState.countryShortList)

            screenState.errorDialogMessage?.let {
                showErrorDialog()
            }

            when (val navigationState = screenState.navigationState) {
                is NavigationState.None -> {
                    pbLoading.visibility = View.GONE
                }

                is NavigationState.Loading -> {
                    pbLoading.visibility = View.VISIBLE
                }

                is NavigationState.UserProfileScreen -> {
                    pbLoading.visibility = View.GONE
                    // TODO - ADD NAVIGATION LOGIC AFTER USER PROFILE SCREEN IMPLEMENTATION
                    viewModel.sendEvent(CountriesScreenEvent.NavigationComplete)
                }

                is NavigationState.CountryDetailScreen -> {
                    pbLoading.visibility = View.GONE
                    deps.navigateToCountryDetailFragment(navigationState.country)
                    viewModel.sendEvent(CountriesScreenEvent.NavigationComplete)
                }
            }
        }
    }

    private fun showLoadingState() {
        if (binding.tvSearchResult.visibility == View.VISIBLE)  hideSearchResultView()

        (binding.rvCountries.adapter as CountriesAdapter).showLoadingSkeletons()

        with(binding) {
            tvHead.setTextColor(0xffffff)
            ivUserAvatar.visibility = View.GONE
            etSearch.setHintTextColor(0xffffff)
            etSearch.setTextColor(0xffffff)
            etSearch.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            tvTitle.setTextColor(0xffffff)
            showSkeletons(tvHead, cvUserAvatarContainer, etSearch, tvTitle)
        }
    }

    private fun hideLoadingState() {
        (binding.rvCountries.adapter as CountriesAdapter).showLoadedData(viewModel.countriesScreenStateFlow.value.countryShortList)

        with(binding) {
            val textColor = ContextCompat
                .getColor(requireContext(), com.coldfier.core_res.R.color.title_text_color)
            val hintColor = ContextCompat
                .getColor(requireContext(), com.coldfier.core_res.R.color.text_hint_color)
            val searchDrawable = ContextCompat.getDrawable(requireContext(), com.coldfier.core_res.R.drawable.ic_search)
            tvHead.setTextColor(textColor)
            ivUserAvatar.visibility = View.VISIBLE
            etSearch.setHintTextColor(hintColor)
            etSearch.setTextColor(textColor)
            etSearch.setCompoundDrawablesWithIntrinsicBounds(searchDrawable, null, null, null)
            tvTitle.setTextColor(textColor)
            hideSkeletons(tvHead, cvUserAvatarContainer, etSearch, tvTitle)
        }
    }

    private fun showSkeletons(vararg views: View) {
        views.forEach { view ->
            val animDrawableTv = ContextCompat
                .getDrawable(binding.root.context, R.drawable.gradient_list) as AnimationDrawable
            view.foreground = animDrawableTv
            animDrawableTv.setEnterFadeDuration(500)
            animDrawableTv.setExitFadeDuration(500)
            animDrawableTv.start()
        }
    }

    private fun hideSkeletons(vararg views: View) {
        views.forEach { view ->
            view.foreground = null
        }
    }

    private fun updateSearchResultView(searchResult: SearchResult?) {
        when (searchResult) {
            is SearchResult.Loading -> {
                binding.tvSearchResult.text = null
                if (binding.tvSearchResult.visibility == View.INVISIBLE) {
                    showSearchResultView()

                    try {
                        Handler(Looper.getMainLooper()).postDelayed({ binding.pbSearch.visibility = View.VISIBLE }, 500)
                    } catch (e: Exception) {
                        Timber.tag(CountriesListFragment::class.simpleName ?: "").e(e)
                    }
                } else {
                    binding.pbSearch.visibility = View.VISIBLE
                }
            }

            is SearchResult.Complete -> {
                binding.pbSearch.visibility = View.GONE
                binding.tvSearchResult.text = searchResult.searchResult.name ?: getString(com.coldfier.core_res.R.string.no_search_result_text)
                if (binding.tvSearchResult.visibility == View.INVISIBLE) showSearchResultView()
            }

            is SearchResult.Error -> {
                binding.pbSearch.visibility = View.GONE
                binding.tvSearchResult.text = getString(com.coldfier.core_res.R.string.no_search_result_text)
                if (binding.tvSearchResult.visibility == View.INVISIBLE) showSearchResultView()
            }

            null -> {
                binding.pbSearch.visibility = View.GONE
                binding.tvSearchResult.text = getString(com.coldfier.core_res.R.string.no_search_result_text)
                if (binding.tvSearchResult.visibility == View.VISIBLE) hideSearchResultView()
            }
        }
    }

    private fun showErrorDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage(com.coldfier.core_res.R.string.error_country_loading)
            .setCancelable(false)
            .setPositiveButton(com.coldfier.core_res.R.string.error_dialog_button_ok) { dialog, _ ->
                dialog.dismiss()
                viewModel.sendEvent(CountriesScreenEvent.ErrorDialogClosed)
            }
            .show()
    }

    private fun showSearchResultView() {
        binding.tvSearchResult.visibility = View.VISIBLE

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
        binding.tvSearchResult.visibility = View.INVISIBLE
    }

    private fun showImagePlaceholder(
        imageView: ImageView, progressBar: ProgressBar, showProgress: Boolean
    ) {
        imageView.setImageResource(com.coldfier.core_res.R.drawable.bg_country_photo_placeholder)
        progressBar.visibility = if (showProgress) View.VISIBLE else View.GONE
    }
}