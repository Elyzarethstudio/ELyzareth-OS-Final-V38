package com.example.foundation

/**
 * ELYZARETH OS — FOUNDATION LAYER (Authority & Rules)
 * Reference: ELYZARETH_OS_STRUCTURE_1.0
 *
 * Defines:
 * 1. Human Authority (Student-Director / Human Creator)
 * 2. Constitutional Laws
 * 3. System Identity
 * 4. Governance Rules (G1-G5)
 * 5. Evidence Rules (4-State Verification Model)
 * 6. Release Rules (Atomic Sealing & Zero-Bypass Lock)
 * 7. Canonical 14-Stage Production Pipeline
 */

object SystemIdentity {
    const val OS_NAME = "ELYZARETH OS"
    const val STRUCTURE_VERSION = "1.0"
    const val LEGACY_BASELINE_VERSION = "38.1"
    const val SOVEREIGN_ROLE = "Student-Director / Human Creator"
    const val AUTHORITY_MODE = "Sovereign Human Authority (100% Veto & Sign-Off)"
    const val ARCHITECTURE_DOCTRINE = "Offline-First • Zero-Mutation Ingestion • G1-G5 Gate Lock"
}

enum class VerificationState(val code: String, val label: String, val description: String) {
    SPEC("SPEC", "Specification Defined", "Functional contract and witness boundaries established."),
    IMPLEMENTED_UNVERIFIED("UNVERIFIED", "Implemented / Unverified", "Artifact parsed & hashed; awaiting forensic human audit."),
    VERIFIED("VERIFIED", "Verified", "Confirmed physical witness anchor and 100% SHA-256 parity."),
    FAILED("FAILED", "Failed / Defective", "Encoding mismatch, BOM offset error, or prohibited trope violation."),
    NOT_MEASURED("NOT MEASURED", "Not Measured (Locked)", "Gate locked pending explicit Human Governor measurement.")
}

enum class GovernanceGateId(
    val gateCode: String,
    val title: String,
    val domain: String,
    val mandatoryRule: String
) {
    G1("G1", "G1 — Concept & Lyrics", "Witness Transformation Contract", "Every stanza must anchor in concrete physical objects; zero metaphor soup or pop melodrama."),
    G2("G2", "G2 — Evidence", "Read-Only Evaluator & SHA-256 Capture", "Non-destructive ingestion; immutable SHA-256 file hash and provenance ledger entry."),
    G3("G3", "G3 — Creative DNA", "Style, Voice & Acoustic Calibration", "T60 decay 0.42s dry room morphology; conformant imagery and vocal identity."),
    G4("G4", "G4 — Forensic", "Similarity, Collision & Watermark Audit", "Zero catalog collision, verified audio integrity, and clean forensic similarity scan."),
    G5("G5", "G5 — Release Approval", "Human Governor Master Sign-Off", "Requires G1–G4 VERIFIED plus explicit Student-Director cryptographic sign-off.")
}

enum class PipelineStage(
    val stageNumber: Int,
    val code: String,
    val title: String,
    val responsibleBranch: String
) {
    STAGE_01_IDEA_INCEPTION(1, "01", "Idea Inception", "Idea & Blueprint Studio"),
    STAGE_02_CREATIVE_BRIEF(2, "02", "Creative Brief & Blueprint", "Idea & Blueprint Studio"),
    STAGE_03_LYRIC_GENERATOR(3, "03", "Lyric Generator", "Lyric Generator"),
    STAGE_04_EVIDENCE_CAPTURE(4, "04", "Evidence Capture", "Evidence Vault"),
    STAGE_05_LYRIC_CURATOR(5, "05", "Lyric Curator", "Lyric Curator"),
    STAGE_06_CREATIVE_DNA(6, "06", "Creative DNA Registry", "Creative DNA Engine"),
    STAGE_07_MUSIC_STYLE(7, "07", "Music Style Engineering", "Music Production Console"),
    STAGE_08_VISUAL_DIRECTION(8, "08", "Visual Direction", "Visual Direction Studio"),
    STAGE_09_PRODUCTION_INGEST(9, "09", "Production Ingest", "Music Production Console"),
    STAGE_10_ASSET_REGISTRATION(10, "10", "Asset Registration", "Asset Registry"),
    STAGE_11_FORENSIC_SCANNER(11, "11", "Forensic Scanner", "Forensic Lab"),
    STAGE_12_G1_G2_GOVERNANCE(12, "12", "G1–G2 Governance", "Governance Console"),
    STAGE_13_G3_G5_GOVERNANCE(13, "13", "G3–G5 Governance", "Governance Console"),
    STAGE_14_MASTER_RELEASE(14, "14", "Master Player & Distribution", "Master Vault & Player")
}

data class ConstitutionalLaw(
    val lawId: String,
    val title: String,
    val mandate: String,
    val isImmutable: Boolean = true
)

object ConstitutionalLaws {
    val ALL_LAWS: List<ConstitutionalLaw> = listOf(
        ConstitutionalLaw(
            lawId = "LAW-01",
            title = "Sovereign Human Authority",
            mandate = "The Student-Director / Human Creator retains sole final authority over all curation, governance gate sign-offs (G1–G5), and Master Release sealing."
        ),
        ConstitutionalLaw(
            lawId = "LAW-02",
            title = "Zero-Mutation First-Pass Ingestion",
            mandate = "Discovery and evidence capture passes are strictly read-only. Source files are never mutated or auto-cured during ingestion."
        ),
        ConstitutionalLaw(
            lawId = "LAW-03",
            title = "Physical Witness Anchor Mandate",
            mandate = "Creative output must anchor in concrete physical reality (thread, salt, porcelain, iron key, dry wood) and reject synthetic melodrama."
        ),
        ConstitutionalLaw(
            lawId = "LAW-04",
            title = "Single Canonical Sovereign Kernel",
            mandate = "All workflow state transitions and gate unlocks route exclusively through the Sovereign Kernel. Competing state authorities are prohibited."
        ),
        ConstitutionalLaw(
            lawId = "LAW-05",
            title = "Zero-Bypass G1–G5 Release Lock",
            mandate = "No project or song may enter the sealed Master Release state unless G1, G2, G3, G4, and G5 are explicitly verified and signed off."
        ),
        ConstitutionalLaw(
            lawId = "LAW-06",
            title = "Offline-First Core Autonomy",
            mandate = "Core persistence, forensic hashing, curation, and governance operate 100% offline. Cloud and AI adapters remain strictly optional."
        )
    )
}

object ReleaseRules {
    fun canUnlockMasterRelease(
        g1Status: String,
        g2Status: String,
        g3Status: String,
        g4Status: String,
        g5Status: String,
        humanSignOffConfirmed: Boolean
    ): Boolean {
        val allVerified = listOf(g1Status, g2Status, g3Status, g4Status, g5Status).all {
            it.equals("VERIFIED", ignoreCase = true) || it.equals("APPROVED", ignoreCase = true)
        }
        return allVerified && humanSignOffConfirmed
    }
}
