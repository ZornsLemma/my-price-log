@file:OptIn(ExperimentalMaterial3Api::class)

package app.zornslemma.mypricelog.ui.screens.about

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import androidx.navigation.NavHostController
import app.zornslemma.mypricelog.BuildConfig
import app.zornslemma.mypricelog.R
import app.zornslemma.mypricelog.ui.components.BulletPoint
import app.zornslemma.mypricelog.ui.components.ClickableLink
import app.zornslemma.mypricelog.ui.fullScreenDialogHorizontalBorder
import app.zornslemma.mypricelog.ui.fullScreenDialogVerticalBorder

@Composable
fun AboutScreen(navController: NavHostController, onViewLegalClick: () -> Unit) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_about_app_name)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(R.string.content_description_back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    .padding(horizontal = fullScreenDialogHorizontalBorder)
                    .verticalScroll(rememberScrollState())
        ) {
            // We manually implement the vertical border so it is part of the scrollable region, not
            // something which reduces the size of the scrollable region. This feels a bit better to
            // me and (albeit not for the same reason) matches what we do in GeneralEditScreen().
            Spacer(modifier = Modifier.height(fullScreenDialogVerticalBorder))
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // App icon
                LauncherIcon(size = 96.dp)

                Spacer(modifier = Modifier.height(16.dp))

                // Version
                val version =
                    if (BuildConfig.DEBUG) {
                        getAppVersion() + " " + stringResource(R.string.debug_version_suffix)
                    } else {
                        getAppVersion()
                    }
                Text(text = version, style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Links card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        stringResource(R.string.title_resources),
                        style =
                            MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        stringResource(R.string.message_links_below_will_open_in_your_browser),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                    ClickableLink(
                        stringResource(R.string.title_user_manual),
                        "https://zornslemma.github.io/my-price-log-docs/",
                        showRawUrl = true,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ClickableLink(
                        stringResource(R.string.title_source_code_on_github),
                        "https://github.com/ZornsLemma/my-price-log",
                        showRawUrl = true,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Attributions card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // This is where we give credit for third-party components we are using in a
                    // readable way. The full legally compliant stuff which is not actually readable
                    // doesn't go here, it goes on LegalScreen().
                    Text(
                        stringResource(R.string.title_attributions),
                        style =
                            MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    BulletPoint(
                        stringResource(R.string.message_material_design_icons_google_apache_2_0)
                    )
                    /* For future reference:
                    BulletPoint("ExampleLibrary (MIT) — placeholder for future third-party library")
                    */
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Button to legal screen
            FilledTonalButton(onClick = onViewLegalClick, shape = MaterialTheme.shapes.small) {
                Text(stringResource(R.string.button_view_full_legal_information))
            }
            Spacer(modifier = Modifier.height(fullScreenDialogVerticalBorder))
        }
    }
}

private fun Drawable.toBitmap(width: Int, height: Int): Bitmap {
    val bitmap = createBitmap(width, height)
    val canvas = Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap
}

@Composable
private fun LauncherIcon(size: Dp = 120.dp) {
    val context = LocalContext.current
    val drawable = context.packageManager.getApplicationIcon(context.packageName)
    val sizePx = with(LocalDensity.current) { size.toPx().toInt() }
    val bitmap = drawable.toBitmap(sizePx, sizePx)
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = stringResource(R.string.content_description_app_icon),
        modifier = Modifier.size(size),
    )
}

@SuppressLint("ObsoleteSdkInt")
@Composable
private fun getAppVersion(): String {
    val context = LocalContext.current
    return remember {
        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            // pInfo.versionName is the human-readable version (e.g., "1.2.3")
            val versionName = pInfo.versionName
            // minSdk is currently 30 and we don't even use versionCode, but let's keep this logic
            // around for now. It's borderline possible we'll drop minSdk to 24 at some point.
            @Suppress("UnusedVariable")
            val versionCode =
                if (android.os.Build.VERSION.SDK_INT >= 28) {
                    pInfo.longVersionCode
                } else {
                    @Suppress("DEPRECATION") pInfo.versionCode.toLong()
                }
            context.getString(R.string.label_version, versionName) // could add " ($versionCode)"?
        } catch (e: PackageManager.NameNotFoundException) {
            "Version unknown"
        }
    }
}
