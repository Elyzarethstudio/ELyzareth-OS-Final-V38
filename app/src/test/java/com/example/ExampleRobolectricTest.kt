package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.CorpusSeedData
import com.example.data.local.ElyzarethDatabase
import com.example.data.model.DEFAULT_PHYSICAL_ANCHORS
import com.example.data.model.WitnessPillar
import com.example.data.repository.CorpusRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Elyzareth OS", appName)
  }

  @Test
  fun `corpus seed data contains baseline 390 titles and 510 artifacts`() {
    val (titles, artifacts) = CorpusSeedData.generateSeedCorpus()
    assertEquals(390, titles.size)
    assertEquals(510, artifacts.size)

    val report = CorpusSeedData.getInitialReport()
    assertEquals(510, report.totalFilesDiscovered)
    assertEquals(390, report.baseTitlesDiscovered)
    assertEquals(505, report.successfullyParsed)
    assertEquals(5, report.unparsed)
    assertEquals(4, report.duplicateCandidates)
    assertEquals(4, report.orphanArtifacts)
    assertEquals(46, report.itemsRequiringHumanReview)
  }

  @Test
  fun `witness trope evaluator catches prohibited metaphors`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = ElyzarethDatabase.getDatabase(context)
    val repo = CorpusRepository(db.elyzarethDao())

    val (purity, violations) = repo.evaluateCustomLyrics("The starlight in my soul creates a celestial symphony")
    assertTrue(violations.isNotEmpty())
    assertTrue(purity < 60)

    val (pureScore, pureViolations) = repo.evaluateCustomLyrics("Cold floorboards under bare heels, salt on the wooden sill.")
    assertTrue(pureViolations.isEmpty())
    assertEquals(100, pureScore)
  }

  @Test
  fun `generate witness lyrics anchors to physical objects`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = ElyzarethDatabase.getDatabase(context)
    val repo = CorpusRepository(db.elyzarethDao())

    val stanzas = repo.generateWitnessLyrics(
      anchor = DEFAULT_PHYSICAL_ANCHORS.first(),
      pillar = WitnessPillar.DOMESTIC,
      meter = "4/4",
      bpm = 72,
      customSeed = ""
    )

    assertEquals(2, stanzas.size)
    assertTrue(stanzas[0].witnessPurityScore >= 95)
    assertTrue(stanzas[0].lines.any { it.contains("thread", ignoreCase = true) || it.contains("floorboards", ignoreCase = true) })
  }
}
