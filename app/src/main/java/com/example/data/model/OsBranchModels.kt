package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * ELYZARETH OS — DOMAIN & ROOM PERSISTENCE ENTITIES FOR ALL FUNCTIONAL BRANCHES
 * Reference: ELYZARETH_OS_STRUCTURE_1.0
 */

// 1. Idea & Blueprint Studio (Branch: Creative Production / Stage 01 & 02)
@Entity(tableName = "idea_blueprints")
data class IdeaBlueprintEntity(
    @PrimaryKey val id: String,
    val title: String,
    val conceptIntent: String,
    val songStructure: String, // e.g. "Verse 1 - Chorus - Verse 2 - Bridge - Outro (P2 Gap)"
    val genreAndStyle: String, // e.g. "Dark Acoustic Chamber Folk / 72 BPM"
    val emotionAndTheme: String, // e.g. "Quiet Somatic Recovery / Domestic Stillness"
    val physicalAnchorName: String,
    val primaryLanguage: String,
    val pipelineStageCode: String = "02",
    val status: String = "BLUEPRINT LOCKED",
    val createdTimestampMs: Long = System.currentTimeMillis()
)

// 4. Creative DNA Engine (Branch: Creative Production / Stage 06)
@Entity(tableName = "creative_dna_profiles")
data class CreativeDnaEntity(
    @PrimaryKey val id: String,
    val profileName: String,
    val styleAndVoice: String,
    val imageryControlRules: String,
    val acousticSignature: String, // e.g. "Envelope Class A • T60 0.42s Dry Room"
    val prohibitedTropesPolicy: String,
    val conformanceScore: Int,
    val status: String = "CONFORMANT"
)

// 5. Music Production Console (Branch: Music Production / Stage 07 & 09)
@Entity(tableName = "music_production_tracks")
data class MusicProductionEntity(
    @PrimaryKey val id: String,
    val songTitle: String,
    val linkedBaseTitleId: String,
    val styleEngineeringPrompt: String,
    val externalWorkflowSource: String, // e.g. "Suno v4 Stem Ingest / Studio Acoustic"
    val stemsSummary: String, // e.g. "Vocal (Dry), Felt Piano, Upright Bass, Room Mic"
    val arrangementAndMixNotes: String,
    val bpm: Int,
    val meter: String,
    val t60DecaySeconds: Double = 0.42,
    val status: String = "STEMS INGESTED"
)

// 6. Visual Direction Studio (Branch: Visual Direction / Stage 08)
@Entity(tableName = "visual_direction_assets")
data class VisualDirectionEntity(
    @PrimaryKey val id: String,
    val songTitle: String,
    val assetCategory: String, // Album Artwork, Thumbnail Studio, Lyric Background, Visual Storyboard, Promotional Asset
    val visualConcept: String,
    val colorPaletteSpec: String, // e.g. "Obsidian #0E1015 • Bronze Gold #D4AF37 • Blue Thread #5B9BF4"
    val aspectRatio: String,
    val status: String = "APPROVED"
)

// 8. Forensic Lab (Branch: Forensic Intelligence / Stage 11)
@Entity(tableName = "forensic_reports")
data class ForensicReportEntity(
    @PrimaryKey val id: String,
    val targetTitleId: String,
    val targetTitle: String,
    val lyricSimilarityPercent: Int, // Lower is better (0-8% = Original)
    val audioIntegrityStatus: String, // e.g. "PCM 24-bit/48kHz Intact • Zero Clipping"
    val vocalAnalysisResult: String, // e.g. "Natural Formant Envelope • Zero Synthetic Artifacts"
    val watermarkDetection: String, // e.g. "CLEAN (No External Watermark)"
    val catalogCollisionStatus: String, // e.g. "ZERO COLLISIONS ACROSS 390 TITLES"
    val verdict: String = "VERIFIED CLEAN",
    val auditedTimestampMs: Long = System.currentTimeMillis()
)

// 10. Evidence Vault (Branch: Evidence & Provenance / Stage 04)
@Entity(tableName = "evidence_ledger")
data class EvidenceLedgerEntity(
    @PrimaryKey val id: String,
    val targetId: String,
    val targetTitle: String,
    val evidenceType: String, // ORIGINAL_EVIDENCE, DERIVED_EVIDENCE, GOVERNOR_SIGNATURE
    val sha256Hash: String,
    val cryptographicSignature: String,
    val parentProvenanceHash: String,
    val integrityVerified: Boolean = true,
    val recordedTimestampMs: Long = System.currentTimeMillis()
)

// 11. Asset Registry (Branch: Asset Registry / Stage 10)
@Entity(tableName = "asset_registry")
data class RegisteredAssetEntity(
    @PrimaryKey val id: String,
    val assetCode: String,
    val title: String,
    val assetCategory: String, // AUDIO_ASSET, LYRIC_ASSET, ARTWORK_ASSET, METADATA_MANIFEST
    val linkedSongTitle: String,
    val checksumSha256: String,
    val relationshipGraph: String, // e.g. "Parent: bt_1 -> Lyric: art_101 -> Stem: MP-01"
    val status: String = "REGISTERED"
)

// 12. Master Vault & Player (Branch: Release & Master Vault / Stage 14)
@Entity(tableName = "master_releases")
data class MasterReleaseEntity(
    @PrimaryKey val id: String,
    val releaseCode: String,
    val songTitle: String,
    val primaryLanguage: String,
    val sealedPackageHash: String,
    val atomicSealStatus: String, // SEALED_MASTER, PENDING_G5_LOCK
    val distributionAuthorization: String, // AUTHORIZED_BY_SOVEREIGN, HOLD
    val durationLabel: String,
    val g1ToG5Summary: String,
    val releasedTimestampMs: Long = System.currentTimeMillis()
)
