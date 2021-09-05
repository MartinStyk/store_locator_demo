package com.example.storelocator.screen.results

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.storelocator.databinding.FragmentStoresBinding
import com.example.storelocator.manager.backpress.BackPressedManager
import com.example.storelocator.model.location.GeoLocation
import com.example.storelocator.screen.map.MapDataModel
import com.example.storelocator.util.components.SnackBarComponent
import com.example.storelocator.util.components.createIn
import com.example.storelocator.util.components.showIn
import com.example.storelocator.util.provideViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class StoresFragment : Fragment() {

    @Inject
    lateinit var factory: StoresFragmentViewModel.Factory

    @Inject
    lateinit var backPressedManager: BackPressedManager

    private val mapDataModel by viewModels<MapDataModel>()

    private lateinit var viewModel: StoresFragmentViewModel
    private lateinit var binding: FragmentStoresBinding
    private var snackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = provideViewModel { factory.create(mapDataModel) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentStoresBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        with(viewModel) {
            showToast.observe(viewLifecycleOwner) { it.showIn(requireContext()) }
            showIndefiniteSnackbar.observe(viewLifecycleOwner) { handleSnackbar(it) }
            launchNavi.observe(viewLifecycleOwner) { launchGoogleNavi(it) }
        }

        backPressedManager.registerBackPressedListener(viewModel)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        backPressedManager.unregisterBackPressedListener(viewModel)
    }

    private fun handleSnackbar(component: SnackBarComponent?) {
        snackbar?.dismiss()
        snackbar = null
        if (component != null) {
            snackbar = component.createIn(binding.root)
            snackbar?.show()
        }
    }

    private fun launchGoogleNavi(location: GeoLocation) {
        Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=${location.latitude},${location.longitude}")).apply {
            setPackage("com.google.android.apps.maps")
        }.takeIf { it.resolveActivity(requireContext().packageManager) != null }
            ?.let { startActivity(it) }
    }

}