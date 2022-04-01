package com.example.firebasescansamples.ui.vanillascan

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.camera.camera2.internal.compat.quirk.CameraNoResponseWhenEnablingFlashQuirk
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.example.firebasescansamples.CameraPermission
import com.example.firebasescansamples.databinding.FragmentVanillaScanBinding

class VanillaScanFragment : Fragment() {

    private var _binding: FragmentVanillaScanBinding? = null

    private lateinit var viewModel: VanillaScanViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val launcher = registerForActivityResult(
        CameraPermission.RequestContract(), ::onPermissionResult
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[VanillaScanViewModel::class.java]

        _binding = FragmentVanillaScanBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textNotifications
        viewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
            resumeBarcodeView()
        }

        if (CameraPermission.hasPermission(requireContext())) {
            resumeBarcodeView()
        } else {
            launcher.launch(Unit)
        }
        return root
    }
//
//    override fun onResume() {
//        super.onResume()
//        resumeBarcodeView()
//    }

    override fun onPause() {
        super.onPause()
        pauseBarcodeView()
    }

    private fun resumeBarcodeView() {
        if(!lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) return
        binding.barcodeView.resume()
        binding.barcodeView.decodeSingle(viewModel.getBarcodeCallback())
    }

    private fun pauseBarcodeView() {
        binding.barcodeView.pause()
    }

    private fun onPermissionResult(granted: Boolean) {
        if (granted) {
            resumeBarcodeView()
        } else {
            activity?.finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
