/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package com.goanderco.simplecharts

import android.content.Intent
import android.net.Uri
import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.goanderco.simplecharts.core.ui.theme.SimplePlotTheme
import com.goanderco.simplecharts.core.util.DataFileType
import com.goanderco.simplecharts.core.viewmodel.DataViewModel
import com.goanderco.simplecharts.feature.main.MainScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Extract URI properly
        val fileUri = extractFileUri(intent)



        setContent {
            val dataViewModel: DataViewModel = viewModel()

            // Handle file URI if present
            fileUri?.let { uri ->
                val context = LocalContext.current

                LaunchedEffect(uri) {
                    val fileType = DataFileType.getDataFileType(context, uri)
                    dataViewModel.readDataFile(context, fileType, uri, true)
                }
            }

            SimplePlotTheme {
                MainScreen()
            }
        }
    }
}

private fun extractFileUri(intent: Intent): Uri? {
    return when (intent.action) {
        Intent.ACTION_VIEW -> intent.data
        Intent.ACTION_SEND -> intent.getParcelableExtra(Intent.EXTRA_STREAM)
        else -> null
    }
}

