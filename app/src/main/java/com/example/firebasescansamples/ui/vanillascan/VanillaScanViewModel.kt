package com.example.firebasescansamples.ui.vanillascan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult

class VanillaScanViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val text: LiveData<String> = _text

    fun getBarcodeCallback(): BarcodeCallback {
        return object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                _text.value = result?.text ?: "null"
            }

            override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {}
        }
    }
}
