@file:OptIn(ExperimentalMaterial3Api::class)

package app.zornslemma.mypricelog.ui.screens.legal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import app.zornslemma.mypricelog.R
import app.zornslemma.mypricelog.ui.copyrightSymbol
import app.zornslemma.mypricelog.ui.emDash
import app.zornslemma.mypricelog.ui.fullScreenDialogHorizontalBorder
import app.zornslemma.mypricelog.ui.fullScreenDialogVerticalBorder

@Composable
fun LegalScreen(navController: NavHostController, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_legal_information)) },
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
                modifier
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    .padding(horizontal = fullScreenDialogHorizontalBorder)
        ) {
            // We manually implement the vertical border so it is part of the scrollable region, not
            // something which reduces the size of the scrollable region. This feels a bit better to
            // me and (albeit not for the same reason) matches what we do in GeneralEditScreen().
            Spacer(modifier = Modifier.height(fullScreenDialogVerticalBorder))

            // Our license
            Text(
                text = stringResource(R.string.title_app_name_license),
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Copyright $copyrightSymbol 2025 Steven Flintham",
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(modifier = Modifier.height(8.dp))
            LicenseText(
                // We do not extract this string and allow it to be translated. It's legal text and
                // if it does get localized we want to be careful. Leaving it hardcoded will trigger
                // a discussion if needed instead of allowing a change to slip in casually via an
                // otherwise innocuous strings.xml.
                licenseText =
                    "Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:\n\nThe above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.\n\nTHE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE."
            )

            /* Third-party licences, if/when we have some:

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Third-Party Licenses",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Each third-party library gets a separate block
            ThirdPartyLicense(
                libraryName = "ExampleLibrary",
                licenseName = "MIT License",
                licenseText = "[full MIT license text for ExampleLibrary]"
            )
            ThirdPartyLicense(
                libraryName = "ExampleLibrary2",
                licenseName = "MIT License",
                licenseText = "[full MIT license text for ExampleLibrary]"
            )
            */

            Spacer(modifier = Modifier.height(fullScreenDialogVerticalBorder))
        }
    }
}

@Suppress("unused")
@Composable
private fun ThirdPartyLicense(libraryName: String, licenseName: String, licenseText: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = "$libraryName $emDash $licenseName",
            style = MaterialTheme.typography.titleSmall,
        )
        Spacer(modifier = Modifier.height(4.dp))
        LicenseText(licenseText)
    }
}

@Composable
private fun LicenseText(licenseText: String) {
    Text(text = licenseText, style = MaterialTheme.typography.bodySmall)
}
