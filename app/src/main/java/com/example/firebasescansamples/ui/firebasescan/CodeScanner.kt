package com.example.firebasescansamples.ui.firebasescan

import androidx.activity.ComponentActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import java.util.concurrent.Executors

class CodeScanner(
    private val activity: ComponentActivity,
    private val previewView: PreviewView,
    callback: (List<Barcode>) -> Unit
) {
    private val workerExecutor = Executors.newSingleThreadExecutor()
    private val scanner = BarcodeScanning.getClient()
    private val analyzer = CodeAnalyzer(scanner, callback)

    init {
        activity.lifecycle.addObserver(
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_DESTROY) {
                    workerExecutor.shutdown()
                    scanner.close()
                }
            }
        )
    }

    fun startCamera() {
        ProcessCameraProvider.getInstance(activity).let {
            it.addListener({
                setup(it.get())
            }, ContextCompat.getMainExecutor(activity))
        }
    }


    private fun setup(provider: ProcessCameraProvider) {
        val prev = Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

        val analysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(workerExecutor, analyzer)
            }

        kotlin.runCatching {
            provider.unbindAll()
            provider.bindToLifecycle(
                activity, CameraSelector.DEFAULT_BACK_CAMERA, prev, analysis
            )
        }
    }
}
