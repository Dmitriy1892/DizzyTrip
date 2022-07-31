package com.coldfier.feature_bookmarks.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.ImageLoader
import coil.request.ImageRequest
import com.coldfier.core_mvi.changeVisibility
import com.coldfier.core_utils.di.ViewModelFactory
import com.coldfier.core_utils.di.findDependencies
import com.coldfier.core_utils.ui.observeWithLifecycle
import com.coldfier.feature_bookmarks.BookmarksDeps
import com.coldfier.feature_bookmarks.databinding.FragmentBookmarksBinding
import com.coldfier.feature_bookmarks.di.DaggerBookmarksComponent
import com.coldfier.feature_bookmarks.ui.mvi.BookmarksSideEffect
import com.coldfier.feature_bookmarks.ui.mvi.BookmarksState
import com.coldfier.feature_bookmarks.ui.mvi.BookmarksUiEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BookmarksFragment : Fragment() {

    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory

    @Inject
    internal lateinit var deps: BookmarksDeps

    private val viewModel: BookmarksViewModel by viewModels { viewModelFactory }

    private var _binding: FragmentBookmarksBinding? = null
    private val binding: FragmentBookmarksBinding
        get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val component = DaggerBookmarksComponent.builder()
            .deps(findDependencies())
            .context(context)
            .build()

        component.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvBookmarks.adapter = BookmarksAdapter(
            onItemClick = { viewModel.sendUiEvent(BookmarksUiEvent.OpenCountryFullInfo(it)) },
            onBookmarkClick = { viewModel.sendUiEvent(BookmarksUiEvent.ChangeIsBookmark(it)) },
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

        viewModel.bookmarksStateFlow.observeWithLifecycle { renderState(it) }
        viewModel.bookmarksSideEffectFlow.observeWithLifecycle { renderSideEffect(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun renderState(state: BookmarksState) {

        binding.pbLoading.changeVisibility(if (state.isShowProgress) View.VISIBLE else View.GONE)

        binding.tvNoData.changeVisibility(
            if (!state.isShowProgress && state.countryShortList.isEmpty()) View.VISIBLE
            else View.GONE
        )

        binding.layoutContent.changeVisibility(
            if (state.countryShortList.isNotEmpty()) View.VISIBLE else View.GONE
        )

        (binding.rvBookmarks.adapter as BookmarksAdapter).submitList(state.countryShortList)
    }

    private fun renderSideEffect(sideEffect: BookmarksSideEffect) {
        when (sideEffect) {
            is BookmarksSideEffect.ShowErrorDialog -> showErrorDialog()
            is BookmarksSideEffect.NavigateToDetailScreen -> {
                deps.navigateToDetailScreen(sideEffect.country)
            }
        }
    }

    private fun showImagePlaceholder(
        imageView: ImageView, progressBar: ProgressBar, showProgress: Boolean
    ) {
        imageView.setImageResource(com.coldfier.core_res.R.drawable.bg_country_photo_placeholder)
        progressBar.visibility = if (showProgress) View.VISIBLE else View.GONE
    }

    private fun showErrorDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage(com.coldfier.core_res.R.string.error_country_loading)
            .setCancelable(false)
            .setPositiveButton(com.coldfier.core_res.R.string.error_dialog_button_ok) { dialog, _ -> dialog.dismiss() }
            .show()
    }
}