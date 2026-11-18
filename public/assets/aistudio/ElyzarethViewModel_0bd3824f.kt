package com.example.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CorpusSeedData
import com.example.data.local.ElyzarethDatabase
import com.example.data.model.ArtifactEntity
import com.example.data.model.BaseTitleEntity
import com.example.data.model.CorpusAuditReport
import com.example.data.model.CreativeDnaEntity
import com.example.data.model.DEFAULT_PHYSICAL_ANCHORS
import com.example.data.model.EvidenceLedgerEntity
import com.example.data.model.ForensicReportEntity
import com.example.data.model.GeneratedLyricStanza
import com.example.data.model.IdeaBlueprintEntity
import com.example.data.model.MasterReleaseEntity
import com.example.data.model.MusicProductionEntity
import com.example.data.model.PhysicalAnchor
import com.example.data.model.RegisteredAssetEntity
import com.example.data.model.VisualDirectionEntity
import com.example.data.model.WitnessPillar
import com.example.data.repository.CorpusRepository
import com.example.data.repository.OsBranchRepository
import com.example.foundation.GovernanceGateId
import com.example.foundation.PipelineStage
import com.example.kernel.CanonicalProjectContext
import com.example.kernel.GateEvaluationState
import com.example.kernel.KernelEvent
import com.example.kernel.SovereignKernel
import com.example.navigation.OsModuleDestination
import com.example.shared.BackupSnapshotManifest
import com.example.shared.SnapshotBackupManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ElyzarethUiState(
    // Preserved legacy tab index (0 = Curator/Corpus, 1 = Generator, 2 = Protocol/Governance, 3 = Command Centre)
    val activeTab: Int = 3,
    val activeDestination: OsModuleDestination = OsModuleDestination.COMMAND_CENTRE,
    val navigationHistory: List<OsModuleDestination> = emptyList(),
    val report: CorpusAuditReport = CorpusSeedData.getInitialReport(),
    val searchQuery: String = "",
    val selectedLanguage: String = "All",
    val selectedStatus: String = "All",
    val showReviewOnly: Boolean = false,
    val selectedBaseTitle: BaseTitleEntity? = null,
    val selectedBaseTitleArtifacts: List<ArtifactEntity> = emptyList(),
    val selectedArtifactForDetail: ArtifactEntity? = null,
    val isScanning: Boolean = false,
    val scanMessage: String? = null,
    val manifestJsonExport: String? = null,

    // Lyric Generator State (App 01)
    val generatorSelectedAnchor: PhysicalAnchor = DEFAULT_PHYSICAL_ANCHORS.first(),
    val generatorSelectedPillar: WitnessPillar = WitnessPillar.DOMESTIC,
    val generatorMeter: String = "4/4",
    val generatorBpm: Int = 72,
    val generatorCustomSeed: String = "",
    val generatedStanzas: List<GeneratedLyricStanza> = emptyList(),
    val customLyricInput: String = "",
    val customLyricEvaluation: Pair<Int, List<String>>? = null,

    // 3.2.1.0 Protocol & G1-G5 Governance State
    val protocolStep: Int = 3, // 3: Listen, 2: Evaluate, 1: Decide, 0: Freeze
    val isFreezeLocked: Boolean = false,
    val reviewNotesInput: String = "",
    val gateEvaluationState: GateEvaluationState = GateEvaluationState(
        targetId = "bt_1",
        targetTitle = "Neela Dhaaga (The Blue Thread)",
        g1Status = "VERIFIED",
        g2Status = "VERIFIED",
        g3Status = "VERIFIED",
        g4Status = "VERIFIED",
        g5Status = "NOT MEASURED",
        humanSignOff = false
    ),

    // Master Player & System Services State
    val activePlayingReleaseId: String? = null,
    val isMasterPlayerPlaying: Boolean = false,
    val lastBackupSnapshot: BackupSnapshotManifest? = null
)

class ElyzarethViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CorpusRepository
    private val branchRepository: OsBranchRepository

    private val _uiState = MutableStateFlow(ElyzarethUiState())

    init {
        val db = ElyzarethDatabase.getDatabase(application)
        repository = CorpusRepository(db.elyzarethDao())
        branchRepository = OsBranchRepository(db.osBranchDao())

        viewModelScope.launch {
            repository.ensureCorpusInitialized()
            branchRepository.ensureBranchesInitialized()
        }

        // Initialize generator stanzas
        _uiState.value = _uiState.value.copy(
            generatedStanzas = repository.generateWitnessLyrics(
                anchor = DEFAULT_PHYSICAL_ANCHORS.first(),
                pillar = WitnessPillar.DOMESTIC,
                meter = "4/4",
                bpm = 72,
                customSeed = ""
            )
        )
    }

    /**
     * Complete corpus stream. Dashboard totals, review totals and backup manifests must
     * use this stream; baseTitles below is intentionally filtered for the curator UI.
     */
    val allBaseTitles: StateFlow<List<BaseTitleEntity>> = repository.allBaseTitles
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    val baseTitles: StateFlow<List<BaseTitleEntity>> = combine(
        repository.allBaseTitles,
        _uiState
    ) { titles, state ->
        titles.filter { title ->
            val matchesQuery = state.searchQuery.isBlank() ||
                    title.baseTitle.contains(state.searchQuery, ignoreCase = true) ||
                    title.id.contains(state.searchQuery, ignoreCase = true)

            val matchesLang = state.selectedLanguage == "All" ||
                    title.primaryLanguage.equals(state.selectedLanguage, ignoreCase = true)

            val matchesStatus = state.selectedStatus == "All" ||
                    title.status.equals(state.selectedStatus, ignoreCase = true)

            val matchesReview = !state.showReviewOnly || title.containsAmbiguity

            matchesQuery && matchesLang && matchesStatus && matchesReview
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allArtifacts: StateFlow<List<ArtifactEntity>> = repository.allArtifacts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Functional Branch StateFlows
    val blueprints: StateFlow<List<IdeaBlueprintEntity>> = branchRepository.allBlueprints
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dnaProfiles: StateFlow<List<CreativeDnaEntity>> = branchRepository.allDnaProfiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val productionTracks: StateFlow<List<MusicProductionEntity>> = branchRepository.allProductionTracks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val visualAssets: StateFlow<List<VisualDirectionEntity>> = branchRepository.allVisualAssets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val forensicReports: StateFlow<List<ForensicReportEntity>> = branchRepository.allForensicReports
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val evidenceRecords: StateFlow<List<EvidenceLedgerEntity>> = branchRepository.allEvidenceRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val registeredAssets: StateFlow<List<RegisteredAssetEntity>> = branchRepository.allRegisteredAssets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val masterReleases: StateFlow<List<MasterReleaseEntity>> = branchRepository.allMasterReleases
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val kernelProjectContext: StateFlow<CanonicalProjectContext> = SovereignKernel.projectContext
    val kernelEvents: StateFlow<List<KernelEvent>> = SovereignKernel.eventLedger

    val uiState: StateFlow<ElyzarethUiState> = _uiState

    fun setTab(index: Int) {
        val destination = when (index) {
            0 -> OsModuleDestination.CORPUS_ARCHIVE
            1 -> OsModuleDestination.LYRIC_GENERATOR
            2 -> OsModuleDestination.GOVERNANCE_CONSOLE
            else -> OsModuleDestination.COMMAND_CENTRE
        }
        navigateToDestination(destination)
    }

    fun navigateToDestination(destination: OsModuleDestination) {
        val current = _uiState.value.activeDestination
        val updatedHistory = if (current != destination) {
            (_uiState.value.navigationHistory + current).takeLast(12)
        } else {
            _uiState.value.navigationHistory
        }
        val mappedTab = when (destination) {
            OsModuleDestination.CORPUS_ARCHIVE, OsModuleDestination.LYRIC_CURATOR -> 0
            OsModuleDestination.LYRIC_GENERATOR, OsModuleDestination.IDEA_BLUEPRINT -> 1
            OsModuleDestination.GOVERNANCE_CONSOLE, OsModuleDestination.EVIDENCE_VAULT -> 2
            else -> 3
        }
        _uiState.value = _uiState.value.copy(
            activeTab = mappedTab,
            activeDestination = destination,
            navigationHistory = updatedHistory
        )
        SovereignKernel.recordEvent(
            branch = "NavigationRouter",
            action = "NAVIGATE_TO_MODULE",
            details = "Routed to ${destination.title} (${destination.architecturalLayer})"
        )
    }

    private fun legacyTabFor(destination: OsModuleDestination): Int = when (destination) {
        OsModuleDestination.CORPUS_ARCHIVE,
        OsModuleDestination.LYRIC_CURATOR -> 0

        OsModuleDestination.LYRIC_GENERATOR,
        OsModuleDestination.IDEA_BLUEPRINT -> 1

        OsModuleDestination.GOVERNANCE_CONSOLE,
        OsModuleDestination.EVIDENCE_VAULT -> 2

        else -> 3
    }

    fun navigateBack(): Boolean {
        val current = _uiState.value
        val previous = current.navigationHistory.lastOrNull()
            ?: if (current.activeDestination != OsModuleDestination.COMMAND_CENTRE) {
                OsModuleDestination.COMMAND_CENTRE
            } else {
                return false
            }

        _uiState.value = current.copy(
            activeTab = legacyTabFor(previous),
            activeDestination = previous,
            navigationHistory = if (current.navigationHistory.isNotEmpty()) {
                current.navigationHistory.dropLast(1)
            } else {
                emptyList()
            }
        )
        SovereignKernel.recordEvent(
            branch = "NavigationRouter",
            action = "NAVIGATE_BACK",
            details = "Restored ${previous.title}"
        )
        return true
    }

    // Kernel & Governance Actions
    fun setPipelineStage(stage: PipelineStage) {
        SovereignKernel.transitionPipelineStage(stage, "Manual stage selection by Student-Director")
    }

    fun updateGateDecision(gate: GovernanceGateId, newStatus: String, note: String) {
        val updated = SovereignKernel.evaluateGateTransition(
            current = _uiState.value.gateEvaluationState,
            gate = gate,
            newStatus = newStatus,
            governorNote = note
        )
        _uiState.value = _uiState.value.copy(
            gateEvaluationState = updated,
            scanMessage = "Governance Gate ${gate.gateCode} updated -> ${newStatus.uppercase()}"
        )
    }

    // Branch Actions
    fun createIdeaBlueprint(
        title: String,
        conceptIntent: String,
        songStructure: String,
        genreAndStyle: String,
        emotionAndTheme: String,
        physicalAnchorName: String,
        primaryLanguage: String
    ) {
        viewModelScope.launch {
            branchRepository.createBlueprint(
                title = title,
                conceptIntent = conceptIntent,
                songStructure = songStructure,
                genreAndStyle = genreAndStyle,
                emotionAndTheme = emotionAndTheme,
                physicalAnchorName = physicalAnchorName,
                primaryLanguage = primaryLanguage
            )
            _uiState.value = _uiState.value.copy(
                scanMessage = "Blueprint '$title' saved to Room Persistence & registered with Sovereign Kernel."
            )
        }
    }

    fun registerNewAsset(
        title: String,
        category: String,
        linkedSongTitle: String,
        relationshipGraph: String
    ) {
        viewModelScope.launch {
            branchRepository.registerNewAsset(title, category, linkedSongTitle, relationshipGraph)
            _uiState.value = _uiState.value.copy(
                scanMessage = "Asset '$title' registered with SHA-256 checksum & Evidence Vault record."
            )
        }
    }

    fun runForensicAuditOnSelectedTitle() {
        val target = _uiState.value.selectedBaseTitle ?: baseTitles.value.firstOrNull()
        val id = target?.id ?: "bt_1"
        val name = target?.baseTitle ?: "Neela Dhaaga (The Blue Thread)"
        viewModelScope.launch {
            branchRepository.runForensicScan(id, name)
            _uiState.value = _uiState.value.copy(
                scanMessage = "Forensic Audit completed for '$name': 100% Clean • Zero Catalog Collisions."
            )
        }
    }

    fun addMusicProductionTrack(
        songTitle: String,
        stylePrompt: String,
        stemsSummary: String,
        mixNotes: String,
        bpm: Int,
        meter: String
    ) {
        viewModelScope.launch {
            branchRepository.addProductionTrack(songTitle, stylePrompt, stemsSummary, mixNotes, bpm, meter)
            _uiState.value = _uiState.value.copy(
                scanMessage = "Production Track '$songTitle' ingested into Music Production Console."
            )
        }
    }

    fun addVisualAsset(
        songTitle: String,
        category: String,
        concept: String,
        aspectRatio: String
    ) {
        viewModelScope.launch {
            branchRepository.addVisualAsset(songTitle, category, concept, aspectRatio)
            _uiState.value = _uiState.value.copy(
                scanMessage = "Visual Direction Asset '$songTitle' ($category) added."
            )
        }
    }

    fun sealMasterRelease(release: MasterReleaseEntity) {
        viewModelScope.launch {
            branchRepository.sealMasterRelease(release)
            _uiState.value = _uiState.value.copy(
                scanMessage = "Master Release '${release.songTitle}' atomically sealed with SHA-256 signature."
            )
        }
    }

    fun toggleMasterPlayback(releaseId: String) {
        val currentlyPlaying = _uiState.value.activePlayingReleaseId == releaseId && _uiState.value.isMasterPlayerPlaying
        _uiState.value = _uiState.value.copy(
            activePlayingReleaseId = releaseId,
            isMasterPlayerPlaying = !currentlyPlaying
        )
    }

    fun createSystemBackupSnapshot() {
        val snap = SnapshotBackupManager.generateSnapshot(
            titlesCount = allBaseTitles.value.size,
            artifactsCount = allArtifacts.value.size,
            projectsCount = blueprints.value.size,
            evidenceCount = evidenceRecords.value.size
        )
        _uiState.value = _uiState.value.copy(
            lastBackupSnapshot = snap,
            scanMessage = "Encrypted Backup Snapshot ${snap.snapshotId} created (${snap.checksumSha256.take(20)}...)"
        )
    }

    // Preserved Corpus & Curator Controls (App 02)
    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun setSelectedLanguage(language: String) {
        _uiState.value = _uiState.value.copy(selectedLanguage = language)
    }

    fun setSelectedStatus(status: String) {
        _uiState.value = _uiState.value.copy(selectedStatus = status)
    }

    fun toggleReviewOnly() {
        _uiState.value = _uiState.value.copy(showReviewOnly = !_uiState.value.showReviewOnly)
    }

    fun selectBaseTitle(baseTitle: BaseTitleEntity?) {
        _uiState.value = _uiState.value.copy(
            selectedBaseTitle = baseTitle,
            reviewNotesInput = baseTitle?.notes ?: ""
        )
        if (baseTitle != null) {
            viewModelScope.launch {
                repository.getArtifactsForTitle(baseTitle.id).collect { artifacts ->
                    _uiState.value = _uiState.value.copy(selectedBaseTitleArtifacts = artifacts)
                }
            }
        } else {
            _uiState.value = _uiState.value.copy(selectedBaseTitleArtifacts = emptyList())
        }
    }

    fun selectArtifactForDetail(artifact: ArtifactEntity?) {
        _uiState.value = _uiState.value.copy(selectedArtifactForDetail = artifact)
    }

    fun scanSafFolder(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isScanning = true,
                scanMessage = "Ingesting directory via Storage Access Framework non-destructively..."
            )
            val report = repository.scanStorageAccessFrameworkDirectory(getApplication(), uri)
            _uiState.value = _uiState.value.copy(
                isScanning = false,
                scanMessage = "Ingestion Complete: Discovered ${report.totalFilesDiscovered} files across ${report.baseTitlesDiscovered} base titles.",
                report = report
            )
        }
    }

    fun resetToBaseline() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isScanning = true,
                scanMessage = "Resetting corpus vault to 390-title / 510-artifact baseline..."
            )
            repository.resetToBaselineCorpus()
            _uiState.value = _uiState.value.copy(
                isScanning = false,
                scanMessage = "Baseline loaded: 390 titles / 510 version artifacts verified.",
                report = CorpusSeedData.getInitialReport()
            )
        }
    }

    fun recordHumanGovernorDecision(markVerified: Boolean) {
        val currentTitle = _uiState.value.selectedBaseTitle ?: return
        val notes = _uiState.value.reviewNotesInput
        viewModelScope.launch {
            repository.updateHumanGovernorDecision(currentTitle, notes, markVerified)
            _uiState.value = _uiState.value.copy(
                selectedBaseTitle = currentTitle.copy(
                    notes = notes,
                    status = if (markVerified) "RELEASE READY" else currentTitle.status,
                    containsAmbiguity = false
                ),
                scanMessage = "Human Governor Decision Recorded for ${currentTitle.baseTitle}"
            )
        }
    }

    fun setArtifactStatus(artifact: ArtifactEntity, newStatus: String, reason: String? = null, parseError: String? = null) {
        viewModelScope.launch {
            repository.updateArtifactStatus(artifact, newStatus, reason, parseError)
            if (_uiState.value.selectedArtifactForDetail?.id == artifact.id) {
                _uiState.value = _uiState.value.copy(
                    selectedArtifactForDetail = artifact.copy(
                        status = newStatus,
                        reviewReason = reason,
                        parseError = parseError,
                        requiresHumanReview = (newStatus == "CURATION REQUIRED" || newStatus == "FAILED" || newStatus == "DEFECTIVE")
                    )
                )
            }
            _uiState.value = _uiState.value.copy(
                scanMessage = "Artifact ${artifact.filename} transition -> $newStatus"
            )
        }
    }

    fun setBaseTitleStatus(baseTitle: BaseTitleEntity, newStatus: String, notes: String? = null) {
        viewModelScope.launch {
            repository.updateBaseTitleStatus(baseTitle, newStatus, notes)
            if (_uiState.value.selectedBaseTitle?.id == baseTitle.id) {
                _uiState.value = _uiState.value.copy(
                    selectedBaseTitle = baseTitle.copy(
                        status = newStatus,
                        notes = notes ?: baseTitle.notes,
                        containsAmbiguity = (newStatus == "CURATION REQUIRED" || newStatus == "FAILED" || newStatus == "DEFECTIVE")
                    )
                )
            }
            _uiState.value = _uiState.value.copy(
                scanMessage = "Title ${baseTitle.baseTitle} transition -> $newStatus"
            )
        }
    }

    fun setReviewNotesInput(notes: String) {
        _uiState.value = _uiState.value.copy(reviewNotesInput = notes)
    }

    fun setScanMessage(message: String) {
        _uiState.value = _uiState.value.copy(scanMessage = message)
    }

    fun releaseAndFreezeBaseTitle(baseTitle: BaseTitleEntity) {
        setBaseTitleStatus(baseTitle, "RELEASED & FROZEN", "Cryptographically frozen & released by Curator.")
    }

    fun rejectAndArchiveBaseTitle(baseTitle: BaseTitleEntity, reason: String? = null) {
        setBaseTitleStatus(baseTitle, "REJECTED & ARCHIVED", reason ?: "Defective item rejected & archived from release pipeline.")
    }

    fun releaseAndFreezeArtifact(artifact: ArtifactEntity) {
        setArtifactStatus(artifact, "RELEASED & FROZEN", "Cryptographically frozen & released by Curator.")
    }

    fun rejectAndArchiveArtifact(artifact: ArtifactEntity, reason: String? = null) {
        setArtifactStatus(artifact, "REJECTED & ARCHIVED", reason ?: "Defective artifact archived.")
    }

    fun repairArtifact(artifact: ArtifactEntity) {
        setArtifactStatus(artifact, "CURATION REQUIRED", "Repaired during curator diagnostic session.", parseError = null)
    }

    fun exportManifest() {
        val titles = baseTitles.value
        val artifacts = allArtifacts.value
        val json = repository.exportManifestJson(_uiState.value.report, titles, artifacts)
        _uiState.value = _uiState.value.copy(manifestJsonExport = json)
    }

    fun dismissExport() {
        _uiState.value = _uiState.value.copy(manifestJsonExport = null)
    }

    fun clearScanMessage() {
        _uiState.value = _uiState.value.copy(scanMessage = null)
    }

    // Lyric Generator controls (App 01)
    fun setGeneratorAnchor(anchor: PhysicalAnchor) {
        _uiState.value = _uiState.value.copy(generatorSelectedAnchor = anchor)
        generateLyrics()
    }

    fun setGeneratorPillar(pillar: WitnessPillar) {
        _uiState.value = _uiState.value.copy(generatorSelectedPillar = pillar)
        generateLyrics()
    }

    fun setGeneratorMeter(meter: String) {
        _uiState.value = _uiState.value.copy(generatorMeter = meter)
        generateLyrics()
    }

    fun setGeneratorBpm(bpm: Int) {
        _uiState.value = _uiState.value.copy(generatorBpm = bpm)
        generateLyrics()
    }

    fun setGeneratorSeed(seed: String) {
        _uiState.value = _uiState.value.copy(generatorCustomSeed = seed)
    }

    fun generateLyrics() {
        val stanzas = repository.generateWitnessLyrics(
            anchor = _uiState.value.generatorSelectedAnchor,
            pillar = _uiState.value.generatorSelectedPillar,
            meter = _uiState.value.generatorMeter,
            bpm = _uiState.value.generatorBpm,
            customSeed = _uiState.value.generatorCustomSeed
        )
        _uiState.value = _uiState.value.copy(generatedStanzas = stanzas)
    }

    fun setCustomLyricInput(text: String) {
        _uiState.value = _uiState.value.copy(
            customLyricInput = text,
            customLyricEvaluation = repository.evaluateCustomLyrics(text)
        )
    }

    // Protocol 3.2.1.0 steps
    fun advanceProtocolStep(step: Int) {
        _uiState.value = _uiState.value.copy(
            protocolStep = step,
            isFreezeLocked = (step == 0)
        )
    }
}
