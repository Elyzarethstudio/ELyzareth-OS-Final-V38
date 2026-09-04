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
import com.example.data.model.DEFAULT_PHYSICAL_ANCHORS
import com.example.data.model.GeneratedLyricStanza
import com.example.data.model.PhysicalAnchor
import com.example.data.model.WitnessPillar
import com.example.data.repository.CorpusRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ElyzarethUiState(
    val activeTab: Int = 0, // 0 = App 02 Curator, 1 = App 01 Generator, 2 = 3.2.1.0 Protocol
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

    // 3.2.1.0 Protocol State
    val protocolStep: Int = 3, // 3: Listen, 2: Evaluate, 1: Decide, 0: Freeze
    val isFreezeLocked: Boolean = false,
    val reviewNotesInput: String = ""
)

class ElyzarethViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CorpusRepository

    private val _uiState = MutableStateFlow(ElyzarethUiState())

    init {
        val db = ElyzarethDatabase.getDatabase(application)
        repository = CorpusRepository(db.elyzarethDao())

        viewModelScope.launch {
            repository.ensureCorpusInitialized()
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

    val uiState: StateFlow<ElyzarethUiState> = _uiState

    fun setTab(index: Int) {
        _uiState.value = _uiState.value.copy(activeTab = index)
    }

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
            // Update in-memory selected artifact if it's open
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
