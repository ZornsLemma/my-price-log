package app.zornslemma.mypricelog.feature.nfceimport

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import app.zornslemma.mypricelog.data.AppDatabase
import app.zornslemma.mypricelog.ui.common.userPreferencesStore
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NfceImportActivity : ComponentActivity() {
    private var busy by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) setScannerContent() else finish()
        }
        launcher.launch(Manifest.permission.CAMERA)
    }

    private fun setScannerContent() {
        setContent { ScannerView(activity = this, isBusy = busy, onScan = ::onScanPayload) }
    }

    private fun onScanPayload(payload: String) {
        if (busy) return
        busy = true
        CoroutineScope(Dispatchers.IO).launch {
            val result = runCatching {
                val resolved = NfceResolver.resolve(payload) ?: error("Invalid NFC-e QR")
                val html = NfceHtmlFetcher().fetch(resolved.url)
                val receipt = NfceHtmlParser().parse(resolved.url, html, resolved.accessKey)
                val dataSetId = applicationContext.userPreferencesStore.data.first().selectedDataSetId
                if (dataSetId == 0L) error("No collection selected")
                NfceImportRepository(
                        AppDatabase.getDatabase(applicationContext)
                    )
                    .importReceipt(dataSetId, receipt)
                    .getOrThrow()
            }

            launch(Dispatchers.Main) {
                result.onSuccess {
                    Toast.makeText(
                            this@NfceImportActivity,
                            "Imported ${it.importedCount} items • Created ${it.createdItems} new products • Updated ${it.updatedPrices} prices${if (it.skippedCount > 0) " • (${it.skippedCount} skipped)" else ""} • Store: ${it.storeName ?: "Unknown"} • ${it.datetimeText}",
                            Toast.LENGTH_LONG,
                        )
                        .show()
                    finish()
                }
                result.onFailure {
                    Toast.makeText(
                            this@NfceImportActivity,
                            if (it.message == "Already imported") "Already imported" else "Import failed: ${it.message}. Retry scan.",
                            Toast.LENGTH_LONG,
                        )
                        .show()
                    busy = false
                }
            }
        }
    }
}

@Composable
private fun ScannerView(activity: ComponentActivity, isBusy: Boolean, onScan: (String) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                val previewView = PreviewView(context)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                cameraProviderFuture.addListener(
                    {
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also { it.surfaceProvider = previewView.surfaceProvider }
                        val scanner =
                            BarcodeScanning.getClient(
                                BarcodeScannerOptions.Builder()
                                    .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                                    .build()
                            )
                        val analysis =
                            ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()
                        analysis.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                            val mediaImage = imageProxy.image
                            if (mediaImage == null) {
                                imageProxy.close()
                                return@setAnalyzer
                            }
                            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                            scanner.process(image)
                                .addOnSuccessListener { barcodes ->
                                    barcodes.firstOrNull()?.rawValue?.let { onScan(it) }
                                }
                                .addOnCompleteListener { imageProxy.close() }
                        }
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            activity,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            analysis,
                        )
                    },
                    ContextCompat.getMainExecutor(context),
                )
                previewView
            },
        )
        if (isBusy) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
