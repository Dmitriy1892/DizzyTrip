package com.coldfier.feature_search_country.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.coldfier.core_mvi.changeText
import com.coldfier.core_mvi.changeVisibility
import com.coldfier.core_res.R
import com.coldfier.core_utils.di.ViewModelFactory
import com.coldfier.core_utils.di.findDependencies
import com.coldfier.core_utils.ui.observeWithLifecycle
import com.coldfier.core_utils.ui.setAfterTextChangedListenerWithDebounce
import com.coldfier.feature_search_country.SearchCountryDeps
import com.coldfier.feature_search_country.databinding.FragmentSearchCountryBinding
import com.coldfier.feature_search_country.di.DaggerSearchCountryComponent
import com.coldfier.feature_search_country.ui.mvi.SearchResult
import com.coldfier.feature_search_country.ui.mvi.SearchSideEffect
import com.coldfier.feature_search_country.ui.mvi.SearchState
import com.coldfier.feature_search_country.ui.mvi.SearchUiEvent
import timber.log.Timber
import javax.inject.Inject

class SearchCountryFragment : Fragment() {

    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory

    @Inject
    internal lateinit var deps: SearchCountryDeps

    private val viewModel: SearchCountryViewModel by viewModels { viewModelFactory }

    private var _binding: FragmentSearchCountryBinding? = null
    private val binding: FragmentSearchCountryBinding
        get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerSearchCountryComponent.factory()
            .create(context, findDependencies())
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchCountryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etSearch.setAfterTextChangedListenerWithDebounce(
            debounceMillis = 700L,
            coroutineScope = viewLifecycleOwner.lifecycleScope,
            actionBeforeDebounce = {
                if (binding.etSearch.text?.isNotBlank() == true) {
                    viewModel.sendUiEvent(
                        SearchUiEvent.ShowSearchLoadingState(
                            binding.etSearch.text?.toString() ?: ""
                        )
                    )
                } else {
                    viewModel.sendUiEvent(SearchUiEvent.SetEmptySearchRequest)
                }
            }
        ) {
            viewModel.sendUiEvent(SearchUiEvent.SearchCountryByName(it))
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
            viewModel.searchStateFlow.value.searchResult?.let { searchResult ->
                if (searchResult is SearchResult.Complete) {
                    viewModel.sendUiEvent(
                        SearchUiEvent.OpenSearchedCountry(searchResult.searchResult)
                    )
                }
            }
        }

        viewModel.searchStateFlow.observeWithLifecycle(::renderState)
        viewModel.searchSideEffect.observeWithLifecycle(::renderSideEffect)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun renderState(state: SearchState) {
        if (!binding.etSearch.isFocused) {
            binding.etSearch.changeText(state.searchRequest)
        }

        updateSearchResultView(state.searchResult)
    }

    private fun renderSideEffect(sideEffect: SearchSideEffect) {
        when (sideEffect) {
            is SearchSideEffect.OpenSearchedCountry -> {
                deps.foundCountryClicked(sideEffect.country)
            }
        }
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
        binding.pbSearch.startAnimation(animation)
        binding.tvSearchResult.startAnimation(animation)
        binding.tvSearchResult.changeVisibility(View.INVISIBLE)
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
        binding.pbSearch.startAnimation(animation)
        binding.tvSearchResult.startAnimation(animation)
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
                        Timber.tag(SearchCountryFragment::class.simpleName ?: "").e(e)
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
}