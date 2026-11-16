# ELYZARETH OS — G1–G5 CONSTITUTIONAL GOVERNANCE SPECIFICATION 1.0

**Assignment ID:** `EOS-ARCH-RECON-001` (Section 4.C & Deliverable #4)  
**Architectural Authority:** ELYZARETH OS — Final Master Blueprint (Section 4 & Section 15)  
**Decision Authority:** Student-Director / Human Creator  
**Status:** `SPECIFICATION COMPLETE — IMPLEMENTATION FROZEN PENDING APPROVAL`

---

## 1. CONSTITUTIONAL ROLE OF THE FIVE GATES

Gates **G1 through G5** are the primary constitutional control mechanism of Elyzareth OS. They are **not** decorative UI badges or simple string fields (`DEFECT P0-05`). Each gate is a deterministic state machine backed by:
1. A mandatory **Evidence Package** (`SYS-CON-04`),
2. An independent **Analytical or Provenance Verification Pass**, and
3. An authenticated **Human Authority Digital Signature** (`SYS-CON-05`) recorded in the append-only `evidence_ledger` (`SYS-CON-06`).

---

## 2. CANONICAL GATE LIFECYCLE STATE MACHINE

Every gate (`G1`, `G2`, `G3`, `G4`, `G5`) for a given project and artifact revision transitions strictly through the following 8-state lifecycle:

```text
                       +-----------------------+
                       |     NOT_EVALUATED     | (Initial default state for all gates)
                       +-----------------------+
                                   |
                                   | [Kernel initiates prerequisite & evidence check]
                                   v
                       +-----------------------+
                       | EVALUATION_IN_PROGRESS|
                       +-----------------------+
                          /                 \
  [Missing prerequisites /                   \ [All required evidence & reports
   failed automated check]                    \  assembled and hash-verified]
                        v                      v
        +-----------------------+     +-----------------------+
        |   REVISION_REQUIRED   |     | HUMAN_REVIEW_REQUIRED |
        +-----------------------+     +-----------------------+
                    ^                   /     |       \       \
                    |                  /      |        \       \
     [REQUEST_REVISION]               /   [DEFER]   [REJECT]   [VETO]
                    |                /        |          \         \
                    +---------------+         v           v         v
                                    |  +----------+ +----------+ +----------+
                                    |  | DEFERRED | | REJECTED | |  VETOED  |
                                    |  +----------+ +----------+ +----------+
                                    |
                            [APPROVE + Valid
                             Keystore Signature]
                                    |
                                    v
                       +-----------------------+
                       |        APPROVED       | (Unlocks authorized progression)
                       +-----------------------+
```

### 2.1 Non-Negotiable State Transition Rules
1. **Default Initialization:** Every gate starts at `NOT_EVALUATED` (`ElyzarethViewModel.kt:76–79` currently defaults G1–G4 to `"VERIFIED"`, which is tracked as `DEFECT P0-05` and must be corrected to `NOT_EVALUATED`).
2. **Zero Autonomous Approval:** No automated scanner, AI model, or repository method can transition a gate from `EVALUATION_IN_PROGRESS` directly to `APPROVED`. The maximum state an automated check can reach is `HUMAN_REVIEW_REQUIRED`.
3. **Veto & Rejection Supremacy (Constitutional Law #7):** Once a gate enters `REJECTED` or `VETOED`, progression is blocked. No automated process or background worker can convert `REJECTED` or `VETOED` into `APPROVED`. Re-evaluation requires a new artifact revision (`version + 1`) and an explicit human `REOPEN_FOR_REVIEW` decision.
4. **Upstream Invalidation Rule:** If an artifact revision is modified after `G1` or `G2` was `APPROVED`, all downstream gates for that song automatically transition to `HUMAN_REVIEW_REQUIRED` for the new revision while preserving the historical approval record of the prior revision in `evidence_ledger`.

---

## 3. DETAILED GATE EVALUATION CONTRACTS (`G1` – `G5`)

### 3.1 Gate `G1` — Concept and Curated Lyrics
- **Constitutional Responsibility:** Certify that the song's original creative intent, structural blueprint, and human-curated lyrics are complete, coherent, and editorially endorsed.
- **Required Authority:** `HUMAN_CREATOR` (Student-Director) or authorized `HUMAN_CURATOR` with Creator final sign-off.
- **Mandatory Evidence Matrix (`G1` Prerequisites):**
  1. `IdeaArtifact` (Stage `01`) registered in Room with non-blank concept intent, language, and creator ID.
  2. `CreativeBlueprint` (Stage `02`) locked in Room with defined structure, BPM, meter, and physical witness anchor.
  3. Complete lyric revision chain from `LyricDraft` (Stage `03` or SAF ingest) to `CuratedLyricArtifact` (Stage `05`).
  4. Zero unresolved `DEFECTIVE` / `FAILED` parse or UTF-8 BOM errors on the target lyric artifact.
  5. Explicit Curator editorial endorsement note and originality/emotional coherence review recorded.
- **Rejection / Revision Behaviour:**
  - `REQUEST_REVISION` returns the workflow to Stage `05` (`Lyric Curator`), preserving the rejected draft as historical evidence and creating a new editable revision (`v+1`).
  - `VETO` locks the base title as `REJECTED & ARCHIVED`.

---

### 3.2 Gate `G2` — Evidence and Provenance
- **Constitutional Responsibility:** Verify that the complete chain of custody—from initial prompt or SAF source file through every editorial revision—is intact, tamper-evident, and free of fabricated metadata.
- **Required Authority:** `HUMAN_AUDITOR` or `HUMAN_CREATOR` verification.
- **Mandatory Evidence Matrix (`G2` Prerequisites):**
  1. Original source capture record (`ORIGINAL_EVIDENCE`): either the SAF source file path, byte size, and stream SHA-256 hash (`ArtifactEntity.fileHash`), or the initial generator seed/prompt record.
  2. Generation & tool metadata where applicable (`modelIdentity`, `anchorUsed`, `pillar`, `timestampIso`).
  3. Unbroken parent-child hash chain (`parentProvenanceHash -> sha256Hash`) across all revisions of the artifact in `evidence_ledger`.
  4. 100% SHA-256 byte parity verified between the stored artifact content/file and its recorded `sha256Hash`.
- **Strict Anti-Fabrication Rule:** Missing provenance fields (e.g., an imported external file with no prior prompt history) must be recorded honestly as `EXTERNAL_SAF_SOURCE_INGEST` with the exact ingestion timestamp and SHA-256 hash—never backfilled with synthetic generation history.
- **Rejection / Revision Behaviour:**
  - Any hash mismatch or broken parent link forces `G2` to `FAILED` / `REVISION_REQUIRED` and blocks `G3..G5`.

---

### 3.3 Gate `G3` — Creative DNA Conformance
- **Constitutional Responsibility:** Verify that the curated lyrics and musical/acoustic direction conform to the active, versioned **Artist Identity & Creative DNA Profile** (`CreativeDnaProfile`).
- **Required Authority:** `HUMAN_CREATOR` approval.
- **Architectural Separation of Universal Law vs. Artist DNA (`DEFECT P1-08` Resolution):**
  - **Universal Constitutional Rule (Level 1):** Every project must be evaluated against an active, versioned `CreativeDnaProfile` (`policyVersion`), and any deviation requires either revision or an explicit, documented Human Creator exception.
  - **Configured Canonical Elyzareth DNA Profile (`DNA-ELYZARETH-WITNESS-v1.0`):** Preserves 100% of the established Elyzareth lyric and acoustic governance rules without hardcoding them as universal OS laws for all possible profiles:
    - *Physical Witness Anchors:* Concrete tactile objects (Blue Thread / नीला धागा, Dry Salt / सूखा नमक, Uncarved Key, Cold Porcelain, Weathered Oak, Monsoon Silt, Paper Boat / कागज़ की नाव, Iron Latch, Stone Threshold).
    - *5 Witness Pillars:* Domestic, Environmental, Recovery, Cultural, Transcendental.
    - *Prohibited Imagery & Tropes:* Blocks synthetic pop melodrama and metaphor soup (`"wings of time"`, `"starlight in my soul"`, `"neon rain"`, `"tapestry of fate"`, `"symphony of pain"`, `"shattered glass of my heart"`, `"stadium swell"`, `"cosmic ocean"`).
    - *Language Policies:* Unadorned Devanagari (Hindi), English, and Bilingual phonetic clarity (UTF-8 No-BOM).
    - *Sonic & Structural Identity:* Envelope Class A, dry wooden room `T60 = 0.38s–0.42s`, mandatory `1.8s–2.0s` P2 inter-stanza silence gap, no commercial orchestral/synth swells.
- **Mandatory Evidence Matrix (`G3` Prerequisites):**
  1. Active `CreativeDnaProfile` (`id` + `policyVersion`) bound to the project.
  2. Executed `DnaConformanceReport` listing: (a) physical anchor presence per stanza, (b) prohibited trope scan results, (c) structural/P2 gap compliance, and (d) acoustic profile alignment.
  3. If the automated DNA check flags a warning, the system **never silently rewrites the lyric**; it presents the reasoned `DnaConformanceReport` to the Human Creator, who may either `REQUEST_REVISION` or grant an explicit `APPROVED_WITH_CREATOR_EXCEPTION(reason)` signed decision.

---

### 3.4 Gate `G4` — Forensic Clearance
- **Constitutional Responsibility:** Certify that all required technical, similarity, integrity, and catalog collision examinations have been reviewed and cleared by human authority.
- **Required Authority:** Independent analysis by `ForensicScannerEngine` (`Layer 4`) followed by mandatory verification and certification by `HUMAN_AUDITOR` / `HUMAN_CREATOR` (`Layer 5`). **The Forensic Engine can never approve G4 itself.**
- **Mandatory Evidence Matrix (`G4` Prerequisites):**
  1. Completed `ForensicScanReport` linked to the current `AssetManifest` hashes.
  2. Explicit classification (`PASS`, `WARNING`, `FAIL`, `INCONCLUSIVE`, `NOT_TESTED`) for each of the 8 forensic scanners:
     - `SCAN-01` Lyric Similarity
     - `SCAN-02` Audio Similarity
     - `SCAN-03` Watermark Detection
     - `SCAN-04` Audio Integrity
     - `SCAN-05` Vocal Analysis
     - `SCAN-06` Production Integrity
     - `SCAN-07` Provenance Verification
     - `SCAN-08` Catalog Collision Detection
- **Strict Interpretation Rules:**
  - A missing or unexecuted scanner **must** be reported as `NOT_TESTED`, never `PASS`.
  - Any `FAIL` on **Audio Integrity (`SCAN-04`)** or **Provenance Verification (`SCAN-07`)** blocks `G4` approval until remediated.
  - A similarity percentage (`SCAN-01` / `SCAN-02`) is an algorithmic estimate, not legal proof of plagiarism; synthetic vocal indicators (`SCAN-05`) are signal estimates, not proof of AI authorship. Both require explicit human interpretation and sign-off.

---

### 3.5 Gate `G5` — Final Release Authorization & Atomic Sealing
- **Constitutional Responsibility:** Execute the sovereign, irreversible sealing of an approved master package into the Release Vault (`Stage 13 -> Stage 14`).
- **Required Authority:** **Exclusive `HUMAN_CREATOR` (Student-Director) cryptographic authorization.** Cannot be delegated to `HUMAN_CURATOR`, `HUMAN_AUDITOR`, or any automated service.
- **Eleven Mandatory G5 Sealing Prerequisites (`SYS-CON-15`):**
  1. **G1 Approved:** Valid `GateVerification(G1, APPROVED)` with verified human signature for the exact `CuratedLyricArtifact` version.
  2. **G2 Verified:** Valid `GateVerification(G2, APPROVED)` with verified unbroken `evidence_ledger` hash chain.
  3. **G3 Approved:** Valid `GateVerification(G3, APPROVED)` against active `CreativeDnaProfile`.
  4. **G4 Certified:** Valid `GateVerification(G4, APPROVED)` referencing a current `ForensicScanReport` matching the release assets.
  5. **Complete Release Manifest:** Non-empty `ReleaseManifest` containing project metadata, song title, ISRC/catalog code, language, duration, and all constituent asset IDs (Master Audio, Curated Lyrics, Cover Artwork, Forensic Report, Evidence Chain Root).
  6. **Verified Source & Derived Asset Hashes:** Live byte-stream SHA-256 verification confirming every file in the manifest exists on disk and matches its registered `checksumSha256`.
  7. **Authenticated Human Release Authority:** Active `HUMAN_CREATOR` session.
  8. **Valid Human Release Signature:** Hardware-backed Android Keystore `SHA256withECDSA` signature over the canonical `ReleaseManifest` JSON and Merkle root.
  9. **Immutable Release Identity:** Deterministic `releaseId` and `releaseVersion` (`e.g., ELYZ-MASTER-001-v1.0`).
  10. **Atomic Sealing Operation:** Single Room `@Transaction` that writes the sealed `ReleasePackage`, locks all constituent artifacts (`status = "RELEASED & FROZEN"`), and appends the `G5_RELEASE_SEALED` authority event to `evidence_ledger`.
  11. **Post-Seal Integrity Verification:** Immediate read-back and signature/hash verification of the committed `ReleasePackage` row before returning success.
- **Post-Seal Immutability Rule:** Once sealed, a `ReleasePackage` is permanently read-only. Any subsequent correction requires creating a new release version (`v2.0`) with `parentReleaseId = "ELYZ-MASTER-001-v1.0"` and re-executing the modified gates.

---

## 4. G1–G5 GOVERNANCE ACCEPTANCE TEST SPECIFICATIONS

| Test ID | Target Gate / Rule | Test Scenario | Expected Constitutional Result |
| :--- | :--- | :--- | :--- |
| **`GOV-T01`** | **Gate Default State** | Inspect initial `GateVerification` state for any newly created or seeded song/project. | All 5 gates (`G1..G5`) initialize to `NOT_EVALUATED` (never pre-verified). |
| **`GOV-T02`** | **Out-of-Sequence Gate Evaluation** | Attempt to approve `G2` or `G5` when `G1` is `NOT_EVALUATED`. | Rejected by `SovereignKernel` with `GATE_SEQUENCE_VIOLATION`. |
| **`GOV-T03`** | **Unauthenticated / Unsigned Gate Approval** | Call `submitGateDecision(G1, APPROVE)` with an empty, forged, or hash-only signature string. | Rejected by `DecisionAuthorityService`; gate remains in `HUMAN_REVIEW_REQUIRED`. |
| **`GOV-T04`** | **AI-Generated Approval Rejection** | Submit `GateDecision` with `actorType = AI` (`AI_GENERATOR` or `AI_ANALYZER`). | Rejected immediately with `CONSTITUTIONAL_VIOLATION_LAW_08_NO_SYNTHETIC_AUTHORITY`. |
| **`GOV-T05`** | **Human Veto Supremacy** | Human Creator issues `VETO` on `G3`; automated service or secondary call attempts to set `G3` to `APPROVED` on the same revision. | Blocked by `SovereignKernel`; transition from `VETOED` requires a new artifact revision and explicit human `REOPEN_FOR_REVIEW`. |
| **`GOV-T06`** | **Missing Forensic Report Blocks G4** | Attempt to approve `G4` when no `ForensicScanReport` exists for the target song's current asset hashes. | Blocked with `G4_MISSING_FORENSIC_REPORT`. |
| **`GOV-T07`** | **Incomplete Manifest or Failed G1–G4 Blocks G5** | Attempt `sealMasterRelease()` when any of `G1..G4 != APPROVED` or when the audio master asset file is missing on disk. | Blocked atomically; `atomicSealStatus` remains `PENDING_G5_LOCK`. |
| **`GOV-T08`** | **Post-Approval Revision Invalidation** | Approve `G1` on `CuratedLyricArtifact` `v1`, then commit a modified `CuratedLyricArtifact` `v2`. | `v1` approval remains preserved in `evidence_ledger`, while active project `G1` state resets to `HUMAN_REVIEW_REQUIRED` for `v2`. |
