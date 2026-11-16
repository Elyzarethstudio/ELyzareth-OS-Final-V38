# ELYZARETH OS — COMPLETE TARGET ARCHITECTURE 1.0

**Assignment ID:** `EOS-ARCH-RECON-001` (Section 4.A, 4.B, 4.M & Deliverable #2)  
**Architectural Authority:** ELYZARETH OS — Final Master Blueprint  
**Decision Authority:** Student-Director / Human Creator  
**Status:** `TARGET SPECIFICATION COMPLETE — IMPLEMENTATION FROZEN PENDING APPROVAL`

---

## 1. ARCHITECTURAL CONSTITUTION & SIX-LAYER HIERARCHY

Elyzareth OS is governed by a strict **Six-Layer Sovereign Architecture** anchored on the **Human Creator Foundation**. Higher layers may inspect and analyze lower-layer evidence, but **may never rewrite, delete, or bypass lower-layer historical evidence or kernel state** (*Layer Monotonicity — Constitutional Law #4*).

```text
+===================================================================================+
|                     LAYER 6 — RELEASE & DISTRIBUTION                              |
|        Master Player • Release Vault • Atomic Sealing • Distribution Gate         |
+===================================================================================+
                                         |
                                         | (Read-Only Package Inspection;
                                         |  Requires G5 Cryptographic Seal)
                                         v
+===================================================================================+
|                   LAYER 5 — GOVERNANCE & VERIFICATION                             |
|     G1–G5 Gate Console • 3.2.1.0 Protocol • Human Sign-Off & Veto Workstation     |
+===================================================================================+
                                         |
                                         | (Submits Signed Human Decisions;
                                         |  Evaluates Analytical & Creative Evidence)
                                         v
+===================================================================================+
|                     LAYER 4 — FORENSIC INTELLIGENCE                               |
|   Lyric Similarity • Audio Integrity • Collision • Watermark • Provenance Audit   |
+===================================================================================+
                                         |
                                         | (Read-Only Analysis of Registered Assets
                                         |  & Corpus; Produces ForensicScanReport)
                                         v
+===================================================================================+
|                      LAYER 3 — CREATIVE PRODUCTION                                |
|  Idea & Blueprint • Lyric Generator • Lyric Curator (App 02) • DNA • Music/Visual |
+===================================================================================+
                                         |
                                         | (Submits Artifact Proposals & Revisions;
                                         |  Zero Direct Governor State Mutation)
                                         v
+===================================================================================+
|                     LAYER 2 — EVIDENCE & PROVENANCE                               |
|   Append-Only Evidence Ledger • SHA-256 Hash Chain • Asset Registry • Revisions   |
+===================================================================================+
                                         |
                                         | (Transactional Persistence & Integrity
                                         |  Verification Controlled by Kernel)
                                         v
+===================================================================================+
|                       LAYER 1 — SOVEREIGN KERNEL                                  |
|  Identity & Authority • Governor State Machine • Decision Engine • Policy Engine  |
|  Event Router • Integrity Engine • Recovery Manager • Capability Registry         |
+===================================================================================+
                                         ^
                                         | (Sole Constitutional Mandate, Veto,
                                         |  Keystore Signature & Release Authority)
+===================================================================================+
|                        FOUNDATION — HUMAN CREATOR                                 |
|    Student-Director / Human Creator • 14 Constitutional Laws • 14-Stage Contract  |
+===================================================================================+
```

---

## 2. LAYER-BY-LAYER OWNERSHIP, INTERFACES & BOUNDARIES (SECTION 4.A)

### 2.1 Foundation — Human Creator & Constitutional Invariants
- **Responsibilities:** Define the 14 permanent Constitutional Laws, the 8 Actor Roles, the 14-stage canonical pipeline contracts, the G1–G5 gate definitions, and the canonical decision verbs.
- **Owning Gradle Module:** `:core:foundation`
- **Source Package:** `com.example.foundation`
- **Domain Interfaces:** `ConstitutionalLaw`, `ConstitutionalLaws` (14 laws), `ActorRole`, `ActorPrincipal`, `PipelineStage` (`01`..`14`), `GovernanceGateId` (`G1`..`G5`), `GateLifecycleState`, `DecisionVerb`, `ForensicClassification`.
- **Database Ownership:** None (pure immutable domain contracts; zero Room or Android UI dependencies).
- **Permitted Operations:** Pure deterministic rule definitions and invariant predicates.
- **Prohibited Operations:** Network I/O, database mutation, UI state holding, or artist-specific hardcoded creative preferences (`DEFECT P1-08` correction).
- **Current Implementation Status:** `PARTIALLY IMPLEMENTED` (`ConstitutionalFoundation.kt` has 6 laws and conflates Creative DNA rules).

### 2.2 Layer 1 — Sovereign Kernel
- **Responsibilities:** Authenticate actor sessions, enforce module capabilities, validate and execute all canonical project/pipeline/gate state transitions, route consequential events into the persistent evidence ledger, verify hash chains and signatures, and recover interrupted transactions.
- **Owning Gradle Module:** `:core:kernel`
- **Source Package:** `com.example.kernel`
- **Domain Interfaces:**
  - `SovereignKernelContract` (Root orchestrator)
  - `IdentityAndAuthorityService` (Section B1)
  - `GovernorStateMachine` (Section B2 & B3)
  - `DecisionAuthorityService` (Section B4)
  - `CapabilityRegistry` (Section B5)
  - `KernelEventRouter` & `RecoveryManager` (Section B6)
  - `IntegrityVerificationEngine` (SHA-256 & Keystore signature verification)
- **Persistence Port (Dependency Inversion):** `:core:kernel` defines the `KernelPersistencePort` interface (`recordEventAtomically`, `saveProjectAggregate`, `saveGateVerification`, `loadLastCheckpoint`), which is implemented by the Room persistence layer so `:core:kernel` remains free of direct Android UI dependencies while guaranteeing durable ACID state transitions.
- **Permitted Operations:** Validating transition prerequisites, committing atomic state + evidence transactions via `KernelPersistencePort`, rejecting unauthorized or out-of-order requests.
- **Prohibited Operations:** Autonomous approval of gates without a verified human signature; overwriting historical evidence; external network calls.
- **Current Implementation Status:** `DEFECTIVE` (`DEFECT P0-03`, `DEFECT P0-04`, `DEFECT P0-05`).

### 2.3 Layer 2 — Evidence & Provenance
- **Responsibilities:** Maintain the append-only hash-chained `evidence_ledger`, manage the `asset_registry` and file byte checksum verification, preserve the historical corpus (`base_titles`, `artifacts`), and enforce immutable revision chains.
- **Owning Gradle Module:** `:app` (target `:core:persistence` or `:app` data layer behind domain repository interfaces).
- **Source Packages:** `com.example.data.local`, `com.example.data.model`, `com.example.data.repository`
- **Repositories & DAOs:** `EvidenceRepository`, `AssetRegistryRepository`, `CorpusRepository`, `ElyzarethDao`, `OsBranchDao`.
- **Database Tables Owned:** `evidence_ledger`, `asset_registry`, `base_titles`, `artifacts`.
- **Permitted Operations:** Append-only insertion of `EvidenceLedgerEntity`; registration of source/derived assets with real file stream SHA-256 hashes; non-destructive SAF corpus reconciliation; creation of new artifact revisions.
- **Prohibited Operations:** `UPDATE` or `DELETE` on `evidence_ledger`; overwriting or unsealing `RELEASED & FROZEN` rows in `artifacts`; fabricating hashes from filenames instead of file bytes.
- **Current Implementation Status:** `PARTIALLY IMPLEMENTED` / `DEFECTIVE` (`DEFECT P0-01`, `DEFECT P0-02`, `REQ-030`, `REQ-042`).

### 2.4 Layer 3 — Creative Production
- **Responsibilities:** Authoring and curating creative artifacts across Stages `01`–`09`: Idea Inception (`01`), Creative Blueprint (`02`), Lyric Generator (`03`), Lyric Curator (`05`), Creative DNA Registry (`06`), Music Style Engineering (`07`), Visual Direction (`08`), and Production Ingest (`09`).
- **Owning Gradle Module:** `:app`
- **Source Packages:** `com.example.ui.creative`, `com.example.ui.generator`, `com.example.ui.curator`, `com.example.ui.production`
- **Repositories:** `CorpusRepository` (App 01 & App 02), `CreativeProductionRepository` (refactored from `OsBranchRepository`).
- **Database Tables Owned:** `creative_projects`, `creative_blueprints`, `dna_profiles`, `music_production_specs`, `visual_direction_specs` (plus read/revision access to `base_titles` and `artifacts`).
- **Permitted Operations:** Creating draft artifacts, running live creative DNA / trope advisory checks, submitting human editorial revisions in App 02, and submitting stage-completion requests to `SovereignKernel`.
- **Prohibited Operations:** Directly mutating `CanonicalProjectContext.currentPipelineStage`, approving G1–G5 gates, or overwriting sealed artifacts.
- **Current Implementation Status:** App 01 & App 02 are `IMPLEMENTED AND VERIFIED` (except SAF rescan `DEFECT P0-02`); Modules 1, 3, 4, 5, 6 are `PARTIALLY IMPLEMENTED` / `PROTOTYPE ONLY`.

### 2.5 Layer 4 — Forensic Intelligence
- **Responsibilities:** Execute independent analytical scans across 8 forensic domains (Stage `11`): Lyric Similarity, Audio Similarity, Watermark Detection, Audio Integrity, Vocal Analysis, Production Integrity, Provenance Verification, and Catalog Collision Detection.
- **Owning Gradle Module:** `:app`
- **Source Package:** `com.example.ui.intelligence` & `com.example.domain.forensic`
- **Repositories & Services:** `ForensicScannerEngine` (8 independent analyzer services), `ForensicRepository`.
- **Database Tables Owned:** `forensic_reports` (read-only access to `base_titles`, `artifacts`, `asset_registry`, `evidence_ledger`).
- **Permitted Operations:** Reading registered assets and corpus text/audio streams; computing deterministic measurements (`PASS`, `WARNING`, `FAIL`, `INCONCLUSIVE`, `NOT_TESTED`); writing immutable `ForensicReportEntity` records and `ANALYTICAL_EVIDENCE` ledger events.
- **Prohibited Operations:** Approving G4 or G5; deleting duplicate candidates; fabricating `PASS` results for unimplemented or unexecuted scanners (`DEFECT P1-09`).
- **Current Implementation Status:** `DEFECTIVE` (`DEFECT P1-09`).

### 2.6 Layer 5 — Governance & Verification
- **Responsibilities:** Present G1–G5 gate evaluation packages, evidence chains, Creative DNA conformance reports, and Forensic Scan reports to the authorized human actor; capture authenticated Keystore digital signatures for `APPROVE`, `REJECT`, `VETO`, `REQUEST_REVISION`, `REQUEST_EVIDENCE`, `DEFER`, and `REOPEN_FOR_REVIEW` (Stages `12` & `13`).
- **Owning Gradle Module:** `:app` (UI) + `:core:kernel` (Decision & Policy enforcement).
- **Source Packages:** `com.example.ui.protocol`, `com.example.ui.services`, `com.example.kernel`
- **Database Tables Owned:** `gate_verifications` / `human_sign_offs` (persisted via Room).
- **Permitted Operations:** Submitting signed human decisions to `SovereignKernel.submitGateDecision(...)`.
- **Prohibited Operations:** Displaying green `VERIFIED` badges from local UI state without verified database + signature backing; bypassing G1–G4 prerequisites.
- **Current Implementation Status:** `PARTIALLY IMPLEMENTED` / `DEFECTIVE` (`DEFECT P0-05`).

### 2.7 Layer 6 — Release & Distribution
- **Responsibilities:** Assemble the complete `ReleasePackage` manifest, execute atomic G5 cryptographic sealing when authorized by the Sovereign Kernel, verify post-seal package parity, and unlock authorized lossless playback in the Master Player (Stage `14`).
- **Owning Gradle Module:** `:app`
- **Source Package:** `com.example.ui.intelligence` (`MasterVaultAndPlayerScreen`) & `com.example.domain.release`
- **Database Tables Owned:** `release_vault`
- **Permitted Operations:** Inspecting release manifests; verifying sealed asset hashes prior to playback; playing masters whose `atomicSealStatus == "SEALED_MASTER"` and whose G5 signature & asset checksums verify at playback time.
- **Prohibited Operations:** Sealing any package without verified G1–G5 gates and a valid Human Creator Keystore signature; playing unsealed or tampered masters (`DEFECT P0-06`).
- **Current Implementation Status:** `DEFECTIVE` (`DEFECT P0-06`) / `DEFERRED BY DESIGN` (Blueprint Section 18).

---

## 3. COMPLETE SOVEREIGN KERNEL SPECIFICATION (SECTION 4.B)

### 3.1 Identity & Authority Model (Section B1)

| Actor Role (`ActorRole`) | Actor Type | Allowed Capabilities | Prohibited Capabilities | Required Authentication |
| :--- | :--- | :--- | :--- | :--- |
| **`HUMAN_CREATOR`** (Student-Director) | `HUMAN` | Full creative, curation, G1–G5 approval, veto, and G5 final release sealing authority. | Overwriting immutable historical evidence in place. | Android Keystore asymmetric key signature (`SHA256withECDSA`) + active authenticated session. |
| **`HUMAN_CURATOR`** | `HUMAN` | Editorial curation (`Stage 05`), line refinement, defect repair, and G1 endorsement proposal. | G5 release sealing; overriding a `HUMAN_CREATOR` veto. | Authenticated Curator session + Keystore signature. |
| **`HUMAN_AUDITOR`** | `HUMAN` | Evidence inspection (`G2`), forensic report verification (`G4` certification), integrity audit execution. | Authoring creative artifacts; G5 release sealing. | Authenticated Auditor session + Keystore signature. |
| **`AI_GENERATOR`** | `AI` | Creating draft lyric/style proposals (`Stage 03` / `Stage 07`) tagged as `DERIVED_EVIDENCE`. | Any gate approval, stage transition, curation endorsement, or release action. | Adapter capability token (`READ_BLUEPRINT`, `CREATE_DRAFT_ONLY`). |
| **`AI_ANALYZER`** | `AI` | Producing advisory analytical estimates (`Stage 06` / `Stage 11`) clearly marked as `ALGORITHMIC_ESTIMATE`. | Approving G3/G4, deleting duplicates, or mutating artifacts. | Adapter capability token (`READ_ARTIFACT`, `WRITE_ADVISORY_REPORT`). |
| **`PRODUCTION_ADAPTER`** | `SYSTEM` | Importing external stems/audio (`Stage 09`) and registering raw file checksums (`Stage 10`). | Modifying existing assets or advancing governance gates. | System capability scope (`INGEST_MEDIA_ONLY`). |
| **`GOVERNOR_ENGINE`** | `SYSTEM` | Deterministic policy evaluation, stage prerequisite enforcement, and hash-chain verification. | Fabricating human approvals or overriding a human veto. | Internal `:core:kernel` singleton authority. |
| **`MASTER_PLAYER`** | `SYSTEM` | Read-only verification and playback of G5-sealed `ReleasePackage` bundles (`Stage 14`). | Playing unsealed tracks or mutating release packages. | Post-seal verification token from `SovereignKernel`. |

### 3.2 Canonical 14-Stage Pipeline State Machine (Section B2 & B3)

| Stage | Name | Input Artifact | Output Artifact | Required Evidence | Entry Condition | Validation Rule | Human Decision Required | Next Stage |
| :---: | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :---: |
| **01** | **Idea Inception** | Creator seed intent | `IdeaArtifact` | `ORIGINAL_EVIDENCE` (Seed hash, timestamp, creator ID) | Active project created | Non-blank concept intent, language, and emotional direction | Creator confirmation of seed | `02` |
| **02** | **Creative Brief & Blueprint** | `IdeaArtifact` | `CreativeBlueprint` | `DERIVED_EVIDENCE` linked to `IdeaArtifact.id` | Stage `01` committed | Valid structure, BPM (`40..220`), meter, key/style, and physical anchor | Creator locks blueprint | `03` |
| **03** | **Lyric Generator** | `CreativeBlueprint` | `LyricDraft` | `DERIVED_EVIDENCE` (Prompt/seed, model/engine ID, draft SHA-256) | Stage `02` locked | Stanzas generated and hashed; zero auto-approval | None (Draft generation only) | `04` |
| **04** | **Evidence Capture** | `LyricDraft` + source files | `EvidenceBundle` | `ORIGINAL_EVIDENCE` & `DERIVED_EVIDENCE` ledger chain | Stage `03` or SAF ingest completed | 100% SHA-256 parity between file/draft bytes and ledger entry | None (Deterministic capture; halts on error) | `05` |
| **05** | **Lyric Curator** | `EvidenceBundle` / `LyricDraft` | `CuratedLyricArtifact` | `AUTHORITY_EVIDENCE` (Curator diff, notes, new revision hash) | Stage `04` verified | Zero unresolved parse/BOM errors; explicit human curation status | Human Curator / Creator endorsement | `06` |
| **06** | **Creative DNA Registry** | `CuratedLyricArtifact` + `CreativeDnaProfile` | `DnaConformanceReport` | `ANALYTICAL_EVIDENCE` (Policy version, anchor check, trope violations) | Stage `05` endorsed | Evaluates lyric against active `CreativeDnaProfile` version | Human review if warnings/exceptions exist | `07` |
| **07** | **Music Style Engineering** | `CreativeBlueprint` + `DnaConformanceReport` | `MusicStyleSpec` | `DERIVED_EVIDENCE` (Arrangement, tempo, T60 target, prompt spec) | Stage `06` completed | Valid BPM, meter, instrumentation, and acoustic room spec | Creator approval of style spec | `08` |
| **08** | **Visual Direction** | `CreativeBlueprint` + `CuratedLyricArtifact` | `VisualGuideSpec` | `DERIVED_EVIDENCE` (Palette, aspect ratio, visual narrative spec) | Stage `07` completed | Complete artwork & typography specification linked to project | Creator approval of visual spec | `09` |
| **09** | **Production Ingest** | External audio master / stems + `MusicStyleSpec` | `ProductionIngestRecord` | `ORIGINAL_EVIDENCE` (Raw audio file stream SHA-256, MIME, size) | Stage `08` completed | Physical audio/stem files accessible and hashed via SAF/storage | Human confirmation of ingested take | `10` |
| **10** | **Asset Registration** | All project source & derived files | `AssetManifest` | `DERIVED_EVIDENCE` (Complete parent-child asset graph & checksums) | Stage `09` completed | Every required lyric, audio, stem, and artwork asset registered & byte-verified | Human verification of asset manifest | `11` |
| **11** | **Forensic Scanner** | `AssetManifest` + Corpus DB | `ForensicScanReport` | `ANALYTICAL_EVIDENCE` (8 scanner classifications & confidence scores) | Stage `10` completed | All 8 scanners executed or explicitly classified (`PASS`/`WARNING`/`FAIL`/`INCONCLUSIVE`/`NOT_TESTED`) | None (Analytical output only; cannot self-approve) | `12` |
| **12** | **G1–G2 Governance** | `CuratedLyricArtifact` + `EvidenceBundle` | `GovernanceVerification(G1, G2)` | `AUTHORITY_EVIDENCE` (Keystore-signed G1 & G2 human decisions) | Stage `11` completed | G1 & G2 evaluated; human digital signature verified for both gates | **Mandatory Human Approval (`G1` & `G2`)** | `13` |
| **13** | **G3–G5 Governance** | `DnaConformanceReport` + `ForensicScanReport` + `AssetManifest` | `ReleasePackage` (Sealed) | `AUTHORITY_EVIDENCE` (Signed G3, G4, and atomic G5 seal signature) | Stage `12` (`G1` & `G2` `APPROVED`) | `G3` `APPROVED`, `G4` `APPROVED`, complete manifest, atomic G5 human signature & post-seal hash check | **Mandatory Human Approval (`G3`, `G4`, `G5`)** | `14` |
| **14** | **Master Player & Distribution** | Sealed `ReleasePackage` | `ReleasedMaster` | `AUTHORITY_EVIDENCE` (Playback/distribution audit event) | Stage `13` (`G5` `APPROVED` & sealed) | Post-seal hash parity verified against `ReleasePackage` before playback | Human distribution authorization | Terminal |

#### Repetition, Re-Entry & Parallel Work Rules
1. **No Forward Stage Skipping:** `GovernorStateMachine.requestStageTransition(projectId, targetStage)` rejects any transition where `targetStage.stageNumber > currentStage.stageNumber + 1`.
2. **Controlled Re-Entry on Rejection or Revision:** If a human decision at Stage `05`, `06`, `12`, or `13` returns `REJECT` or `REQUEST_REVISION`, the project transitions backward to the designated remediation stage (e.g., `12 -> 05` for lyric revision, `13 -> 07/09` for production re-ingest) while preserving all prior artifacts and gate records in the append-only ledger. Creating a revised artifact increments its `versionNumber` and resets downstream gate states (`G1..G5`) to `HUMAN_REVIEW_REQUIRED` for the new revision.
3. **Parallel Work Preparation vs. Canonical Progression:** A creator may draft visual notes (`Module 06`) or style notes (`Module 05`) in the workspace at any time, saving them as `DRAFT` records in Room; however, the project's **canonical pipeline stage** (`STAGE_01`..`STAGE_14`) advances strictly sequentially through the Sovereign Kernel.

---

## 4. GRADLE MODULE TOPOLOGY & DEPENDENCY RULES (SECTION 4.M)

### 4.1 Current vs. Target Gradle Dependency Graph

```text
+-------------------------------------------------------------------+
|                               :app                                |
|   UI Screens • Navigation • ViewModels • Room SQLite Implementation|
|   (Implements KernelPersistencePort & Domain Repository Contracts)|
+-------------------------------------------------------------------+
         |                        |                        |
         v                        v                        v
+-----------------+    +--------------------+    +------------------+
|  :core:shared   |    |    :core:kernel    |    | :core:foundation |
| Security (Key-  |--->| Governor, Decision,|--->| 14 Laws, 8 Roles,|
| store), Backup, |    | Policy, Capability,|    | 14 Stages, G1-G5,|
| Adapter Sandbox |    | Event & Integrity  |    | Domain Contracts |
+-----------------+    +--------------------+    +------------------+
```

### 4.2 Ten Mandatory Dependency & Boundary Invariants
1. **`:core:foundation` Independence:** Zero dependencies on `:core:kernel`, `:core:shared`, `:app`, Room, or Android UI libraries.
2. **`:core:kernel` Sovereignty:** Depends only on `:core:foundation` and `kotlinx.coroutines.core`. Communicates with persistence exclusively via the `KernelPersistencePort` interface defined in `:core:kernel`.
3. **Creative Module Containment:** UI composables and creative repositories cannot mutate `SovereignKernel._projectContext` or `GateEvaluationState` directly; they must submit typed `KernelCommand` requests through `SovereignKernel`.
4. **Forensic Module Containment:** Forensic scanners produce `ForensicScanReport` data objects; they have zero dependency on `DecisionAuthorityService` and cannot invoke gate approvals.
5. **`:core:shared` Containment:** `SovereignSecurityManager` provides Android Keystore cryptographic signing and verification primitives; it cannot approve gates or seal releases on its own.
6. **UI-to-Domain Interface Boundary:** All 16 Compose screens render immutable `StateFlow` state and dispatch user intents to `ElyzarethViewModel`, which delegates governance actions exclusively to `SovereignKernel`.
7. **External Provider Isolation:** Optional adapters (`OfflineFirstIntegrations`) implement `IsolatedExternalAdapter<I, O>` with zero reference to `SovereignKernel` state mutators or Room DAOs.
8. **Zero Circular Dependencies:** Enforced by Gradle module compilation order (`:core:foundation` -> `:core:kernel` -> `:core:shared` -> `:app`).
9. **Zero Duplicated State Machines:** The legacy `protocolStep` (`3..0`) in `ElyzarethUiState` is unified with `SovereignKernel` gate states so only one state machine governs release readiness.
10. **Single Persistence Authority:** `ElyzarethDatabase` (`elyzareth_os_database`) is the single SQLite database on device, accessed exclusively through transactional repository contracts.

### 4.3 Recommendation on Feature Module Splitting (`DEFECT P1-10`)
- **Engineering Recommendation:** Retain the **4-module Gradle topology** (`:core:foundation`, `:core:kernel`, `:core:shared`, `:app`) for Phase 1 and Phase 2 while enforcing package-private/internal visibility on Room DAOs inside `:app` and moving all domain repository interfaces into `:core:foundation` / `:core:kernel`. Splitting `:app` into 14 separate Gradle feature modules now would increase build overhead and complicate the preservation of App 01 / App 02 without adding runtime security. Once Phases 1–4 are complete, `:feature:corpus` and `:feature:forensic` may optionally be extracted in Phase 5 (`P3`) if independent library compilation is desired by the Student-Director (`ARCH-DEC-02`).
