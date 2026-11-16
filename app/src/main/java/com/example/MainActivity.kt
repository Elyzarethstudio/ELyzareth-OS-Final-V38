package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foundation.SystemIdentity
import com.example.navigation.OsModuleDestination
import com.example.ui.commandcentre.CommandCentreScreen
import com.example.ui.commandcentre.moduleVisualSpec
import com.example.ui.creative.CreativeDnaEngineScreen
import com.example.ui.creative.IdeaBlueprintStudioScreen
import com.example.ui.creative.LyricCuratorModuleScreen
import com.example.ui.curator.CorpusDashboardScreen
import com.example.ui.generator.LyricGeneratorScreen
import com.example.ui.intelligence.EvidenceVaultScreen
import com.example.ui.intelligence.ForensicLabScreen
import com.example.ui.intelligence.MasterVaultAndPlayerScreen
import com.example.ui.production.AssetRegistryScreen
import com.example.ui.production.MusicProductionConsoleScreen
import com.example.ui.production.VisualDirectionStudioScreen
import com.example.ui.services.GovernanceSuiteScreen
import com.example.ui.services.ProjectAndSystemOverviewScreen
import com.example.ui.services.SovereignKernelScreen
import com.example.ui.services.SystemServicesScreen
import com.example.ui.theme.BlueThread
import com.example.ui.theme.BronzeGold
import com.example.ui.theme.MutedSlateText
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.SlateDark
import com.example.ui.theme.StateNotMeasured
import com.example.ui.theme.StateParsed
import com.example.ui.theme.WitnessParchment
import com.example.viewmodel.ElyzarethViewModel
import kotlinx.coroutines.launch

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
    val allArtifacts by viewModel.allArtifacts.collectAsStateWithLifecycle()
    val blueprints by viewModel.blueprints.collectAsStateWithLifecycle()
    val dnaProfiles by viewModel.dnaProfiles.collectAsStateWithLifecycle()
    val productionTracks by viewModel.productionTracks.collectAsStateWithLifecycle()
    val visualAssets by viewModel.visualAssets.collectAsStateWithLifecycle()
    val forensicReports by viewModel.forensicReports.collectAsStateWithLifecycle()
    val evidenceRecords by viewModel.evidenceRecords.collectAsStateWithLifecycle()
    val registeredAssets by viewModel.registeredAssets.collectAsStateWithLifecycle()
    val masterReleases by viewModel.masterReleases.collectAsStateWithLifecycle()
    val projectContext by viewModel.kernelProjectContext.collectAsStateWithLifecycle()
    val kernelEvents by viewModel.kernelEvents.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // BackHandler returns to previous module or Command Centre
    BackHandler(enabled = uiState.activeDestination != OsModuleDestination.COMMAND_CENTRE) {
        viewModel.navigateBack()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = ObsidianBlack,
                drawerContentColor = WitnessParchment,
                modifier = Modifier
                    .width(290.dp)
                    .fillMaxHeight()
                    .border(1.dp, BronzeGold.copy(alpha = 0.45f))
                    .testTag("os_navigation_drawer")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(14.dp)
                ) {
                    // Royal Crest Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(BronzeGold.copy(alpha = 0.15f))
                                .border(1.5.dp, BronzeGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "E",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Serif,
                                color = BronzeGold
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = SystemIdentity.OS_NAME,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = BronzeGold,
                                fontFamily = FontFamily.Serif
                            )
                            Text(
                                text = "UI/UX Suite v${SystemIdentity.STRUCTURE_VERSION}",
                                fontSize = 10.sp,
                                color = MutedSlateText,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Student-Director / Human Creator Badge (matching left sidebar of Image 2)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(SlateDark)
                            .border(1.dp, BronzeGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(BronzeGold.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Student-Director",
                                tint = BronzeGold,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Student-Director",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = WitnessParchment
                            )
                            Text(
                                text = "Human Creator (Sovereign Authority)",
                                fontSize = 9.sp,
                                color = BronzeGold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = SlateCardBorder)
                    Spacer(modifier = Modifier.height(8.dp))

                    // All Canonical OS Modules
                    OsModuleDestination.entries.forEach { dest ->
                        val isSelected = uiState.activeDestination == dest
                        val (icon, accentColor) = moduleVisualSpec(dest)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) BlueThread.copy(alpha = 0.22f) else SlateDark.copy(alpha = 0.4f)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) BlueThread else ObsidianBlack,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    viewModel.navigateToDestination(dest)
                                    coroutineScope.launch { drawerState.close() }
                                }
                                .padding(horizontal = 10.dp, vertical = 9.dp)
                                .testTag("drawer_item_${dest.routeId}"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = dest.shortTitle,
                                tint = if (isSelected) BlueThread else accentColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = dest.shortTitle,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) WitnessParchment else WitnessParchment.copy(alpha = 0.85f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = SlateCardBorder)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("v1.0 • Offline-First", fontSize = 10.sp, color = MutedSlateText, fontFamily = FontFamily.Monospace)
                        Text("G1–G5 Active", fontSize = 10.sp, color = StateParsed, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SlateDark)
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("os_top_bar"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                }
                            },
                            modifier = Modifier.testTag("open_drawer_btn")
                        ) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Open Navigation Drawer",
                                tint = BronzeGold
                            )
                        }

                        if (uiState.activeDestination != OsModuleDestination.COMMAND_CENTRE) {
                            IconButton(
                                onClick = { viewModel.navigateBack() },
                                modifier = Modifier.testTag("top_back_btn")
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Go back",
                                    tint = WitnessParchment
                                )
                            }
                        }

                        Column {
                            Text(
                                text = uiState.activeDestination.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = WitnessParchment
                            )
                            Text(
                                text = uiState.activeDestination.architecturalLayer,
                                fontSize = 10.sp,
                                color = BronzeGold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    IconButton(
                        onClick = { viewModel.navigateToDestination(OsModuleDestination.SOVEREIGN_KERNEL) },
                        modifier = Modifier.testTag("open_kernel_top_btn")
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Sovereign Kernel Events",
                            tint = BronzeGold
                        )
                    }
                }
            },
            bottomBar = {
                NavigationBar(
                    containerColor = SlateDark,
                    contentColor = WitnessParchment,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    NavigationBarItem(
                        selected = uiState.activeDestination == OsModuleDestination.COMMAND_CENTRE,
                        onClick = { viewModel.navigateToDestination(OsModuleDestination.COMMAND_CENTRE) },
                        icon = {
                            Icon(
                                Icons.Default.AccountBalance,
                                contentDescription = "Command Centre",
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "Command",
                                fontSize = 10.sp,
                                fontWeight = if (uiState.activeDestination == OsModuleDestination.COMMAND_CENTRE) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BronzeGold,
                            selectedTextColor = BronzeGold,
                            unselectedIconColor = StateNotMeasured,
                            unselectedTextColor = StateNotMeasured,
                            indicatorColor = SlateCard
                        ),
                        modifier = Modifier.testTag("nav_tab_command_centre")
                    )

                    NavigationBarItem(
                        selected = uiState.activeDestination == OsModuleDestination.CORPUS_ARCHIVE,
                        onClick = { viewModel.setTab(0) },
                        icon = {
                            Icon(
                                Icons.Default.LibraryMusic,
                                contentDescription = "Corpus Archive",
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "Corpus Archive",
                                fontSize = 10.sp,
                                fontWeight = if (uiState.activeDestination == OsModuleDestination.CORPUS_ARCHIVE) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BronzeGold,
                            selectedTextColor = BronzeGold,
                            unselectedIconColor = StateNotMeasured,
                            unselectedTextColor = StateNotMeasured,
                            indicatorColor = SlateCard
                        ),
                        modifier = Modifier.testTag("nav_tab_curator")
                    )

                    NavigationBarItem(
                        selected = uiState.activeDestination == OsModuleDestination.LYRIC_GENERATOR,
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
                                fontWeight = if (uiState.activeDestination == OsModuleDestination.LYRIC_GENERATOR) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BlueThread,
                            selectedTextColor = BlueThread,
                            unselectedIconColor = StateNotMeasured,
                            unselectedTextColor = StateNotMeasured,
                            indicatorColor = SlateCard
                        ),
                        modifier = Modifier.testTag("nav_tab_generator")
                    )

                    NavigationBarItem(
                        selected = uiState.activeDestination == OsModuleDestination.GOVERNANCE_CONSOLE,
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
                                fontWeight = if (uiState.activeDestination == OsModuleDestination.GOVERNANCE_CONSOLE) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BronzeGold,
                            selectedTextColor = BronzeGold,
                            unselectedIconColor = StateNotMeasured,
                            unselectedTextColor = StateNotMeasured,
                            indicatorColor = SlateCard
                        ),
                        modifier = Modifier.testTag("nav_tab_protocol")
                    )

                    NavigationBarItem(
                        selected = uiState.activeDestination == OsModuleDestination.MASTER_VAULT,
                        onClick = { viewModel.navigateToDestination(OsModuleDestination.MASTER_VAULT) },
                        icon = {
                            Icon(
                                Icons.Default.PlayCircleFilled,
                                contentDescription = "Master Vault",
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "Master Vault",
                                fontSize = 10.sp,
                                fontWeight = if (uiState.activeDestination == OsModuleDestination.MASTER_VAULT) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BronzeGold,
                            selectedTextColor = BronzeGold,
                            unselectedIconColor = StateNotMeasured,
                            unselectedTextColor = StateNotMeasured,
                            indicatorColor = SlateCard
                        ),
                        modifier = Modifier.testTag("nav_tab_vault")
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
                val pendingReviewCount = baseTitles.count { it.containsAmbiguity }
                when (uiState.activeDestination) {
                    OsModuleDestination.COMMAND_CENTRE -> CommandCentreScreen(
                        viewModel = viewModel,
                        uiState = uiState,
                        projectContext = projectContext,
                        totalTitlesCount = baseTitles.size,
                        totalBlueprintsCount = blueprints.size,
                        pendingReviewCount = pendingReviewCount
                    )
                    OsModuleDestination.IDEA_BLUEPRINT -> IdeaBlueprintStudioScreen(
                        viewModel = viewModel,
                        blueprints = blueprints
                    )
                    OsModuleDestination.LYRIC_GENERATOR -> LyricGeneratorScreen(
                        viewModel = viewModel,
                        uiState = uiState
                    )
                    OsModuleDestination.LYRIC_CURATOR -> LyricCuratorModuleScreen(
                        viewModel = viewModel,
                        uiState = uiState,
                        baseTitles = baseTitles
                    )
                    OsModuleDestination.CREATIVE_DNA -> CreativeDnaEngineScreen(
                        viewModel = viewModel,
                        dnaProfiles = dnaProfiles
                    )
                    OsModuleDestination.MUSIC_PRODUCTION -> MusicProductionConsoleScreen(
                        viewModel = viewModel,
                        tracks = productionTracks
                    )
                    OsModuleDestination.VISUAL_DIRECTION -> VisualDirectionStudioScreen(
                        viewModel = viewModel,
                        visualAssets = visualAssets
                    )
                    OsModuleDestination.CORPUS_ARCHIVE -> CorpusDashboardScreen(
                        viewModel = viewModel,
                        uiState = uiState,
                        baseTitles = baseTitles
                    )
                    OsModuleDestination.FORENSIC_LAB -> ForensicLabScreen(
                        viewModel = viewModel,
                        forensicReports = forensicReports
                    )
                    OsModuleDestination.GOVERNANCE_CONSOLE -> GovernanceSuiteScreen(
                        viewModel = viewModel,
                        uiState = uiState
                    )
                    OsModuleDestination.EVIDENCE_VAULT -> EvidenceVaultScreen(
                        evidenceRecords = evidenceRecords,
                        totalArtifactsCount = allArtifacts.size
                    )
                    OsModuleDestination.ASSET_REGISTRY -> AssetRegistryScreen(
                        viewModel = viewModel,
                        registeredAssets = registeredAssets
                    )
                    OsModuleDestination.MASTER_VAULT -> MasterVaultAndPlayerScreen(
                        viewModel = viewModel,
                        uiState = uiState,
                        masterReleases = masterReleases
                    )
                    OsModuleDestination.SYSTEM_SERVICES -> SystemServicesScreen(
                        viewModel = viewModel,
                        uiState = uiState,
                        titlesCount = baseTitles.size,
                        artifactsCount = allArtifacts.size,
                        kernelEvents = kernelEvents
                    )
                    OsModuleDestination.PROJECT_OVERVIEW -> ProjectAndSystemOverviewScreen(
                        activeProjectsCount = blueprints.size.coerceAtLeast(3),
                        totalSongsCount = baseTitles.size.coerceAtLeast(390),
                        inProductionCount = productionTracks.size.coerceAtLeast(2),
                        pendingApprovalCount = pendingReviewCount
                    )
                    OsModuleDestination.SOVEREIGN_KERNEL -> SovereignKernelScreen(
                        projectContext = projectContext
                    )
                }
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
