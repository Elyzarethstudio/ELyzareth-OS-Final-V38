package com.example

import android.app.Application
import android.content.Context
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performScrollToNode
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.ElyzarethDatabase
import com.example.data.repository.CorpusRepository
import com.example.data.repository.OsBranchRepository
import com.example.foundation.ConstitutionalLaws
import com.example.foundation.GovernanceGateId
import com.example.foundation.PipelineStage
import com.example.foundation.ReleaseRules
import com.example.kernel.CanonicalProjectContext
import com.example.kernel.GateEvaluationState
import com.example.kernel.SovereignKernel
import com.example.navigation.OsModuleDestination
import com.example.shared.BackgroundJobCoordinator
import com.example.shared.OfflineFirstIntegrations
import com.example.shared.SnapshotBackupManager
import com.example.shared.SovereignSecurityManager
import com.example.ui.commandcentre.CommandCentreScreen
import com.example.ui.commandcentre.clampSystemHealthPercent
import com.example.ui.commandcentre.computeHealthRingSweepAngle
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.ElyzarethViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w411dp-h891dp", sdk = [36])
class SovereignKernelAndOsIntegrationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private data class IsolatedEmptyDbContext(
        val viewModel: ElyzarethViewModel,
        val database: ElyzarethDatabase,
        val cleanup: () -> Unit
    )

    private fun createIsolatedEmptyDbViewModel(
        app: Application
    ): IsolatedEmptyDbContext {
        val store = androidx.lifecycle.ViewModelStore()
        val directExecutor = java.util.concurrent.Executor { it.run() }
        val db = Room.inMemoryDatabaseBuilder(app, ElyzarethDatabase::class.java)
            .allowMainThreadQueries()
            .setQueryExecutor(directExecutor)
            .setTransactionExecutor(directExecutor)
            .build()
        val factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return ElyzarethViewModel(
                    application = app,
                    database = db,
                    autoInitializeSeedData = false
                ) as T
            }
        }
        val vm = androidx.lifecycle.ViewModelProvider(store, factory)[ElyzarethViewModel::class.java]
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
        val cleanup: () -> Unit = {
            store.clear()
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
            db.close()
        }
        return IsolatedEmptyDbContext(vm, db, cleanup)
    }

    @Test
    fun `navigation registry contains all sixteen unique canonical destinations`() {
        val destinations = OsModuleDestination.entries
        assertEquals(16, destinations.size)
        assertEquals(16, destinations.map { it.routeId }.toSet().size)
        assertEquals(16, destinations.map { it.badgeNumber }.toSet().size)
        assertEquals(12, OsModuleDestination.COMMAND_CENTRE_GRID_MODULES.size)
        assertTrue(OsModuleDestination.COMMAND_CENTRE_GRID_MODULES.all {
            it != OsModuleDestination.COMMAND_CENTRE &&
                it != OsModuleDestination.SYSTEM_SERVICES &&
                it != OsModuleDestination.PROJECT_OVERVIEW &&
                it != OsModuleDestination.SOVEREIGN_KERNEL
        })
    }

    @Test
    fun `F-09 health ring clamps values outside 0 to 100 and maps 0, 50, and 100 percent to exact sweep angles`() {
        // Boundary clamping and sweep angle verification
        assertEquals(0, clampSystemHealthPercent(-45))
        assertEquals(0f, computeHealthRingSweepAngle(-45), 0.001f)

        assertEquals(0, clampSystemHealthPercent(0))
        assertEquals(0f, computeHealthRingSweepAngle(0), 0.001f)

        assertEquals(50, clampSystemHealthPercent(50))
        assertEquals(180f, computeHealthRingSweepAngle(50), 0.001f)

        assertEquals(100, clampSystemHealthPercent(100))
        assertEquals(360f, computeHealthRingSweepAngle(100), 0.001f)

        assertEquals(100, clampSystemHealthPercent(145))
        assertEquals(360f, computeHealthRingSweepAngle(145), 0.001f)

        // Compose UI verification of CommandCentreScreen health ring across -45%, 0%, 50%, 100%, and 145%
        val app = ApplicationProvider.getApplicationContext<Application>()
        val (viewModel, _, cleanup) = createIsolatedEmptyDbViewModel(app)
        try {
            var currentHealth by mutableIntStateOf(0)

            composeTestRule.setContent {
                MyApplicationTheme {
                    CommandCentreScreen(
                        viewModel = viewModel,
                        uiState = viewModel.uiState.value,
                        projectContext = CanonicalProjectContext(systemHealthPercent = currentHealth),
                        totalTitlesCount = 0,
                        totalBlueprintsCount = 0,
                        pendingReviewCount = 0
                    )
                }
            }
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithTag("command_centre_screen")
                .performScrollToNode(hasText("System Status"))
            composeTestRule.waitForIdle()

            // 0% renders clamped "0%" and zero foreground arc
            composeTestRule.onNodeWithTag("system_health_ring").assertIsDisplayed()
            composeTestRule.onNodeWithTag("system_health_percent_text").assertTextEquals("0%")

            // Negative value clamps to "0%"
            currentHealth = -45
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithTag("system_health_percent_text").assertTextEquals("0%")

            // 50% renders "50%" (180° half-circle)
            currentHealth = 50
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithTag("system_health_percent_text").assertTextEquals("50%")

            // 100% renders "100%" (360° complete circle)
            currentHealth = 100
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithTag("system_health_percent_text").assertTextEquals("100%")

            // >100% clamps to "100%"
            currentHealth = 145
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithTag("system_health_percent_text").assertTextEquals("100%")
        } finally {
            cleanup()
        }
    }

    @Test
    fun `F-13 all sixteen routes are reachable with ordered deduplicated history and BackHandler restoration`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val (viewModel, _, cleanup) = createIsolatedEmptyDbViewModel(app)
        try {
            fun expectedTabFor(destination: OsModuleDestination): Int = when (destination) {
                OsModuleDestination.CORPUS_ARCHIVE,
                OsModuleDestination.LYRIC_CURATOR -> 0
                OsModuleDestination.LYRIC_GENERATOR,
                OsModuleDestination.IDEA_BLUEPRINT -> 1
                OsModuleDestination.GOVERNANCE_CONSOLE,
                OsModuleDestination.EVIDENCE_VAULT -> 2
                else -> 3
            }

            // 1. Empty-history behavior at initial COMMAND_CENTRE root
            assertEquals(OsModuleDestination.COMMAND_CENTRE, viewModel.uiState.value.activeDestination)
            assertEquals(3, viewModel.uiState.value.activeTab)
            assertTrue(viewModel.uiState.value.navigationHistory.isEmpty())
            assertFalse(viewModel.navigateBack())

            // 2. Reachability of all 16 canonical destinations & no duplicate history on repeat navigation
            OsModuleDestination.entries.forEach { destination ->
                viewModel.navigateTo(destination)
                assertEquals(destination, viewModel.uiState.value.activeDestination)
                assertEquals(expectedTabFor(destination), viewModel.uiState.value.activeTab)

                // Re-navigating to the same active destination must not add a duplicate history entry
                val historySnapshot = viewModel.uiState.value.navigationHistory
                viewModel.navigateTo(destination)
                assertEquals(historySnapshot, viewModel.uiState.value.navigationHistory)
                assertFalse(viewModel.uiState.value.navigationHistory.contains(destination))
            }

            // 3. Verify exact history ordering across all 16 destinations (15 predecessors in traversal order)
            val expectedHistoryOrder = OsModuleDestination.entries.dropLast(1)
            assertEquals(expectedHistoryOrder, viewModel.uiState.value.navigationHistory)
            assertEquals(
                viewModel.uiState.value.navigationHistory.size,
                viewModel.uiState.value.navigationHistory.distinct().size
            )

            // 4. Pop back through all 15 history entries and verify destination, activeTab, and no stale entries
            for (expectedPrevious in expectedHistoryOrder.asReversed()) {
                val sizeBeforePop = viewModel.uiState.value.navigationHistory.size
                assertTrue(viewModel.navigateBack())
                assertEquals(expectedPrevious, viewModel.uiState.value.activeDestination)
                assertEquals(expectedTabFor(expectedPrevious), viewModel.uiState.value.activeTab)
                assertEquals(sizeBeforePop - 1, viewModel.uiState.value.navigationHistory.size)
                assertFalse(viewModel.uiState.value.navigationHistory.contains(expectedPrevious))
            }

            // 5. Back at COMMAND_CENTRE with empty history -> navigateBack() returns false
            assertEquals(OsModuleDestination.COMMAND_CENTRE, viewModel.uiState.value.activeDestination)
            assertEquals(3, viewModel.uiState.value.activeTab)
            assertTrue(viewModel.uiState.value.navigationHistory.isEmpty())
            assertFalse(viewModel.navigateBack())

            // 6. Verify revisit deduplication (A -> B -> C -> B) removes stale B from history
            viewModel.navigateTo(OsModuleDestination.IDEA_BLUEPRINT)
            viewModel.navigateTo(OsModuleDestination.FORENSIC_LAB)
            viewModel.navigateTo(OsModuleDestination.IDEA_BLUEPRINT)
            assertEquals(OsModuleDestination.IDEA_BLUEPRINT, viewModel.uiState.value.activeDestination)
            assertEquals(
                listOf(OsModuleDestination.COMMAND_CENTRE, OsModuleDestination.FORENSIC_LAB),
                viewModel.uiState.value.navigationHistory
            )

            // Reset back to COMMAND_CENTRE with empty history before UI BackHandler regression scenario
            while (viewModel.navigateBack()) {
                // drain remaining history
            }
            assertEquals(OsModuleDestination.COMMAND_CENTRE, viewModel.uiState.value.activeDestination)
            assertTrue(viewModel.uiState.value.navigationHistory.isEmpty())

            // 7. Required BackHandler regression scenario:
            //    (a) Start at COMMAND_CENTRE with empty history -> BackHandler is disabled.
            //    (b) Navigate from COMMAND_CENTRE to IDEA_BLUEPRINT.
            //    (c) Navigate back to COMMAND_CENTRE via app navigation control while preserving history [IDEA_BLUEPRINT].
            //    (d) Verify Android Back (OnBackPressedDispatcher) is enabled at COMMAND_CENTRE and restores IDEA_BLUEPRINT.
            //    (e) Verify subsequent Android Back from IDEA_BLUEPRINT with empty history falls back to COMMAND_CENTRE,
            //        after which BackHandler is disabled at empty-history COMMAND_CENTRE.
            var backDispatcher: OnBackPressedDispatcher? = null
            composeTestRule.setContent {
                backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
                MyApplicationTheme {
                    ElyzarethApp(viewModel = viewModel)
                }
            }
            composeTestRule.waitForIdle()
            assertNotNull(backDispatcher)
            assertFalse(backDispatcher!!.hasEnabledCallbacks())

            // Step (b): Navigate from COMMAND_CENTRE to IDEA_BLUEPRINT via UI grid card
            composeTestRule.onNodeWithTag("command_centre_screen").performScrollToIndex(1)
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithTag("grid_card_idea_blueprint").performClick()
            composeTestRule.waitForIdle()
            assertEquals(OsModuleDestination.IDEA_BLUEPRINT, viewModel.uiState.value.activeDestination)
            assertEquals(listOf(OsModuleDestination.COMMAND_CENTRE), viewModel.uiState.value.navigationHistory)
            assertTrue(backDispatcher!!.hasEnabledCallbacks())

            // Step (c): Return to COMMAND_CENTRE via bottom navigation bar control while preserving history [IDEA_BLUEPRINT]
            composeTestRule.onNodeWithTag("nav_tab_command_centre").performClick()
            composeTestRule.waitForIdle()
            assertEquals(OsModuleDestination.COMMAND_CENTRE, viewModel.uiState.value.activeDestination)
            assertEquals(listOf(OsModuleDestination.IDEA_BLUEPRINT), viewModel.uiState.value.navigationHistory)

            // Step (d): Verify BackHandler is enabled at COMMAND_CENTRE when history is non-empty and restores IDEA_BLUEPRINT
            assertTrue(backDispatcher!!.hasEnabledCallbacks())
            composeTestRule.runOnUiThread {
                backDispatcher!!.onBackPressed()
            }
            composeTestRule.waitForIdle()
            assertEquals(OsModuleDestination.IDEA_BLUEPRINT, viewModel.uiState.value.activeDestination)
            assertEquals(1, viewModel.uiState.value.activeTab)
            assertTrue(viewModel.uiState.value.navigationHistory.isEmpty())
            composeTestRule.onNodeWithTag("idea_blueprint_screen").assertIsDisplayed()

            // Step (e): From IDEA_BLUEPRINT with empty history, BackHandler remains enabled to fall back to COMMAND_CENTRE
            assertTrue(backDispatcher!!.hasEnabledCallbacks())
            composeTestRule.runOnUiThread {
                backDispatcher!!.onBackPressed()
            }
            composeTestRule.waitForIdle()
            assertEquals(OsModuleDestination.COMMAND_CENTRE, viewModel.uiState.value.activeDestination)
            assertEquals(3, viewModel.uiState.value.activeTab)
            assertTrue(viewModel.uiState.value.navigationHistory.isEmpty())
            composeTestRule.onNodeWithTag("command_centre_screen").assertIsDisplayed()

            // At COMMAND_CENTRE with empty history, BackHandler is disabled and navigateBack() returns false
            assertFalse(backDispatcher!!.hasEnabledCallbacks())
            assertFalse(viewModel.navigateBack())
        } finally {
            cleanup()
        }
    }

    @Test
    fun `F-14 fresh empty Room database preserves genuine zero-state without synthetic initialization`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val (viewModel, database, cleanup) = createIsolatedEmptyDbViewModel(app)
        try {
            // 1. Explicitly await Room DAO queries and Flow emissions so empty-state assertions
            //    verify settled database queries rather than initial StateFlow default values.
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
            runBlocking {
                assertEquals(0, database.elyzarethDao().getBaseTitlesCount())
                assertEquals(0, database.elyzarethDao().getArtifactsCount())
                assertEquals(0, database.osBranchDao().getBlueprintsCount())

                assertTrue(database.elyzarethDao().getAllBaseTitles().first().isEmpty())
                assertTrue(database.elyzarethDao().getAllArtifacts().first().isEmpty())
                assertTrue(database.osBranchDao().getAllBlueprints().first().isEmpty())
                assertTrue(database.osBranchDao().getAllEvidenceRecords().first().isEmpty())
                assertTrue(database.osBranchDao().getAllDnaProfiles().first().isEmpty())
                assertTrue(database.osBranchDao().getAllProductionTracks().first().isEmpty())
                assertTrue(database.osBranchDao().getAllVisualAssets().first().isEmpty())
                assertTrue(database.osBranchDao().getAllForensicReports().first().isEmpty())
                assertTrue(database.osBranchDao().getAllRegisteredAssets().first().isEmpty())
                assertTrue(database.osBranchDao().getAllMasterReleases().first().isEmpty())

                assertTrue(viewModel.allBaseTitles.first().isEmpty())
                assertTrue(viewModel.allArtifacts.first().isEmpty())
                assertTrue(viewModel.blueprints.first().isEmpty())
                assertTrue(viewModel.evidenceRecords.first().isEmpty())
            }
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()

            // 2. Verify ViewModel StateFlows and audit report reflect genuine zero-state
            assertTrue(viewModel.allBaseTitles.value.isEmpty())
            assertTrue(viewModel.allArtifacts.value.isEmpty())
            assertTrue(viewModel.blueprints.value.isEmpty())
            assertTrue(viewModel.baseTitles.value.isEmpty())
            assertTrue(viewModel.evidenceRecords.value.isEmpty())
            assertEquals(0, viewModel.uiState.value.report.baseTitlesDiscovered)
            assertEquals(0, viewModel.uiState.value.report.totalArtifactsDiscovered)

            // 3. Snapshot generation succeeds and the generated snapshot itself contains zero counts
            viewModel.createSystemBackupSnapshot()
            val zeroSnapshot = viewModel.uiState.value.lastBackupSnapshot
            assertNotNull(zeroSnapshot)
            assertEquals(0, zeroSnapshot!!.baseTitlesCount)
            assertEquals(0, zeroSnapshot.artifactsCount)
            assertEquals(0, zeroSnapshot.projectsCount)
            assertEquals(0, zeroSnapshot.evidenceEntriesCount)
            assertTrue(zeroSnapshot.checksumSha256.isNotBlank())

            // 4. Export manifest on empty state does not throw NoSuchElementException or divide by zero
            viewModel.exportManifest()
            assertNotNull(viewModel.uiState.value.manifestJsonExport)
            viewModel.dismissExport()

            // 5. Render full ElyzarethApp shell with the empty-database ViewModel
            composeTestRule.setContent {
                MyApplicationTheme {
                    ElyzarethApp(viewModel = viewModel)
                }
            }
            composeTestRule.waitForIdle()

            // Command Centre renders without crashing and displays genuine zero counts (no 390 / 3 floor)
            composeTestRule.onNodeWithTag("command_centre_screen").assertIsDisplayed()
            composeTestRule.onNodeWithTag("command_centre_screen")
                .performScrollToNode(hasText("System Status"))
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithText("Projects: 0").assertIsDisplayed()
            composeTestRule.onNodeWithText("Songs: 0").assertIsDisplayed()
            composeTestRule.onNodeWithText("Pending Review: 0").assertIsDisplayed()

            // Navigate to Corpus Archive & Project Overview in empty state to confirm no crash or auto-seeding
            viewModel.navigateToDestination(OsModuleDestination.CORPUS_ARCHIVE)
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithText("INVENTORY: 0 BASE TITLES DISCOVERED").assertIsDisplayed()

            viewModel.navigateToDestination(OsModuleDestination.PROJECT_OVERVIEW)
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithTag("project_overview_screen").assertIsDisplayed()

            // 6. Confirm neither UI rendering nor navigation triggered synthetic database initialization
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
            runBlocking {
                assertEquals(0, database.elyzarethDao().getBaseTitlesCount())
                assertEquals(0, database.elyzarethDao().getArtifactsCount())
                assertEquals(0, database.osBranchDao().getBlueprintsCount())
                assertTrue(database.osBranchDao().getAllEvidenceRecords().first().isEmpty())
            }
            assertTrue(viewModel.allBaseTitles.value.isEmpty())
            assertTrue(viewModel.allArtifacts.value.isEmpty())
            assertTrue(viewModel.blueprints.value.isEmpty())
            assertTrue(viewModel.evidenceRecords.value.isEmpty())
        } finally {
            cleanup()
        }
    }

    @Test
    fun `foundation layer enforces 6 constitutional laws and 14 canonical pipeline stages`() {
        assertEquals(6, ConstitutionalLaws.ALL_LAWS.size)
        assertEquals(14, PipelineStage.entries.size)
        assertEquals("01", PipelineStage.STAGE_01_IDEA_INCEPTION.code)
        assertEquals("14", PipelineStage.STAGE_14_MASTER_RELEASE.code)

        // Verify G1-G5 zero-bypass release lock
        assertFalse(
            ReleaseRules.canUnlockMasterRelease(
                g1Status = "VERIFIED",
                g2Status = "VERIFIED",
                g3Status = "VERIFIED",
                g4Status = "NOT MEASURED",
                g5Status = "VERIFIED",
                humanSignOffConfirmed = true
            )
        )
        assertTrue(
            ReleaseRules.canUnlockMasterRelease(
                g1Status = "VERIFIED",
                g2Status = "VERIFIED",
                g3Status = "VERIFIED",
                g4Status = "VERIFIED",
                g5Status = "VERIFIED",
                humanSignOffConfirmed = true
            )
        )
    }

    @Test
    fun `sovereign kernel blocks G5 approval when G1 to G4 are unverified and allows when verified`() {
        val unverifiedState = GateEvaluationState(
            targetId = "bt_test",
            targetTitle = "Test Witness Song",
            g1Status = "VERIFIED",
            g2Status = "VERIFIED",
            g3Status = "NOT MEASURED",
            g4Status = "VERIFIED",
            g5Status = "NOT MEASURED",
            humanSignOff = false
        )

        val blockedAttempt = SovereignKernel.evaluateGateTransition(
            current = unverifiedState,
            gate = GovernanceGateId.G5,
            newStatus = "VERIFIED",
            governorNote = "Attempt early G5 unlock"
        )
        assertTrue(blockedAttempt.g5Status.startsWith("BLOCKED"))
        assertFalse(SovereignKernel.canSealMasterRelease(blockedAttempt))

        // Now verify G3 and then G5
        val g3Verified = SovereignKernel.evaluateGateTransition(
            current = unverifiedState,
            gate = GovernanceGateId.G3,
            newStatus = "VERIFIED",
            governorNote = "G3 Creative DNA verified"
        )
        val g5Approved = SovereignKernel.evaluateGateTransition(
            current = g3Verified,
            gate = GovernanceGateId.G5,
            newStatus = "VERIFIED",
            governorNote = "Student-Director final G5 sign-off"
        )
        assertEquals("VERIFIED", g5Approved.g5Status)
        assertTrue(g5Approved.humanSignOff)
        assertTrue(SovereignKernel.canSealMasterRelease(g5Approved))
    }

    @Test
    fun `shared utilities generate deterministic hashes, encrypted snapshots, and offline adapters`() {
        val hash1 = SovereignKernel.computeSha256Hex("ELYZARETH_TEST_PAYLOAD")
        assertTrue(SovereignKernel.verifyHashParity("ELYZARETH_TEST_PAYLOAD", hash1))

        val signature = SovereignSecurityManager.signPayload(hash1)
        assertTrue(signature.startsWith("sha256_"))

        // Replaced hardcoded 390/510 tests to verify zero-state logic correctly handles real bounds
        val snapshot = SnapshotBackupManager.generateSnapshot(
            titlesCount = 0,
            artifactsCount = 0,
            projectsCount = 0,
            evidenceCount = 0
        )
        assertEquals(0, snapshot.baseTitlesCount)
        assertEquals(0, snapshot.artifactsCount)
        assertTrue(snapshot.encrypted)

        val jobs = BackgroundJobCoordinator.getRegisteredJobs(0, 0)
        assertEquals(4, jobs.size)
        assertEquals(4, OfflineFirstIntegrations.adapters.size)
        assertTrue(OfflineFirstIntegrations.adapters.all { it.isOptional })
    }

    @Test
    fun `os branch repository initializes and persists all functional branches in Room`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = ElyzarethDatabase.getDatabase(context)
        val branchRepo = OsBranchRepository(db.osBranchDao())

        branchRepo.ensureBranchesInitialized()

        val blueprints = branchRepo.allBlueprints.first()
        val dnaProfiles = branchRepo.allDnaProfiles.first()
        val tracks = branchRepo.allProductionTracks.first()
        val visuals = branchRepo.allVisualAssets.first()
        val forensics = branchRepo.allForensicReports.first()
        val evidence = branchRepo.allEvidenceRecords.first()
        val assets = branchRepo.allRegisteredAssets.first()
        val releases = branchRepo.allMasterReleases.first()

        assertTrue(blueprints.size >= 3)
        assertTrue(dnaProfiles.size >= 2)
        assertTrue(tracks.size >= 2)
        assertTrue(visuals.size >= 2)
        assertTrue(forensics.size >= 2)
        assertTrue(evidence.size >= 2)
        assertTrue(assets.size >= 3)
        assertTrue(releases.size >= 2)

        // Test creating a new blueprint and registering a new asset
        branchRepo.createBlueprint(
            title = "Iron Key on the Threshold",
            conceptIntent = "Cold iron key resting on stone step",
            songStructure = "Verse 1 - Chorus - Verse 2",
            genreAndStyle = "Acoustic Chamber Folk / 72 BPM",
            emotionAndTheme = "Domestic Grounding",
            physicalAnchorName = "Iron Key",
            primaryLanguage = "English"
        )
        val updatedBlueprints = branchRepo.allBlueprints.first()
        assertTrue(updatedBlueprints.any { it.title == "Iron Key on the Threshold" })
    }

    @Test
    fun `viewModel allBaseTitles, pendingReview totals, and backup snapshots remain independent of active corpus filters`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val db = ElyzarethDatabase.getDatabase(app)
        runBlocking {
            CorpusRepository(db.elyzarethDao()).ensureCorpusInitialized()
            OsBranchRepository(db.osBranchDao()).ensureBranchesInitialized()
        }

        val viewModel = ElyzarethViewModel(app)
        val deadline = System.currentTimeMillis() + 5_000L
        while (viewModel.allBaseTitles.value.isEmpty() && System.currentTimeMillis() < deadline) {
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
            Thread.sleep(20)
        }

        val unfilteredList = viewModel.allBaseTitles.value
        val totalCorpusCount = unfilteredList.size
        val totalPendingReviewCount = unfilteredList.count { it.containsAmbiguity }
        assertTrue(totalCorpusCount > 0)
        assertTrue(totalPendingReviewCount > 0)

        // Apply strict search and status filters that narrow or empty the curator view
        viewModel.setSearchQuery("__NON_MATCHING_FILTER_QUERY_EOS_UI_001__")
        viewModel.setSelectedLanguage("Hindi")
        viewModel.setSelectedStatus("RELEASED & FROZEN")
        viewModel.toggleReviewOnly()

        val filterDeadline = System.currentTimeMillis() + 5_000L
        while (viewModel.baseTitles.value.isNotEmpty() && System.currentTimeMillis() < filterDeadline) {
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
            Thread.sleep(20)
        }
        assertEquals(0, viewModel.baseTitles.value.size)

        // Verify system-wide unfiltered corpus stream and pending-review count remain intact
        assertEquals(totalCorpusCount, viewModel.allBaseTitles.value.size)
        assertEquals(totalPendingReviewCount, viewModel.allBaseTitles.value.count { it.containsAmbiguity })

        // Verify ViewModel backup snapshot uses the complete corpus rather than filtered baseTitles
        viewModel.createSystemBackupSnapshot()
        val snapshot = viewModel.uiState.value.lastBackupSnapshot
        assertNotNull(snapshot)
        assertEquals(totalCorpusCount, snapshot!!.baseTitlesCount)
    }

    @Test
    fun `application shell launches Command Centre and navigates across branches`() {
        composeTestRule.setContent {
            MyApplicationTheme {
                ElyzarethApp()
            }
        }
        composeTestRule.waitForIdle()

        // Verify Command Centre Dashboard is displayed on startup
        composeTestRule.onNodeWithTag("command_centre_screen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bottom_nav_bar").assertIsDisplayed()

        // Scroll to System Status card in LazyColumn and verify truthful subsystem status lines
        composeTestRule.onNodeWithTag("command_centre_screen")
            .performScrollToNode(hasText("Kernel: Context available"))
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Kernel: Context available").assertIsDisplayed()
        composeTestRule.onNodeWithText("Database: Status not exposed").assertIsDisplayed()
        composeTestRule.onNodeWithText("Governance: Status not exposed").assertIsDisplayed()

        // Scroll back to top of Command Centre and navigate to Idea & Blueprint Studio via Grid
        composeTestRule.onNodeWithTag("command_centre_screen").performScrollToIndex(1)
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("grid_card_idea_blueprint").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("idea_blueprint_screen").assertIsDisplayed()

        // Navigate to App 01 Lyric Generator via bottom bar
        composeTestRule.onNodeWithTag("nav_tab_generator").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("lyric_generator_screen").assertIsDisplayed()

        // Navigate to 3.2.1.0 Protocol / Governance Console via bottom bar
        composeTestRule.onNodeWithTag("nav_tab_protocol").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("gov_mode_gates").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("g1_g5_gate_matrix").assertIsDisplayed()

        // Exercise back navigation from Governance Console -> Lyric Generator -> Idea & Blueprint -> Command Centre
        composeTestRule.onNodeWithTag("top_back_btn").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("lyric_generator_screen").assertIsDisplayed()

        composeTestRule.onNodeWithTag("top_back_btn").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("idea_blueprint_screen").assertIsDisplayed()

        composeTestRule.onNodeWithTag("top_back_btn").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("command_centre_screen").assertIsDisplayed()
    }
}
