package com.example.storelocator.screen.results

import android.view.View
import androidx.lifecycle.*
import com.example.storelocator.R
import com.example.storelocator.manager.backpress.BackPressedListener
import com.example.storelocator.manager.location.LocationManager
import com.example.storelocator.manager.location.LocationRequestManager
import com.example.storelocator.manager.paysafe.stores.StoresManager
import com.example.storelocator.manager.permission.PermissionManager
import com.example.storelocator.manager.resources.ResourcesManager
import com.example.storelocator.model.location.GeoLocation
import com.example.storelocator.model.map.MapAreaZoomData
import com.example.storelocator.model.map.MapPinData
import com.example.storelocator.model.store.Store
import com.example.storelocator.screen.map.MapDataModel
import com.example.storelocator.util.SignalingLiveData
import com.example.storelocator.util.TextInfo
import com.example.storelocator.util.components.SnackBarComponent
import com.example.storelocator.util.components.ToastComponent
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

class StoresFragmentViewModel @AssistedInject constructor(
    @Assisted private val mapDataModel: MapDataModel,
    val storesAdapter: StoresAdapter,
    private val locationManager: LocationManager,
    private val locationRequestManager: LocationRequestManager,
    private val permissionManager: PermissionManager,
    private val storesManager: StoresManager,
    private val resourcesManager: ResourcesManager,
) : ViewModel(), BackPressedListener {

    private var stores: List<Store> = emptyList()
        set(value) {
            field = value
            storesAdapter.stores = value
        }

    private val showToastSignal = SignalingLiveData<ToastComponent>()
    val showToast: LiveData<ToastComponent> = showToastSignal

    private val showIndefiniteSnackbarLiveData = MutableLiveData<SnackBarComponent?>()
    val showIndefiniteSnackbar: LiveData<SnackBarComponent?> = showIndefiniteSnackbarLiveData

    private val resultsBottomSheetStateLiveData = MutableLiveData(BottomSheetBehavior.STATE_HIDDEN)
    val resultsBottomSheetState: LiveData<Int> = resultsBottomSheetStateLiveData

    private val resultsBottomSheetHideableLiveData = MutableLiveData(true)
    val resultsBottomSheetHideable: LiveData<Boolean> = resultsBottomSheetHideableLiveData

    private val selectedStoreLiveData = MutableLiveData<Store?>()
    val selectedStore: LiveData<Store?> = selectedStoreLiveData
    val selectedStoreTitle: LiveData<String> = selectedStoreLiveData.map { it?.name ?: "" }
    val selectedStoreSubtitle: LiveData<String> = selectedStoreLiveData.map {
        it?.let { "${it.address}, ${it.postalCode} ${it.city}" } ?: ""
    }

    private val detailBottomSheetStateLiveData = MutableLiveData(BottomSheetBehavior.STATE_HIDDEN)
    val detailBottomSheetState: LiveData<Int> = detailBottomSheetStateLiveData

    private val launchNaviSignal = SignalingLiveData<GeoLocation>()
    val launchNavi: LiveData<GeoLocation> = launchNaviSignal

    private val reloadButtonVisibleLiveData = MutableLiveData(false)
    val reloadButtonVisible: LiveData<Boolean> = reloadButtonVisibleLiveData

    private val loadingVisibleLiveData = MutableLiveData(false)
    val loadingVisible: LiveData<Boolean> = loadingVisibleLiveData

    val resultsBottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_EXPANDED || newState == BottomSheetBehavior.STATE_COLLAPSED) {
                resultsBottomSheetHideableLiveData.value = false
            }
            resultsBottomSheetStateLiveData.value = newState
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    val detailBottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                viewModelScope.launch { showResultList() }
            }
            detailBottomSheetStateLiveData.value = newState
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    init {
        viewModelScope.launch {
            ensureLocationReadyAndLoadData()
        }
        viewModelScope.launch {
            storesAdapter.showDetail.asFlow().collect {
                showDetail(it)
            }
        }
        viewModelScope.launch {
            mapDataModel.markerClick.collect { markerLocation ->
                val clickedStore =
                    stores.firstOrNull { markerLocation == GeoLocation(it.latitude, it.longitude) }
                if (clickedStore != null) {
                    showDetail(clickedStore)
                } else {
                    Timber.e("Clicked marker doesn't correspond to store")
                }
            }
        }
    }

    private suspend fun ensureLocationReadyAndLoadData() {
        showIndefiniteSnackbarLiveData.postValue(null)
        if (!locationRequestManager.isGpsEnabled) {
            val isGpsEnabled = locationRequestManager.requestGpsEnable()
            if (!isGpsEnabled) {
                showIndefiniteSnackbarLiveData.postValue(
                    SnackBarComponent(
                        text = TextInfo.from(R.string.allow_device_location_to_get_your_position),
                        action = TextInfo.from(R.string.allow),
                        callback = { viewModelScope.launch { ensureLocationReadyAndLoadData() } },
                        length = Snackbar.LENGTH_INDEFINITE
                    )
                )
                return
            }
        }
        if (!permissionManager.hasPermissionGranted(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            val hasPermission =
                permissionManager.requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)).grantedPermissions.isNotEmpty()
            if (!hasPermission) {
                showIndefiniteSnackbarLiveData.postValue(
                    SnackBarComponent(
                        text = TextInfo.from(R.string.allow_acces_to_your_location),
                        action = TextInfo.from(R.string.allow),
                        callback = { viewModelScope.launch { ensureLocationReadyAndLoadData() } },
                        length = Snackbar.LENGTH_INDEFINITE
                    )
                )
                return
            }
        }
        mapDataModel.enableMyPosition(true)
        loadData()
    }

    private suspend fun loadData() {
        val position = locationManager.getLastKnownPosition()
        if (position == null) {
            showIndefiniteSnackbarLiveData.postValue(
                SnackBarComponent(
                    text = TextInfo.from(R.string.your_location_is_not_available),
                    action = TextInfo.from(R.string.try_again),
                    callback = {
                        viewModelScope.launch {
                            showIndefiniteSnackbarLiveData.postValue(null)
                            ensureLocationReadyAndLoadData()
                        }
                    },
                    length = Snackbar.LENGTH_INDEFINITE
                )
            )
            return
        }

        try {
            loadingVisibleLiveData.value = true
            stores = storesManager.loadStoresInPosition(position)
        } catch (e: Exception) {
            Timber.e(e)
            showIndefiniteSnackbarLiveData.postValue(
                SnackBarComponent(
                    text = TextInfo.from(R.string.can_not_load_stores_around_you),
                    action = TextInfo.from(R.string.try_again),
                    callback = {
                        viewModelScope.launch {
                            showIndefiniteSnackbarLiveData.postValue(null)
                            ensureLocationReadyAndLoadData()
                        }
                    },
                    length = Snackbar.LENGTH_INDEFINITE
                )
            )
            return
        } finally {
            loadingVisibleLiveData.value = false
        }

        if (stores.isEmpty()) {
            showIndefiniteSnackbarLiveData.postValue(
                SnackBarComponent(
                    text = TextInfo.from(R.string.no_stores_around_you),
                    action = TextInfo.from(R.string.try_again),
                    callback = { viewModelScope.launch { ensureLocationReadyAndLoadData() } },
                    length = Snackbar.LENGTH_INDEFINITE
                )
            )
        } else {
            showResultList()
        }
    }

    private fun showDetail(store: Store) {
        selectedStoreLiveData.value = store
        hideResultList()
        detailBottomSheetStateLiveData.value = BottomSheetBehavior.STATE_EXPANDED
        mapDataModel.showPins(listOf(MapPinData(GeoLocation(store.latitude, store.longitude), store.name)))
        mapDataModel.zoomOnLocation(GeoLocation(store.latitude, store.longitude))
    }

    private fun hideResultList() {
        resultsBottomSheetHideableLiveData.value = true
        reloadButtonVisibleLiveData.value = false
        resultsBottomSheetStateLiveData.value = BottomSheetBehavior.STATE_HIDDEN
    }

    private suspend fun showResultList() {
        zoomOnAllStores()
        reloadButtonVisibleLiveData.value = true
        resultsBottomSheetStateLiveData.value = BottomSheetBehavior.STATE_COLLAPSED
    }

    private suspend fun zoomOnAllStores() {
        mapDataModel.showPins(stores.map { MapPinData(GeoLocation(it.latitude, it.longitude), it.name) })
        mapDataModel.zoomOnArea(
            MapAreaZoomData(
                locations = stores.map { GeoLocation(it.latitude, it.longitude) }.toMutableList()
                    .apply { locationManager.getLastKnownPosition()?.let { add(it) } },
                padding = resourcesManager.getDimensionPixelSize(R.dimen.map_padding)
            )
        )
    }

    fun navigateToCurrentlySelectedStore() {
        selectedStore.value?.let {
            launchNaviSignal.value = GeoLocation(it.latitude, it.longitude)
        }
    }

    fun reloadData() = viewModelScope.launch {
        hideResultList()
        mapDataModel.showPins(emptyList())
        loadData()
    }

    override fun onBackPressed(): Boolean {
        if (detailBottomSheetStateLiveData.value == BottomSheetBehavior.STATE_EXPANDED) {
            detailBottomSheetStateLiveData.value = BottomSheetBehavior.STATE_HIDDEN
            return true
        }
        if (resultsBottomSheetState.value == BottomSheetBehavior.STATE_EXPANDED) {
            resultsBottomSheetStateLiveData.value = BottomSheetBehavior.STATE_COLLAPSED
            return true
        }
        return false
    }

    @AssistedFactory
    interface Factory {
        fun create(mapDataModel: MapDataModel): StoresFragmentViewModel
    }

}