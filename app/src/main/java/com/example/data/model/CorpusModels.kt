package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "base_titles")
data class BaseTitleEntity(
    @PrimaryKey val id: String,
    val baseTitle: String,
    val artifactsCount: Int,
    val primaryLanguage: String,
    val hasLyrics: Boolean,
    val hasMetadata: Boolean,
    val hasAudioRef: Boolean,
    val containsAmbiguity: Boolean,
    val g1Score: String = "NOT MEASURED",
    val g2Score: String = "NOT MEASURED",
    val g3Score: String = "NOT MEASURED",
    val g4Score: String = "NOT MEASURED",
    val g5Score: String = "NOT MEASURED",
    val status: String = "PARSED",
    val notes: String = "",
    val lastObservedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "artifacts")
data class ArtifactEntity(
    @PrimaryKey val id: String,
    val baseTitleId: String,
    val baseTitleSuggested: String,
    val path: String,
    val filename: String,
    val versionLabel: String,
    val artifactType: String, // lyric_txt, lyric_json, metadata, audio_ref
    val status: String,       // PARSED, FAILED, DISCOVERED, VERIFIED, UNVERIFIED
    val fileHash: String,     // SHA-256
    val sizeBytes: Long,
    val lastModifiedMs: Long,
    val extension: String,
    val detectedLanguage: String,
    val hasPhysicalWitnessAnchors: Boolean,
    val contentPreview: String,
    val isOrphan: Boolean = false,
    val isDuplicateCandidate: Boolean = false,
    val requiresHumanReview: Boolean = false,
    val reviewReason: String? = null,
    val parseError: String? = null
)

data class CorpusAuditReport(
    val timestamp: String,
    val totalFilesDiscovered: Int,
    val baseTitlesDiscovered: Int,
    val totalArtifactsDiscovered: Int,
    val successfullyParsed: Int,
    val unparsed: Int,
    val duplicateCandidates: Int,
    val orphanArtifacts: Int,
    val missingExpectedComponents: Int,
    val englishCount: Int,
    val hindiCount: Int,
    val multilingualCount: Int,
    val evidenceAvailableCount: Int,
    val evidenceUnavailableCount: Int,
    val itemsRequiringHumanReview: Int
)

enum class WitnessPillar(val title: String, val description: String) {
    DOMESTIC("Domestic", "Quiet interior objects: thread, salt, porcelain, key, doorframe"),
    ENVIRONMENTAL("Environmental", "Exterior grounded textures: river silt, frost, dry wood, iron rail"),
    RECOVERY("Recovery", "Somatic physical reality: breath, pulse, scar, bone, stillness"),
    CULTURAL("Cultural", "Rooted idioms: Kagaz Ki Naav, Dhaaga, street corner, threshold"),
    TRANSCENDENTAL("Transcendental", "Immanent light, salt water horizon, empty room, negative space")
}

data class PhysicalAnchor(
    val id: String,
    val name: String,
    val hindiAlias: String,
    val pillar: WitnessPillar
)

val DEFAULT_PHYSICAL_ANCHORS = listOf(
    PhysicalAnchor("blue_thread", "Blue Thread", "नीला धागा (Neela Dhaaga)", WitnessPillar.DOMESTIC),
    PhysicalAnchor("dry_salt", "Dry Salt", "सूखा नमक (Sukha Namak)", WitnessPillar.ENVIRONMENTAL),
    PhysicalAnchor("cold_porcelain", "Cold Porcelain", "चीनी मिट्टी (Cheeni Mitti)", WitnessPillar.DOMESTIC),
    PhysicalAnchor("iron_key", "Iron Key", "लोहे की चाबी (Lohe ki Chaabi)", WitnessPillar.DOMESTIC),
    PhysicalAnchor("felt_hammer", "Felt Hammer", "हथौड़ा / स्वर (Piano Felt)", WitnessPillar.RECOVERY),
    PhysicalAnchor("stone_step", "Stone Step", "पत्थर की सीढ़ी (Patthar ki Seedhi)", WitnessPillar.ENVIRONMENTAL),
    PhysicalAnchor("paper_edge", "Paper Edge / Naav", "कागज़ की नाव (Kagaz Ki Naav)", WitnessPillar.CULTURAL),
    PhysicalAnchor("light_glass", "Light Through Glass", "कांच की धूप (Kaanch ki Dhoop)", WitnessPillar.TRANSCENDENTAL),
    PhysicalAnchor("dry_wood", "Dry Wood", "सूखी लकड़ी (Sukhi Lakdi)", WitnessPillar.ENVIRONMENTAL)
)

data class GeneratedLyricStanza(
    val stanzaNumber: Int,
    val lines: List<String>,
    val physicalAnchorUsed: String,
    val witnessPurityScore: Int,
    val prohibitedTropesDetected: List<String>
)
