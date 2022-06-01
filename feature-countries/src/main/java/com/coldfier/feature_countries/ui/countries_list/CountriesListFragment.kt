package com.coldfier.feature_countries.ui.countries_list

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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.ImageLoader
import coil.load
import coil.request.ImageRequest
import com.coldfier.core_data.domain.models.CountryShort
import com.coldfier.core_utils.di.ViewModelFactory
import com.coldfier.core_utils.di.findDependencies
import com.coldfier.core_utils.ui.observeInCoroutine
import com.coldfier.core_utils.ui.setAfterTextChangedListenerWithDebounce
import com.coldfier.feature_countries.R
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
            loadImage = { countryName, imageView ->
                viewLifecycleOwner.lifecycleScope.launch {
                    val imageUri = withContext(Dispatchers.IO) {
                        viewModel.loadImageForCountry(countryName)
                    }

                    val request = ImageRequest.Builder(requireContext())
                        .data(imageUri)
                        .target(imageView)
                        .placeholder(R.drawable.bg_country_photo_placeholder)
                        .error(R.drawable.bg_country_photo_placeholder)
                        .build()

                    val loader = ImageLoader.Builder(requireContext()).build()

                    loader.enqueue(request)
                }
            }
        )

        binding.cvUserAvatarContainer.setOnClickListener {
            viewModel.sendEvent(CountriesScreenEvent.OpenUserProfile)
        }

        binding.etSearch.setAfterTextChangedListenerWithDebounce(
            debounceMillis = 700L,
            coroutineScope = viewLifecycleOwner.lifecycleScope,
            actionBeforeDebounce = { viewModel.sendEvent(CountriesScreenEvent.CountrySearchLoading) }
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
                        && binding.tvSearchResult.text.isNotEmpty()
                        && binding.tvSearchResult.text != getString(R.string.no_search_result_text)
                    ) { showSearchResultView() }
                }
            }

        binding.tvSearchResult.setOnClickListener {

            val currentSearchResult = when (val state = viewModel.countriesScreenStateFlow.value) {
                is CountriesScreenState.Loading -> null
                is CountriesScreenState.Complete -> state.searchResult
                is CountriesScreenState.Error -> state.searchResult
            }

            currentSearchResult?.let { result ->
                if (result is SearchResult.Complete) {
                    viewModel.sendEvent(CountriesScreenEvent.OpenCountry(result.searchResult))
                }
            }
        }

        viewModel.navigateFlow.observeInCoroutine {
            // TODO
        }

        viewModel.countriesScreenStateFlow.observeInCoroutine { screenState ->
            when (screenState) {
                is CountriesScreenState.Loading -> {
                    showLoadingState()
                }

                is CountriesScreenState.Complete -> {
                    showCompleteState(screenState.countryShortList, screenState.searchResult)
                }

                is CountriesScreenState.Error -> {
                    showErrorState(screenState.message, screenState.searchResult)
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

    private fun showCompleteState(countryShortList: List<CountryShort>, searchResult: SearchResult?) {

        updateSearchResultView(searchResult)

        with(binding) {
            val textColor = ContextCompat
                .getColor(requireContext(), com.coldfier.core_res.R.color.title_text_color)
            val hintColor = ContextCompat
                .getColor(requireContext(), com.coldfier.core_res.R.color.text_hint_color)
            val searchDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_search)
            tvHead.setTextColor(textColor)
            ivUserAvatar.visibility = View.VISIBLE
            etSearch.setHintTextColor(hintColor)
            etSearch.setTextColor(textColor)
            etSearch.setCompoundDrawablesWithIntrinsicBounds(searchDrawable, null, null, null)
            tvTitle.setTextColor(textColor)
            hideSkeletons(tvHead, cvUserAvatarContainer, etSearch, tvTitle)
        }

        if (countryShortList.isEmpty()) {
            binding.viewNoCountries.visibility = View.VISIBLE
            binding.rvCountries.visibility = View.GONE
        } else {
            binding.viewNoCountries.visibility = View.GONE
            binding.rvCountries.visibility = View.VISIBLE
            (binding.rvCountries.adapter as CountriesAdapter).showLoadedData(countryShortList)
        }
    }

    private fun showErrorState(errorMessage: String, searchResult: SearchResult?) {

        updateSearchResultView(searchResult)

        with(binding) {
            val textColor = ContextCompat
                .getColor(requireContext(), com.coldfier.core_res.R.color.title_text_color)
            val hintColor = ContextCompat
                .getColor(requireContext(), com.coldfier.core_res.R.color.text_hint_color)
            val searchDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_search)
            tvHead.setTextColor(textColor)
            ivUserAvatar.visibility = View.VISIBLE
            etSearch.setHintTextColor(hintColor)
            etSearch.setTextColor(textColor)
            etSearch.setCompoundDrawables(searchDrawable, null, null, null)
            tvTitle.setTextColor(textColor)
            hideSkeletons(tvHead, cvUserAvatarContainer, etSearch, tvTitle)
        }

        binding.viewNoCountries.visibility = View.VISIBLE
        binding.rvCountries.visibility = View.GONE
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
                binding.tvSearchResult.text = searchResult.searchResult.name ?: getString(R.string.no_search_result_text)
                if (binding.tvSearchResult.visibility == View.INVISIBLE) showSearchResultView()
            }

            is SearchResult.Error -> {
                binding.pbSearch.visibility = View.GONE
                binding.tvSearchResult.text = getString(R.string.no_search_result_text)
                if (binding.tvSearchResult.visibility == View.INVISIBLE) showSearchResultView()
            }

            null -> {
                binding.pbSearch.visibility = View.GONE
                binding.tvSearchResult.text = getString(R.string.no_search_result_text)
                if (binding.tvSearchResult.visibility == View.VISIBLE) hideSearchResultView()
            }
        }
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
}