
    
  
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

