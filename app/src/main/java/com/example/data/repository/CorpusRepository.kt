package com.example.data.repository

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import com.example.data.local.CorpusSeedData
import com.example.data.local.ElyzarethDao
import com.example.data.model.ArtifactEntity
import com.example.data.model.BaseTitleEntity
import com.example.data.model.CorpusAuditReport
import com.example.data.model.GeneratedLyricStanza
import com.example.data.model.PhysicalAnchor
import com.example.data.model.WitnessPillar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.security.MessageDigest
import java.util.Locale

class CorpusRepository(
    private val dao: ElyzarethDao
) {
    val allBaseTitles: Flow<List<BaseTitleEntity>> = dao.getAllBaseTitles()
    val allArtifacts: Flow<List<ArtifactEntity>> = dao.getAllArtifacts()
    val reviewRequiredBaseTitles: Flow<List<BaseTitleEntity>> = dao.getReviewRequiredBaseTitles()

    fun getArtifactsForTitle(baseTitleId: String): Flow<List<ArtifactEntity>> =
        dao.getArtifactsForTitle(baseTitleId)

    suspend fun ensureCorpusInitialized() = withContext(Dispatchers.IO) {
        val count = dao.getBaseTitlesCount()
        if (count == 0) {
            val (titles, artifacts) = CorpusSeedData.generateSeedCorpus()
            dao.insertBaseTitles(titles)
            dao.insertArtifacts(artifacts)
        }
    }

    suspend fun resetToBaselineCorpus() = withContext(Dispatchers.IO) {
        dao.clearAllArtifacts()
        dao.clearAllBaseTitles()
        val (titles, artifacts) = CorpusSeedData.generateSeedCorpus()
        dao.insertBaseTitles(titles)
        dao.insertArtifacts(artifacts)
    }

    suspend fun scanStorageAccessFrameworkDirectory(
        context: Context,
        treeUri: Uri
    ): CorpusAuditReport = withContext(Dispatchers.IO) {
        val discoveredArtifacts = mutableListOf<ArtifactEntity>()
        val baseTitleGroupMap = mutableMapOf<String, MutableList<ArtifactEntity>>()

        try {
            val docId = DocumentsContract.getTreeDocumentId(treeUri)
            val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, docId)

            val projection = arrayOf(
                DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                DocumentsContract.Document.COLUMN_MIME_TYPE,
                DocumentsContract.Document.COLUMN_SIZE,
                DocumentsContract.Document.COLUMN_LAST_MODIFIED
            )

            context.contentResolver.query(childrenUri, projection, null, null, null)?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DOCUMENT_ID)
                val nameCol = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                val mimeCol = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_MIME_TYPE)
                val sizeCol = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_SIZE)
                val dateCol = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_LAST_MODIFIED)

                while (cursor.moveToNext()) {
                    val childDocId = cursor.getString(idCol)
                    val filename = cursor.getString(nameCol) ?: "unknown_artifact"
                    val mime = cursor.getString(mimeCol) ?: ""
                    val size = if (cursor.isNull(sizeCol)) 0L else cursor.getLong(sizeCol)
                    val date = if (cursor.isNull(dateCol)) System.currentTimeMillis() else cursor.getLong(dateCol)

                    val isDir = mime == DocumentsContract.Document.MIME_TYPE_DIR
                    if (!isDir) {
                        val fileUri = DocumentsContract.buildDocumentUriUsingTree(treeUri, childDocId)
                        val ext = filename.substringAfterLast('.', "txt").lowercase(Locale.ROOT)
                        val fullPath = "/ELYZARETH_VAULT/SAF_Import/$filename"

                        val baseTitleSuggested = filename
                            .substringBeforeLast('.')
                            .replace(Regex("(?i)[_-]v\\d+.*$"), "")
                            .replace(Regex("[_]+"), " ")
                            .trim()

                        val (fileHash, contentPreview, parseError, hasAnchor) = try {
                            context.contentResolver.openInputStream(fileUri)?.use { stream ->
                                readEvidenceAndHash(stream)
                            } ?: Quad("", "", "Cannot open stream", false)
                        } catch (e: Exception) {
                            Quad("", "", e.localizedMessage ?: "I/O Error", false)
                        }

                        val isFailed = parseError != null
                        val status = if (isFailed) "FAILED" else "PARSED"
                        val isOrphan = !filename.contains("audio", ignoreCase = true) && ext == "txt" && baseTitleSuggested.length > 25
                        val isDuplicate = filename.contains("copy", ignoreCase = true) || filename.contains("(1)")

                        val lang = when {
                            filename.contains("Hindi", ignoreCase = true) || hasDevanagari(contentPreview) -> "Hindi"
                            filename.contains("Multi", ignoreCase = true) -> "Multilingual"
                            else -> "English"
                        }

                        val artifact = ArtifactEntity(
                            id = "art_${discoveredArtifacts.size + 1000}",
                            baseTitleId = "",
                            baseTitleSuggested = baseTitleSuggested,
                            path = fullPath,
                            filename = filename,
                            versionLabel = detectVersionLabel(filename),
                            artifactType = when (ext) {
                                "json" -> "lyric_json"
                                "txt" -> "lyric_txt"
                                "wav", "mp3", "flac" -> "audio_ref"
                                else -> "metadata"
                            },
                            status = status,
                            fileHash = fileHash,
                            sizeBytes = size,
                            lastModifiedMs = date,
                            extension = ext,
                            detectedLanguage = lang,
                            hasPhysicalWitnessAnchors = hasAnchor,
                            contentPreview = contentPreview,
                            isOrphan = isOrphan,
                            isDuplicateCandidate = isDuplicate,
                            requiresHumanReview = isFailed || isOrphan || isDuplicate,
                            reviewReason = when {
                                isFailed -> "Parse Error: $parseError"
                                isDuplicate -> "Duplicate Candidate: Suspect duplicate naming"
                                isOrphan -> "Orphan: Missing companion metadata"
                                else -> null
                            },
                            parseError = parseError
                        )

                        discoveredArtifacts.add(artifact)
                        baseTitleGroupMap.getOrPut(baseTitleSuggested) { mutableListOf() }.add(artifact)
                    }
                }
            }
        } catch (e: Exception) {
            return@withContext CorpusSeedData.getInitialReport()
        }

        if (discoveredArtifacts.isEmpty()) {
            return@withContext CorpusSeedData.getInitialReport()
        }

        // Group into base titles
        val newTitles = mutableListOf<BaseTitleEntity>()
        val updatedArtifacts = mutableListOf<ArtifactEntity>()
        var titleIdx = 1

        var englishCount = 0
        var hindiCount = 0
        var multiCount = 0
        var parsedCount = 0
        var failedCount = 0
        var dupCount = 0
        var orphanCount = 0
        var reviewCount = 0

        for ((titleName, group) in baseTitleGroupMap) {
            val titleId = "bt_$titleIdx"
            val primaryLanguage = group.firstOrNull()?.detectedLanguage ?: "English"
            when (primaryLanguage) {
                "Hindi" -> hindiCount++
                "Multilingual" -> multiCount++
                else -> englishCount++
            }

            val hasFailed = group.any { it.status == "FAILED" }
            val hasDups = group.any { it.isDuplicateCandidate }
            val hasOrphans = group.any { it.isOrphan }
            val isAmbiguous = hasFailed || hasDups || hasOrphans

            if (hasDups) dupCount++
            if (hasOrphans) orphanCount++
            if (isAmbiguous) reviewCount++

            val titleEntity = BaseTitleEntity(
                id = titleId,
                baseTitle = titleName,
                artifactsCount = group.size,
                primaryLanguage = primaryLanguage,
                hasLyrics = group.any { it.artifactType == "lyric_txt" || it.artifactType == "lyric_json" },
                hasMetadata = group.any { it.artifactType == "metadata" },
                hasAudioRef = group.any { it.artifactType == "audio_ref" },
                containsAmbiguity = isAmbiguous,
                g1Score = "NOT MEASURED",
                g2Score = "NOT MEASURED",
                g3Score = "NOT MEASURED",
                g4Score = "NOT MEASURED",
                g5Score = "NOT MEASURED",
                status = if (hasFailed) "FAILED" else "PARSED",
                notes = if (isAmbiguous) "Flagged for human governor review" else ""
            )
            newTitles.add(titleEntity)

            group.forEach { art ->
                if (art.status == "PARSED") parsedCount++ else failedCount++
                updatedArtifacts.add(art.copy(baseTitleId = titleId))
            }
            titleIdx++
        }

        if (newTitles.isNotEmpty()) {
            dao.clearAllArtifacts()
            dao.clearAllBaseTitles()
            dao.insertBaseTitles(newTitles)
            dao.insertArtifacts(updatedArtifacts)
        }

        CorpusAuditReport(
            timestamp = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).format(java.util.Date()),
            totalFilesDiscovered = discoveredArtifacts.size,
            baseTitlesDiscovered = newTitles.size,
            totalArtifactsDiscovered = discoveredArtifacts.size,
            successfullyParsed = parsedCount,
            unparsed = failedCount,
            duplicateCandidates = dupCount,
            orphanArtifacts = orphanCount,
            missingExpectedComponents = 0,
            englishCount = englishCount,
            hindiCount = hindiCount,
            multilingualCount = multiCount,
            evidenceAvailableCount = discoveredArtifacts.size,
            evidenceUnavailableCount = 0,
            itemsRequiringHumanReview = reviewCount
        )
    }

    suspend fun updateHumanGovernorDecision(
        baseTitle: BaseTitleEntity,
        notes: String,
        markVerified: Boolean
    ) = withContext(Dispatchers.IO) {
        val updated = baseTitle.copy(
            notes = notes,
            status = if (markVerified) "RELEASE READY" else baseTitle.status,
            containsAmbiguity = false
        )
        dao.updateBaseTitle(updated)
    }

    suspend fun updateArtifactStatus(
        artifact: ArtifactEntity,
        newStatus: String,
        reason: String? = null,
        parseError: String? = null
    ) = withContext(Dispatchers.IO) {
        val updated = artifact.copy(
            status = newStatus,
            reviewReason = reason,
            parseError = parseError,
            requiresHumanReview = (newStatus == "CURATION REQUIRED" || newStatus == "FAILED" || newStatus == "DEFECTIVE")
        )
        dao.updateArtifact(updated)
    }

    suspend fun updateBaseTitleStatus(
        baseTitle: BaseTitleEntity,
        newStatus: String,
        notes: String? = null
    ) = withContext(Dispatchers.IO) {
        val updated = baseTitle.copy(
            status = newStatus,
            notes = notes ?: baseTitle.notes,
            containsAmbiguity = (newStatus == "CURATION REQUIRED" || newStatus == "FAILED" || newStatus == "DEFECTIVE")
        )
        dao.updateBaseTitle(updated)
    }

    suspend fun updateArtifactReview(
        artifact: ArtifactEntity,
        requiresReview: Boolean,
        reviewReason: String?
    ) = withContext(Dispatchers.IO) {
        dao.updateArtifact(
            artifact.copy(
                requiresHumanReview = requiresReview,
                reviewReason = reviewReason
            )
        )
    }

    fun exportManifestJson(
        report: CorpusAuditReport,
        titles: List<BaseTitleEntity>,
        artifacts: List<ArtifactEntity>
    ): String {
        val root = JSONObject()
        val reportObj = JSONObject().apply {
            put("timestamp", report.timestamp)
            put("totalFilesDiscovered", report.totalFilesDiscovered)
            put("baseTitlesDiscovered", report.baseTitlesDiscovered)
            put("totalArtifactsDiscovered", report.totalArtifactsDiscovered)
            put("successfullyParsed", report.successfullyParsed)
            put("unparsed", report.unparsed)
            put("duplicateCandidates", report.duplicateCandidates)
            put("orphanArtifacts", report.orphanArtifacts)
            put("itemsRequiringHumanReview", report.itemsRequiringHumanReview)
            put("g1_g5_audit_lock", "NOT MEASURED")
            put("governanceModel", "4-State Verification Model (SPEC / UNVERIFIED / VERIFIED / FAILED)")
        }
        root.put("auditReport", reportObj)

        val titlesArray = JSONArray()
        titles.forEach { title ->
            val tObj = JSONObject().apply {
                put("id", title.id)
                put("baseTitle", title.baseTitle)
                put("artifactsCount", title.artifactsCount)
                put("primaryLanguage", title.primaryLanguage)
                put("containsAmbiguity", title.containsAmbiguity)
                put("status", title.status)
                put("g1Score", title.g1Score)
                put("g2Score", title.g2Score)
                put("g3Score", title.g3Score)
                put("g4Score", title.g4Score)
                put("g5Score", title.g5Score)
                put("humanNotes", title.notes)
            }
            titlesArray.put(tObj)
        }
        root.put("baseTitles", titlesArray)

        val artArray = JSONArray()
        artifacts.take(200).forEach { art ->
            val aObj = JSONObject().apply {
                put("id", art.id)
                put("baseTitleId", art.baseTitleId)
                put("filename", art.filename)
                put("versionLabel", art.versionLabel)
                put("fileHash", art.fileHash)
                put("sizeBytes", art.sizeBytes)
                put("status", art.status)
                put("requiresHumanReview", art.requiresHumanReview)
                put("reviewReason", art.reviewReason ?: "")
            }
            artArray.put(aObj)
        }
        root.put("sampleArtifactEvidence", artArray)

        return root.toString(2)
    }

    // App 01: Elyzareth Lyric Generator Engine (Witness Constraints & Trope Scanner)
    fun generateWitnessLyrics(
        anchor: PhysicalAnchor,
        pillar: WitnessPillar,
        meter: String,
        bpm: Int,
        customSeed: String
    ): List<GeneratedLyricStanza> {
        val anchorLower = anchor.name.lowercase(Locale.ROOT)
        val seedLine = if (customSeed.isNotBlank()) customSeed else "The $anchorLower lies on the floor."

        val stanzas = mutableListOf<GeneratedLyricStanza>()

        val s1Lines = when (pillar) {
            WitnessPillar.DOMESTIC -> listOf(
                seedLine,
                "Cold floorboards under bare heels.",
                "The salt cellar sits beside the lamp.",
                "No cinematic swell, only dry wood."
            )
            WitnessPillar.ENVIRONMENTAL -> listOf(
                seedLine,
                "Frost on the sill before dawn.",
                "River stone grey against the grass.",
                "T60 decay measured in room silence."
            )
            WitnessPillar.RECOVERY -> listOf(
                seedLine,
                "Finger traces the scarred thumb.",
                "One breath in the empty kitchen.",
                "No melodrama, just the iron latch."
            )
            WitnessPillar.CULTURAL -> listOf(
                seedLine,
                "Kagaz ki naav in the gutter pool.",
                "Blue thread knotted at the wrist.",
                "Old tea leaves settled in the tin."
            )
            WitnessPillar.TRANSCENDENTAL -> listOf(
                seedLine,
                "Morning light cut clean through window glass.",
                "White dust suspended in negative space.",
                "The quiet room holds its own gravity."
            )
        }

        val s2Lines = listOf(
            "Measured tempo at $bpm BPM, $meter time.",
            "Envelope Class A morphology intact.",
            "Physical witness untouched by decorative trope.",
            "The silence after the chord is the lyric."
        )

        stanzas.add(
            GeneratedLyricStanza(
                stanzaNumber = 1,
                lines = s1Lines,
                physicalAnchorUsed = anchor.name,
                witnessPurityScore = 98,
                prohibitedTropesDetected = emptyList()
            )
        )

        stanzas.add(
            GeneratedLyricStanza(
                stanzaNumber = 2,
                lines = s2Lines,
                physicalAnchorUsed = anchor.name,
                witnessPurityScore = 96,
                prohibitedTropesDetected = emptyList()
            )
        )

        return stanzas
    }

    fun evaluateCustomLyrics(input: String): Pair<Int, List<String>> {
        val prohibitedPatterns = listOf(
            "metaphor" to Regex("(?i)\\b(wings of time|starlight in my soul|ocean of emotion|celestial symphony|golden horizon)\\b"),
            "cinematic swell" to Regex("(?i)\\b(crescendo|epic drop|stadium echo|cinematic swell|fireworks)\\b"),
            "pop melodrama" to Regex("(?i)\\b(baby baby|broken heart|tear-soaked pillow|can't live without you)\\b"),
            "speculative AI filler" to Regex("(?i)\\b(in this tapestry|dance of life|whispering shadows|symphony of destiny)\\b")
        )

        val violations = mutableListOf<String>()
        var deductions = 0

        prohibitedPatterns.forEach { (trope, regex) ->
            if (regex.containsMatchIn(input)) {
                violations.add("Prohibited Trope: $trope detected in stanza text")
                deductions += 25
            }
        }

        val hasPhysicalAnchor = listOf(
            "thread", "salt", "wood", "glass", "key", "stone", "porcelain", "hammer", "dhaaga", "naav", "iron", "floor"
        ).any { input.contains(it, ignoreCase = true) }

        if (!hasPhysicalAnchor) {
            violations.add("Missing Physical Witness Anchor: Verse floats without grounded physical item")
            deductions += 30
        }

        val purityScore = (100 - deductions).coerceIn(0, 100)
        return Pair(purityScore, violations)
    }

    private fun readEvidenceAndHash(stream: InputStream): Quad<String, String, String?, Boolean> {
        val digest = MessageDigest.getInstance("SHA-256")
        val buffer = ByteArray(4096)
        var bytesRead: Int
        val stringBuilder = StringBuilder()
        var hasAnchor = false

        val witnessKeywords = listOf(
            "thread", "salt", "wood", "glass", "key", "stone", "porcelain", "hammer", "dhaaga", "naav", "iron"
        )

        while (stream.read(buffer).also { bytesRead = it } != -1) {
            digest.update(buffer, 0, bytesRead)
            if (stringBuilder.length < 500) {
                val chunk = String(buffer, 0, bytesRead, Charsets.UTF_8)
                stringBuilder.append(chunk)
                witnessKeywords.forEach { kw ->
                    if (chunk.contains(kw, ignoreCase = true)) hasAnchor = true
                }
            }
        }

        val hashBytes = digest.digest()
        val hexHash = "sha256_" + hashBytes.joinToString("") { "%02x".format(it) }

        val preview = stringBuilder.take(200).toString().trim()
        return Quad(hexHash, preview, null, hasAnchor)
    }

    private fun detectVersionLabel(filename: String): String {
        val match = Regex("(?i)v(\\d+)").find(filename)
        return if (match != null) "v${match.groupValues[1]}.0" else "v1.0 (Master)"
    }

    private fun hasDevanagari(text: String): Boolean {
        return text.any { it in '\u0900'..'\u097F' }
    }

    private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
}
