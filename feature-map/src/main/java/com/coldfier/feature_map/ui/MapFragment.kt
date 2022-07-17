package com.coldfier.feature_map.ui

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Rect
import android.location.Location
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.request.ImageRequest
import com.coldfier.core_res.R
import com.coldfier.core_utils.di.ViewModelFactory
import com.coldfier.core_utils.di.findDependencies
import com.coldfier.core_utils.ui.observeWithLifecycle
import com.coldfier.core_utils.ui.setAfterTextChangedListenerWithDebounce
import com.coldfier.feature_map.databinding.FragmentMapBinding
import com.coldfier.feature_map.di.DaggerMapComponent
import com.coldfier.feature_map.di.MapComponent
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow
import timber.log.Timber
import javax.inject.Inject

class MapFragment : Fragment() {

    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: MapViewModel by viewModels { viewModelFactory }

    private var _binding: FragmentMapBinding? = null
    private val binding: FragmentMapBinding
        get() = _binding!!

    private var adapter: MapCountriesAdapter? = null

    private val mapPermissionsCallback = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val deniedPermissions = result.filterValues { isGranted -> !isGranted }.keys
        val action = if (deniedPermissions.isEmpty()) {
            MapScreenAction.PermissionsGranted
        } else {
            MapScreenAction.PermissionsDenied(deniedPermissions)
        }
        viewModel.sendAction(action)
    }

    private var snackbar: Snackbar? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val component: MapComponent = DaggerMapComponent.builder()
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
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = MapCountriesAdapter { countryName, imageView, progressBar ->
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

        binding.rvCountries.adapter = adapter
        val helper = PagerSnapHelper()
        helper.attachToRecyclerView(binding.rvCountries)

        val snapScrollListener = SnapOnScrollListener(
            helper, SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL,
            object : OnSnapPositionChangeListener {
                override fun onSnapPositionChange(position: Int) {
                    val countryName = adapter?.currentList?.getOrNull(position)
                    countryName?.let { country ->
                        viewModel.sendAction(MapScreenAction.CountryChosen(country))
                    }
                }
            }
        )

        binding.rvCountries.addOnScrollListener(snapScrollListener)

        binding.rvCountries.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.set(15,0,15,0)
                view.layoutParams.width = binding.root.width * 8 / 10
            }
        })

        viewModel.mapScreenState.observeWithLifecycle {
            renderState(it)
        }

        binding.etSearch.setAfterTextChangedListenerWithDebounce(
            debounceMillis = 700L,
            coroutineScope = viewLifecycleOwner.lifecycleScope,
            actionBeforeDebounce = {
                if (binding.etSearch.text?.isNotBlank() == true) {
                    viewModel.sendAction(
                        MapScreenAction.ShowSearchLoadingState(
                            binding.etSearch.text?.toString() ?: ""
                        )
                    )
                } else {
                    viewModel.sendAction(MapScreenAction.SetEmptySearchRequest)
                }
            }
        ) {
            viewModel.sendAction(MapScreenAction.SearchCountryByName(it))
        }

        binding.etSearch.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            when {
                !hasFocus -> {
                    if (binding.tvSearchResult.visibility == View.VISIBLE) hideSearchResultView()
                    hideSoftInputKeyboard()
                }

                binding.tvSearchResult.visibility == View.INVISIBLE
                        && binding.tvSearchResult.text.isNotBlank()
                        && binding.tvSearchResult.text != getString(R.string.no_search_result_text) -> {
                    showSearchResultView()
                }
            }
        }

        binding.tvSearchResult.setOnClickListener {
            viewModel.mapScreenState.value.searchResult?.let { searchResult ->
                if (searchResult is SearchResult.Complete) {
                    val index = viewModel.mapScreenState.value.countryList.indexOfFirst {
                        it.name == searchResult.searchResult.name
                    }

                    if (index != -1) {
                        binding.rvCountries.scrollToPosition(index-1)
                        Handler(Looper.getMainLooper()).postDelayed({
                            binding.rvCountries.smoothScrollToPosition(index)
                        }, 200)
                    }


                    hideSoftInputKeyboard()
                    binding.etSearch.setText(searchResult.searchResult.name)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onPause() {
        super.onPause()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        adapter = null
    }

    private fun renderState(state: MapScreenState) {
        binding.pbLoading.visibility = if (state.isShowLoading) View.VISIBLE else View.GONE
        binding.tvNoData.visibility = if (state.isShowNoDataLoaded) View.VISIBLE else View.GONE
        adapter?.submitList(state.countryList)

        when {
            checkMultiplePermissions(state.deniedPermissions).isNotEmpty() -> {
                showErrorPermissionsSnackbar(checkMultiplePermissions(state.deniedPermissions))
            }

            state.isNeedToInitMap -> {
                initMap()
                if (state.countryList.isEmpty()) getMyLocation()
            }
        }

        if (state.errorDialogMessage != null) showErrorDialog()

        binding.mapView.overlays.clear()
        getMyLocation()

        if (state.chosenCountry != null && !state.isShowCountrySearchLoading) {
            val lat = state.chosenCountry.lat
            val lon = state.chosenCountry.lon
            val zoomLevel = state.chosenCountry.zoom ?: 10.0
            val infoText = state.chosenCountry.name ?: ""

            if (lat != null && lon != null) {
                setLocation(GeoPoint(lat, lon), zoomLevel, infoText, false)
            }
        }

        if (state.searchRequest != binding.etSearch.text.toString()) {
            binding.etSearch.setText(state.searchRequest)
        }

        updateSearchResultView(state.searchResult)

    }

    private fun initMap() {
        Configuration.getInstance().load(
            requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext())
        )

        binding.mapView.setTileSource(TileSourceFactory.WIKIMEDIA)
        binding.mapView.setMultiTouchControls(true)
        binding.mapView.tileProvider.createTileCache()
        binding.mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)

        viewModel.sendAction(MapScreenAction.MapInitialized)
    }

    private fun getMyLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
            setLocation(
                geoPoint = GeoPoint(
                    location?.latitude ?: 0.0,
                    location?.longitude ?: 0.0,
                ),
                zoomLevel = 16.0,
                infoText = getString(com.coldfier.feature_map.R.string.my_location_text),
                true
            )
        }
    }

    private fun setLocation(
        geoPoint: GeoPoint, zoomLevel: Double, infoText: String, isMyLocation: Boolean
    ) {
        val iconRes = if (isMyLocation) R.drawable.ic_user_location_marker
            else R.drawable.ic_location_marker_filled

        val mapMarker = Marker(binding.mapView).apply {
            position = geoPoint
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            icon = ContextCompat.getDrawable(requireContext(), iconRes)
        }

        val infoWindowResId = com.coldfier.feature_map.R.layout.map_info_window
        val infoWindow = MarkerInfoWindow(infoWindowResId, binding.mapView)
        mapMarker.snippet = infoText

        mapMarker.setInfoWindow(infoWindow)
        mapMarker.setOnMarkerClickListener { marker, mapView ->
            if (infoWindow.isOpen) {
                InfoWindow.closeAllInfoWindowsOn(mapView)
            } else {
                marker.showInfoWindow()
            }

            true
        }

        binding.mapView.overlays.add(mapMarker)

        if (!isMyLocation || viewModel.mapScreenState.value.chosenCountry == null) {
            val mapController = binding.mapView.controller
            val calculatedZoom = if (zoomLevel > 10.0) 10.0 else zoomLevel
            mapController.animateTo(geoPoint, calculatedZoom, 1000)
        }
    }

    private fun showErrorPermissionsSnackbar(deniedPermissions: Set<String>) {
        if (snackbar?.isShown != true) {
            snackbar = Snackbar.make(
                binding.root, R.string.permission_error_message, Snackbar.LENGTH_INDEFINITE
            ).apply {
                setAction(R.string.snackbar_ok_button) {
                    requestPermissions(deniedPermissions)
                    dismiss()
                }
                animationMode = Snackbar.ANIMATION_MODE_SLIDE
            }

            snackbar?.show()
        }
    }

    private fun showErrorDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.error_country_loading)
            .setCancelable(false)
            .setPositiveButton(R.string.error_dialog_button_ok) { dialog, _ ->
                dialog.dismiss()
                viewModel.sendAction(MapScreenAction.ErrorDialogClosed)
            }
            .show()
    }

    /**
     * @return set of denied permissions
     * If all permissions granted,
     * @return empty set
     */
    private fun checkMultiplePermissions(permissions: Set<String>): Set<String> {
        return permissions.filterTo(HashSet()) { !checkPermission(it) }
    }

    private fun checkPermission(permission: String): Boolean =
        ContextCompat.checkSelfPermission(requireContext(), permission) ==
                PackageManager.PERMISSION_GRANTED

    private fun requestPermissions(permissions: Set<String>) {
        mapPermissionsCallback.launch(permissions.toTypedArray())
    }

    private fun showSearchResultView() {
        binding.tvSearchResult.visibility = View.VISIBLE

        val animation = TranslateAnimation(
            0f,
            0f,
            -6f - binding.tvSearchResult.height.toFloat(),
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
            -6f - binding.tvSearchResult.height.toFloat()
        )

        animation.duration = 500
        animation.fillAfter = true
        binding.tvSearchResult.startAnimation(animation)
        binding.tvSearchResult.visibility = View.INVISIBLE
    }

    private fun showImagePlaceholder(
        imageView: ImageView, progressBar: ProgressBar, showProgress: Boolean
    ) {
        imageView.setImageResource(R.drawable.bg_country_photo_placeholder)
        progressBar.visibility = if (showProgress) View.VISIBLE else View.GONE
    }

    private fun hideSoftInputKeyboard() {
        val imm: InputMethodManager = requireActivity()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(
            requireActivity().findViewById<View>(android.R.id.content).windowToken,
            0
        )
    }

    private fun updateSearchResultView(searchResult: SearchResult?) {
        when (searchResult) {
            is SearchResult.Loading -> {
                if (binding.tvSearchResult.text == binding.etSearch.text.toString()) return

                binding.tvSearchResult.text = null
                if (binding.tvSearchResult.visibility == View.INVISIBLE) {
                    showSearchResultView()

                    try {
                        Handler(Looper.getMainLooper()).postDelayed(
                            { binding.pbSearch.visibility = View.VISIBLE }, 500
                        )
                    } catch (e: Exception) {
                        Timber.tag(MapFragment::class.simpleName ?: "").e(e)
                    }
                } else {
                    binding.pbSearch.visibility = View.VISIBLE
                }
            }

            is SearchResult.Complete -> {
                binding.pbSearch.visibility = View.GONE

                binding.tvSearchResult.text = searchResult.searchResult.name
                    ?: getString(R.string.no_search_result_text)

                if (binding.tvSearchResult.visibility == View.INVISIBLE
                    && binding.etSearch.text.toString() != binding.tvSearchResult.text.toString()
                ) {
                    showSearchResultView()
                } else if (
                    binding.etSearch.text.toString() == binding.tvSearchResult.text.toString()
                ) {
                    binding.etSearch.clearFocus()
                    if (binding.tvSearchResult.visibility == View.VISIBLE) hideSearchResultView()
                }
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
}