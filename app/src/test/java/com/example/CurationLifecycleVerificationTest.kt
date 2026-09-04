package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.CorpusSeedData
import com.example.data.local.ElyzarethDatabase
import com.example.data.model.BaseTitleEntity
import com.example.data.repository.CorpusRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CurationLifecycleVerificationTest {

    private lateinit var context: Context
    private lateinit var database: ElyzarethDatabase
    private lateinit var repository: CorpusRepository

    @Before
    fun setUp() = runBlocking {
        context = ApplicationProvider.getApplicationContext<Context>()
        database = ElyzarethDatabase.getDatabase(context)
        repository = CorpusRepository(database.elyzarethDao())
        repository.ensureCorpusInitialized()
    }

    @Test
    fun `verify complete runtime lifecycle from DEFECTIVE to RELEASED AND FROZEN with DB persistence across simulated restart`() = runBlocking {
        val dao = database.elyzarethDao()
        val allTitles = dao.getAllBaseTitles().first()
        assertTrue("Base titles should be seeded", allTitles.isNotEmpty())

        // STEP 1: Identify Defective Title (status == "FAILED" or containsAmbiguity)
        val defectiveTitle = allTitles.firstOrNull { it.status == "FAILED" }
            ?: allTitles.first { it.containsAmbiguity }
        assertNotNull("Must have a defective title in initial corpus", defectiveTitle)
        val targetId = defectiveTitle!!.id
        val originalTitleName = defectiveTitle.baseTitle

        // Verify initial state is DEFECTIVE / FAILED
        assertEquals("FAILED", defectiveTitle.status)

        // STEP 2 & 3: Diagnostic -> Repair / Replace
        // Simulate clicking 'Repair / Replace' -> 'Produce New Artifact & Run Validation Pipeline'
        val sanitizedArtifactHash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
        val repairNotes = "Auto-repaired: Produced new artifact (SHA-256 parity: $sanitizedArtifactHash), PCM phonetic validation passed."
        
        // Mutate via repository (exact same path called by ViewModel -> Repository -> Room DAO)
        repository.updateBaseTitleStatus(
            baseTitle = defectiveTitle,
            newStatus = "CURATION REQUIRED",
            notes = repairNotes
        )

        // STEP 4: Observe state in Room DB after Repair / Replace
        val curationRequiredTitle = dao.getAllBaseTitles().first().first { it.id == targetId }
        assertEquals("CURATION REQUIRED", curationRequiredTitle.status)
        assertTrue(curationRequiredTitle.notes.contains("SHA-256 parity"))
        assertTrue(curationRequiredTitle.containsAmbiguity) // Still flagged for Human Governor

        // STEP 5: Human Governor Review Gate -> Commit RELEASE READY
        val governorNotes = "Governor Sign-Off: Acoustic negative space verified. Physical anchors intact. Approved for release."
        repository.updateHumanGovernorDecision(
            baseTitle = curationRequiredTitle,
            notes = governorNotes,
            markVerified = true
        )

        // Observe state in Room DB after Governor approval
        val releaseReadyTitle = dao.getAllBaseTitles().first().first { it.id == targetId }
        assertEquals("RELEASE READY", releaseReadyTitle.status)
        assertFalse(releaseReadyTitle.containsAmbiguity) // Cleared by governor
        assertEquals(governorNotes, releaseReadyTitle.notes)

        // STEP 6: RELEASE / FREEZE Step
        repository.updateBaseTitleStatus(
            baseTitle = releaseReadyTitle,
            newStatus = "RELEASED & FROZEN",
            notes = "Cryptographically frozen & released by Curator. SHA-256 Parity Locked."
        )

        // Observe state in Room DB after Freeze
        val frozenTitle = dao.getAllBaseTitles().first().first { it.id == targetId }
        assertEquals("RELEASED & FROZEN", frozenTitle.status)
        assertEquals("Cryptographically frozen & released by Curator. SHA-256 Parity Locked.", frozenTitle.notes)

        // STEP 7: SIMULATE APP RESTART & DATABASE RELOAD
        // Re-open/fetch database instance and verify ensureCorpusInitialized doesn't overwrite frozen records
        val restartedDatabase = ElyzarethDatabase.getDatabase(context)
        val restartedRepo = CorpusRepository(restartedDatabase.elyzarethDao())
        restartedRepo.ensureCorpusInitialized()

        val reloadedTitle = restartedDatabase.elyzarethDao().getAllBaseTitles().first().first { it.id == targetId }
        
        // VERIFY: Still RELEASED & FROZEN
        assertEquals("RELEASED & FROZEN", reloadedTitle.status)
        assertEquals(originalTitleName, reloadedTitle.baseTitle)
        assertEquals("Cryptographically frozen & released by Curator. SHA-256 Parity Locked.", reloadedTitle.notes)
        assertFalse(reloadedTitle.containsAmbiguity)
    }
}
