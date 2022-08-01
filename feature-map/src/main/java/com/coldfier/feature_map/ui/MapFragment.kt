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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.request.ImageRequest
import com.coldfier.core_data.repository.models.Country
import com.coldfier.core_mvi.changeVisibility
import com.coldfier.core_res.R
import com.coldfier.core_utils.di.DepsMap
import com.coldfier.core_utils.di.HasDependencies
import com.coldfier.core_utils.di.ViewModelFactory
import com.coldfier.core_utils.di.findDependencies
import com.coldfier.core_utils.ui.observeWithLifecycle
import com.coldfier.feature_map.databinding.FragmentMapBinding
import com.coldfier.feature_map.di.DaggerMapComponent
import com.coldfier.feature_map.di.MapComponent
import com.coldfier.feature_map.ui.mvi.MapSideEffect
import com.coldfier.feature_map.ui.mvi.MapState
import com.coldfier.feature_map.ui.mvi.MapUiEvent
import com.coldfier.feature_search_country.SearchCountryDeps
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
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
import kotlin.math.absoluteValue

class MapFragment : Fragment(), HasDependencies {

    private val searchCountryDeps = object : SearchCountryDeps {
        override fun foundCountryClicked(country: Country) {
            viewModel.sendUiEvent(MapUiEvent.FoundCountryClicked(country))
        }
    }

    override val depsMap: DepsMap = mapOf(SearchCountryDeps::class.java to searchCountryDeps)

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
        val event = if (deniedPermissions.isEmpty()) {
            MapUiEvent.PermissionsGranted
        } else {
            MapUiEvent.PermissionsDenied(deniedPermissions)
        }
        viewModel.sendUiEvent(event)
    }

    private var snackbar: Snackbar? = null

    @OptIn(FlowPreview::class)
    private val snapScrollDebounceFlow = callbackFlow {
        val helper = PagerSnapHelper()
        try {
            helper.attachToRecyclerView(binding.rvCountries)
        } catch (e: Exception) {
            Timber.tag(MapFragment::class.simpleName.toString()).e(e)
        }


        val callback = SnapOnScrollListener(
            helper, SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL,
            object : OnSnapPositionChangeListener {
                override fun onSnapPositionChange(position: Int) {
                    val countryName = adapter?.currentList?.getOrNull(position)
                    countryName?.let { country ->
                        trySend(MapUiEvent.CountryChosen(country))
                    }
                }
            }
        )
        snapOnScrollListener = callback
        binding.rvCountries.addOnScrollListener(callback)
        awaitClose { binding.rvCountries.clearOnScrollListeners() }
    }
        .distinctUntilChanged()
        .debounce(500)

    private var snapOnScrollListener: SnapOnScrollListener? = null

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

        mapPermissionsCallback.launch(viewModel.mapStateFlow.value.deniedPermissions.toTypedArray())

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

        snapScrollDebounceFlow.observeWithLifecycle { viewModel.sendUiEvent(it) }
        viewModel.mapStateFlow.observeWithLifecycle(::renderState)
        viewModel.mapSideEffectFlow.observeWithLifecycle(::renderSideEffect)
    }

    override fun onResume() {
        super.onResume()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

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

    private fun renderState(state: MapState) {

        binding.pbLoading.changeVisibility(if (state.isShowLoading) View.VISIBLE else View.GONE)
        binding.tvNoData.changeVisibility(if (state.isShowNoDataLoaded) View.VISIBLE else View.GONE)
        adapter?.submitList(state.countryList)


        if (checkMultiplePermissions(state.deniedPermissions).isNotEmpty())  {
            showErrorPermissionsSnackbar(checkMultiplePermissions(state.deniedPermissions))
        }

        binding.mapView.overlays.clear()
        getMyLocation()

        if (state.chosenCountry != null && !state.isCountrySearchLoading) {
            val lat = state.chosenCountry.lat
            val lon = state.chosenCountry.lon
            val zoomLevel = state.chosenCountry.zoom ?: 10.0
            val infoText = state.chosenCountry.name ?: ""

            if (lat != null && lon != null) {
                setLocation(GeoPoint(lat, lon), zoomLevel, infoText, false)
            }
        }

        if (state.chosenCountry != null) {
            val index = viewModel.mapStateFlow.value.countryList.indexOfFirst {
                it.name == state.chosenCountry.name
            }

            if (index != -1) {
                val recyclerPosition = (binding.rvCountries.layoutManager as LinearLayoutManager)
                    .findFirstVisibleItemPosition()

                if ((recyclerPosition - index).absoluteValue > 5 ) {
                    binding.rvCountries.clearOnScrollListeners()
                    binding.rvCountries.scrollToPosition(index - 1)

                    binding.rvCountries.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                            super.onScrollStateChanged(recyclerView, newState)
                            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                                binding.rvCountries.clearOnScrollListeners()
                                snapOnScrollListener?.let {
                                    binding.rvCountries.addOnScrollListener(it)
                                }

                            }
                        }
                    })
                }

                Handler(Looper.getMainLooper()).postDelayed({
                    binding.rvCountries.smoothScrollToPosition(index)
                }, 200)
            }

            hideSoftInputKeyboard()
        }
    }

    private fun renderSideEffect(effect: MapSideEffect) {
        when (effect) {

            is MapSideEffect.ShowErrorDialog -> showErrorDialog()

            is MapSideEffect.InitMap -> {
                initMap()
                if (viewModel.mapStateFlow.value.countryList.isEmpty()) getMyLocation()
            }
        }
    }

    private fun initMap() {
        Configuration.getInstance().load(
            requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext())
        )

        binding.mapView.setTileSource(TileSourceFactory.WIKIMEDIA)
        binding.mapView.setMultiTouchControls(true)
        binding.mapView.tileProvider.createTileCache()
        binding.mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
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

        if (!isMyLocation || viewModel.mapStateFlow.value.chosenCountry == null) {
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
}