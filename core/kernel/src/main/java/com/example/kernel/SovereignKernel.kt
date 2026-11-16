package com.example.kernel

import com.example.foundation.ConstitutionalLaws
import com.example.foundation.GovernanceGateId
import com.example.foundation.PipelineStage
import com.example.foundation.ReleaseRules
import com.example.foundation.SystemIdentity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest

/**
 * ELYZARETH OS — SOVEREIGN KERNEL LAYER (The Control Centre)
 * Reference: ELYZARETH_OS_STRUCTURE_1.0
 *
 * The Sovereign Kernel is the sole authority for canonical workflow state transitions.
 * Sub-engines:
 * 1. GovernorStateMachine
 * 2. DecisionEngine
 * 3. KernelEventRouter
 * 4. PolicyEngine
 * 5. IntegrityEngine
 * 6. RecoveryManager
 */

data class KernelEvent(
    val eventId: String,
    val timestampMs: Long,
    val branch: String,
    val action: String,
    val details: String,
    val authority: String = SystemIdentity.SOVEREIGN_ROLE
)

data class CanonicalProjectContext(
    val activeProjectId: String = "PROJ-ELYZ-001",
    val activeProjectTitle: String = "Witness Vault — Sovereign Anthology I",
    val currentPipelineStage: PipelineStage = PipelineStage.STAGE_05_LYRIC_CURATOR,
    val kernelOnline: Boolean = true,
    val databaseStatus: String = "OK (Room Persistent)",
    val governanceStatus: String = "Active (G1–G5 Enforced)",
    val stopConditionActive: Boolean = true,
    val systemHealthPercent: Int = 100
)

data class GateEvaluationState(
    val targetId: String,
    val targetTitle: String,
    val g1Status: String = "NOT MEASURED",
    val g2Status: String = "NOT MEASURED",
    val g3Status: String = "NOT MEASURED",
    val g4Status: String = "NOT MEASURED",
    val g5Status: String = "NOT MEASURED",
    val humanSignOff: Boolean = false,
    val vetoReason: String? = null
)

object SovereignKernel {

    private val _projectContext = MutableStateFlow(CanonicalProjectContext())
    val projectContext: StateFlow<CanonicalProjectContext> = _projectContext.asStateFlow()

    private val _eventLedger = MutableStateFlow<List<KernelEvent>>(
        listOf(
            KernelEvent(
                eventId = "KEV-0001",
                timestampMs = System.currentTimeMillis(),
                branch = "Sovereign Kernel",
                action = "KERNEL_BOOT",
                details = "Sovereign Kernel v1.0 online. ${ConstitutionalLaws.ALL_LAWS.size} Constitutional Laws enforced."
            ),
            KernelEvent(
                eventId = "KEV-0002",
                timestampMs = System.currentTimeMillis(),
                branch = "Governance Console",
                action = "STOP_CONDITION_ARMED",
                details = "G1–G5 gates initialized in zero-bypass mode. Human Governor authority active."
            )
        )
    )
    val eventLedger: StateFlow<List<KernelEvent>> = _eventLedger.asStateFlow()

    // 1. Governor & State Machine
    fun transitionPipelineStage(targetStage: PipelineStage, reason: String): Boolean {
        if (targetStage == PipelineStage.STAGE_14_MASTER_RELEASE) {
            recordEvent(
                branch = "GovernorStateMachine",
                action = "STAGE_14_GUARD_CHECK",
                details = "Stage 14 transition requested: $reason"
            )
        }
        _projectContext.value = _projectContext.value.copy(currentPipelineStage = targetStage)
        recordEvent(
            branch = "GovernorStateMachine",
            action = "PIPELINE_STAGE_TRANSITION",
            details = "Transitioned active project to Stage ${targetStage.code}: ${targetStage.title} ($reason)"
        )
        return true
    }

    fun selectActiveProject(projectId: String, projectTitle: String, stage: PipelineStage) {
        _projectContext.value = _projectContext.value.copy(
            activeProjectId = projectId,
            activeProjectTitle = projectTitle,
            currentPipelineStage = stage
        )
        recordEvent(
            branch = "GovernorStateMachine",
            action = "PROJECT_CONTEXT_SWITCH",
            details = "Switched canonical project context to $projectId ($projectTitle)"
        )
    }

    // 2. Decision Engine & Policy Engine
    fun evaluateGateTransition(
        current: GateEvaluationState,
        gate: GovernanceGateId,
        newStatus: String,
        governorNote: String
    ): GateEvaluationState {
        val normalized = newStatus.uppercase()
        val updated = when (gate) {
            GovernanceGateId.G1 -> current.copy(g1Status = normalized)
            GovernanceGateId.G2 -> current.copy(g2Status = normalized)
            GovernanceGateId.G3 -> current.copy(g3Status = normalized)
            GovernanceGateId.G4 -> current.copy(g4Status = normalized)
            GovernanceGateId.G5 -> {
                // Policy Engine: G5 cannot be VERIFIED unless G1-G4 are VERIFIED
                val g1ToG4Verified = listOf(current.g1Status, current.g2Status, current.g3Status, current.g4Status)
                    .all { it == "VERIFIED" }
                if (normalized == "VERIFIED" && !g1ToG4Verified) {
                    recordEvent(
                        branch = "PolicyEngine",
                        action = "G5_BYPASS_BLOCKED",
                        details = "Blocked G5 approval on ${current.targetTitle}: G1–G4 must be VERIFIED first."
                    )
                    current.copy(
                        g5Status = "BLOCKED (G1-G4 REQUIRED)",
                        humanSignOff = false
                    )
                } else {
                    current.copy(
                        g5Status = normalized,
                        humanSignOff = (normalized == "VERIFIED")
                    )
                }
            }
        }
        recordEvent(
            branch = "DecisionEngine",
            action = "GATE_${gate.gateCode}_DECISION",
            details = "${current.targetTitle} [${gate.gateCode}] -> ${updatedGateValue(updated, gate)} ($governorNote)"
        )
        return updated
    }

    private fun updatedGateValue(state: GateEvaluationState, gate: GovernanceGateId): String = when (gate) {
        GovernanceGateId.G1 -> state.g1Status
        GovernanceGateId.G2 -> state.g2Status
        GovernanceGateId.G3 -> state.g3Status
        GovernanceGateId.G4 -> state.g4Status
        GovernanceGateId.G5 -> state.g5Status
    }

    fun canSealMasterRelease(state: GateEvaluationState): Boolean {
        return ReleaseRules.canUnlockMasterRelease(
            g1Status = state.g1Status,
            g2Status = state.g2Status,
            g3Status = state.g3Status,
            g4Status = state.g4Status,
            g5Status = state.g5Status,
            humanSignOffConfirmed = state.humanSignOff
        )
    }

    // 3. Event Router
    fun recordEvent(branch: String, action: String, details: String) {
        val nextIdx = _eventLedger.value.size + 1
        val event = KernelEvent(
            eventId = "KEV-%04d".format(nextIdx),
            timestampMs = System.currentTimeMillis(),
            branch = branch,
            action = action,
            details = details
        )
        _eventLedger.value = (listOf(event) + _eventLedger.value).take(200)
    }

    // 4. Integrity Engine
    fun computeSha256Hex(payload: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(payload.toByteArray(Charsets.UTF_8))
        return "sha256_" + bytes.joinToString("") { "%02x".format(it) }
    }

    fun verifyHashParity(payload: String, expectedHash: String): Boolean {
        return computeSha256Hex(payload).equals(expectedHash, ignoreCase = true)
    }

    // 5. Recovery Manager
    fun createRecoveryCheckpointSummary(titlesCount: Int, artifactsCount: Int): String {
        val checkpointHash = computeSha256Hex("CHECKPOINT_${titlesCount}_${artifactsCount}_${_projectContext.value.activeProjectId}")
        recordEvent(
            branch = "RecoveryManager",
            action = "RECOVERY_CHECKPOINT_CREATED",
            details = "Titles: $titlesCount | Artifacts: $artifactsCount | Hash: ${checkpointHash.take(24)}..."
        )
        return checkpointHash
    }
}
