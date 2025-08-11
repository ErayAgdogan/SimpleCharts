package com.goanderco.simplecharts.feature.main

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.goanderco.R
import com.goanderco.simplecharts.MainNavigationPage
import com.goanderco.simplecharts.core.ui.dialog.PickDataFileDialog
import com.goanderco.simplecharts.core.util.CSV_MIME_TYPE
import com.goanderco.simplecharts.core.util.ContactInfo
import com.goanderco.simplecharts.core.util.DataFileType
import com.goanderco.simplecharts.core.util.EXCEL_MIME_TYPE
import com.goanderco.simplecharts.core.util.TSV_MIME_TYPE
import com.goanderco.simplecharts.core.viewmodel.DataViewModel
import com.goanderco.simplecharts.feature.summary.SummaryPage
import com.goanderco.simplecharts.feature.table.TablePage
import com.goanderco.simplecharts.feature.visual.VisualPage
import kotlinx.coroutines.launch
import androidx.core.net.toUri


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewmodel: DataViewModel = viewModel()) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var page by rememberSaveable { mutableStateOf(MainNavigationPage.VISUAL) }
    val showLoadingProgress by viewmodel.showProgress.collectAsState()
    val mailLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { uri ->

    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column{
                TopAppBar(
                    title = { Text(stringResource(R.string.app_name)) },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                if(drawerState.isClosed)
                                    drawerState.open()
                                else
                                    drawerState.close()

                            }
                        }) {
                            Icon(
                                imageVector =
                                    if(drawerState.isClosed)
                                        Icons.Filled.Menu
                                    else
                                        Icons.Filled.Clear
                                , contentDescription = "Menu")
                        }
                    },
                    actions = {
                        var expanded by remember{ mutableStateOf(false) }
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More options")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.contact_us)) },
                                leadingIcon = { Icon(Icons.Default.Email, null) },
                                onClick = {
                                    expanded = false
                                    mailLauncher.launch(Intent(Intent.ACTION_SENDTO).apply {
                                        // The intent does not have a URI, so declare the "text/plain" MIME type

                                        data = "mailto:".toUri()
                                        putExtra(Intent.EXTRA_EMAIL, arrayOf(ContactInfo.DEVELOPER_MAIL)) // recipients
                                    })
                                }
                            )
                        }
                    }
                )
                AnimatedVisibility(visible = showLoadingProgress) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

            }

        }
    ) { padding ->
        DismissibleNavigationDrawer (
            modifier= Modifier.padding(padding),
            drawerState = drawerState,
            drawerContent = {
                // Here's where our "rail" items become drawer items
                SimplePlotsNavigation(

                    selected = page,
                    onSelected = { page = it }
                )

            }
        ) {

            when(page) {
                MainNavigationPage.VISUAL ->  VisualPage()
                MainNavigationPage.SUMMARY -> SummaryPage()
                MainNavigationPage.DATA -> TablePage()
            }


        }
    }
}


@Composable
private fun SimplePlotsNavigation(

    selected:MainNavigationPage,
    onSelected: (MainNavigationPage) -> Unit,
    viewmodel: DataViewModel = viewModel()
) {
    val context = LocalContext.current
    var fileType: DataFileType by remember { mutableStateOf(DataFileType.CSV) }
    var firstRowIsHeader by remember { mutableStateOf(true) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let { viewmodel.readDataFile(context, fileType, it, firstRowIsHeader) }
    }
    var showDataFilePicker by remember { mutableStateOf(false) }
    if (showDataFilePicker)
        PickDataFileDialog({ type ->
            fileType = type
            when (type) {
                DataFileType.CSV -> launcher.launch(CSV_MIME_TYPE)
                DataFileType.TSV -> launcher.launch(TSV_MIME_TYPE)
                DataFileType.EXCEL -> launcher.launch(EXCEL_MIME_TYPE)
            }
                           },
            firstRowHeader = firstRowIsHeader,
            onFirstRowIsHeader = { firstRowIsHeader = it },
            dismiss = { showDataFilePicker = false }
        )

    NavigationRail(modifier = Modifier
        .wrapContentWidth()
        .padding(8.dp)) {
        FloatingActionButton(
            modifier= Modifier
                .padding(8.dp)
                .wrapContentSize(),
            onClick = {
                showDataFilePicker = true

            }
        ) {
            Icon(painterResource(R.drawable.baseline_upload_file_24), contentDescription = null)
        }
        Text(stringResource(R.string.upload_file), style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(36.dp))
        NavigationRailItem(
            selected = selected == MainNavigationPage.VISUAL,
            onClick = { onSelected(MainNavigationPage.VISUAL) },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.baseline_bar_chart_24),
                    contentDescription = "Visual"
                )
            },
            label = {  Text("Visual") }
        )
        NavigationRailItem(
            selected = selected == MainNavigationPage.SUMMARY,
            onClick = { onSelected(MainNavigationPage.SUMMARY) },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.baseline_summarize_24),
                    contentDescription = stringResource(R.string.summary)
                )
            },
            label = {  Text(stringResource(R.string.summary)) }
        )
        NavigationRailItem(
            selected = selected == MainNavigationPage.DATA,
            onClick = { onSelected(MainNavigationPage.DATA) },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.baseline_dataset_24),
                    contentDescription = "Data"
                )
            },
            label = {  Text("Data") }
        )

    }
}

