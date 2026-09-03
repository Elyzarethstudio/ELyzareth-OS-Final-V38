package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.curator.CorpusDashboardScreen
import com.example.ui.generator.LyricGeneratorScreen
import com.example.ui.protocol.ProtocolConsoleScreen
import com.example.ui.theme.BlueThread
import com.example.ui.theme.BronzeGold
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SlateDark
import com.example.ui.theme.StateNotMeasured
import com.example.ui.theme.WitnessParchment
import com.example.viewmodel.ElyzarethViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ElyzarethApp()
            }
        }
    }
}

@Composable
fun ElyzarethApp(viewModel: ElyzarethViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val baseTitles by viewModel.baseTitles.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = SlateDark,
                contentColor = WitnessParchment,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                NavigationBarItem(
                    selected = uiState.activeTab == 0,
                    onClick = { viewModel.setTab(0) },
                    icon = {
                        Icon(
                            Icons.Default.LibraryMusic,
                            contentDescription = "App 02 Curator",
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "App 02 Curator",
                            fontSize = 10.sp,
                            fontWeight = if (uiState.activeTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BronzeGold,
                        selectedTextColor = BronzeGold,
                        unselectedIconColor = StateNotMeasured,
                        unselectedTextColor = StateNotMeasured,
                        indicatorColor = SlateDark
                    ),
                    modifier = Modifier.testTag("nav_tab_curator")
                )

                NavigationBarItem(
                    selected = uiState.activeTab == 1,
                    onClick = { viewModel.setTab(1) },
                    icon = {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = "App 01 Generator",
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "App 01 Generator",
                            fontSize = 10.sp,
                            fontWeight = if (uiState.activeTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BlueThread,
                        selectedTextColor = BlueThread,
                        unselectedIconColor = StateNotMeasured,
                        unselectedTextColor = StateNotMeasured,
                        indicatorColor = SlateDark
                    ),
                    modifier = Modifier.testTag("nav_tab_generator")
                )

                NavigationBarItem(
                    selected = uiState.activeTab == 2,
                    onClick = { viewModel.setTab(2) },
                    icon = {
                        Icon(
                            Icons.Default.Gavel,
                            contentDescription = "3.2.1.0 Protocol",
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "3.2.1.0 Protocol",
                            fontSize = 10.sp,
                            fontWeight = if (uiState.activeTab == 2) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BronzeGold,
                        selectedTextColor = BronzeGold,
                        unselectedIconColor = StateNotMeasured,
                        unselectedTextColor = StateNotMeasured,
                        indicatorColor = SlateDark
                    ),
                    modifier = Modifier.testTag("nav_tab_protocol")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianBlack)
                .padding(innerPadding)
        ) {
            when (uiState.activeTab) {
                0 -> CorpusDashboardScreen(
                    viewModel = viewModel,
                    uiState = uiState,
                    baseTitles = baseTitles
                )
                1 -> LyricGeneratorScreen(
                    viewModel = viewModel,
                    uiState = uiState
                )
                2 -> ProtocolConsoleScreen(
                    viewModel = viewModel,
                    uiState = uiState
                )
            }
        }
    }
}

// Kept for test and preview compatibility
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Elyzareth OS v2.0: $name",
        color = BronzeGold,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme { Greeting("Forensic Vault") }
}
