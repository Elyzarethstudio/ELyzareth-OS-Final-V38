package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.ElyzarethDatabase
import com.example.data.repository.OsBranchRepository
import com.example.foundation.ConstitutionalLaws
import com.example.foundation.GovernanceGateId
import com.example.foundation.PipelineStage
import com.example.foundation.ReleaseRules
import com.example.kernel.GateEvaluationState
import com.example.kernel.SovereignKernel
import com.example.navigation.OsModuleDestination
import com.example.shared.BackgroundJobCoordinator
import com.example.shared.OfflineFirstIntegrations
import com.example.shared.SnapshotBackupManager
import com.example.ui.theme.MyApplicationTheme
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
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w411dp-h891dp", sdk = [36])
class SovereignKernelAndOsIntegrationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

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

        // Navigate to Idea & Blueprint Studio via Command Centre Grid
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

        // Return to Command Centre via bottom bar
        composeTestRule.onNodeWithTag("nav_tab_command_centre").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("command_centre_screen").assertIsDisplayed()
    }
}
