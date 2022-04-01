package com.example.firebasescansamples.ui.firebasescan

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.firebasescansamples.CameraPermission
import com.example.firebasescansamples.databinding.FragmentFirebaseScanBinding
import com.google.mlkit.vision.barcode.common.Barcode

class FirebaseScanFragment : Fragment() {

    private var _binding: FragmentFirebaseScanBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var codeScanner: CodeScanner
    private val launcher = registerForActivityResult(
        CameraPermission.RequestContract(), ::onPermissionResult
    )
    private lateinit var viewModel: FirebaseScanViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel =
            ViewModelProvider(this)[FirebaseScanViewModel::class.java]

        _binding = FragmentFirebaseScanBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        viewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        codeScanner = CodeScanner(requireActivity(), binding.previewView, ::onDetectCode)
        if (CameraPermission.hasPermission(requireContext())) {
            startCamera()
        } else {
            launcher.launch(Unit)
        }
        return root
    }

    private fun startCamera() {
        codeScanner.startCamera()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onPermissionResult(granted: Boolean) {
        if (granted) {
           startCamera()
        } else {
            activity?.finish()
        }
    }

    private fun onDetectCode(codes: List<Barcode>) {
        codes.forEach {
            Log.d("hogehoge", "scanned ${it.rawValue}")
            viewModel.updateText(it.rawValue ?: "")
        }
    }
}
