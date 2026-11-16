# ELYZARETH OS — FEATURE MODULE, CREATIVE PRODUCTION AND UI TRACEABILITY 1.0

**Assignment ID:** `EOS-ARCH-RECON-001` (Section 4.G, Section 4.L, Section 4.M, Section 4.O, Section 4.P & Deliverable #9)  
**Architectural Authority:** ELYZARETH OS — Final Master Blueprint  
**Decision Authority:** Student-Director / Human Creator  
**Status:** `SPECIFICATION COMPLETE — IMPLEMENTATION FROZEN PENDING APPROVAL`

---

## 1. CREATIVE PRODUCTION MODULE CONTRACTS (SECTION 4.G)

Every creative production module operates strictly within **Layer 3 (`Creative Production`)**. It may create proposals, run local validations, and record human editorial revisions, but **cannot directly advance canonical pipeline stages or approve G1–G5 gates**.

| Module | Pipeline Stages | Inputs | Outputs | Data Ownership (Room Tables) | Kernel Interaction | Human Decision Points | Offline Operation |
| :--- | :---: | :--- | :--- | :--- | :--- | :--- | :--- |
| **1. Idea & Blueprint Studio** (`IdeaBlueprintStudioScreen`) | `01`, `02` | Creator seed intent, language, physical anchor, structure, BPM, meter | `IdeaArtifact` (`01`), `CreativeBlueprint` (`02`) | `creative_projects`, `creative_blueprints` | Submits `RequestStageCompletion(STAGE_01 / STAGE_02)` | Creator confirms seed (`01`) and locks blueprint (`02`) | 100% Local Room |
| **2. Lyric Generator — App 01** (`LyricGeneratorScreen`) | `03`, `04` | `CreativeBlueprint`, Physical Anchor (`9`), Witness Pillar (`5`), BPM, Meter, Seed | `LyricDraft` (`03`) + `EvidenceBundle` (`04`) | `artifacts` (`type = "lyric_draft"`), `evidence_ledger` | Logs `DERIVED_EVIDENCE` draft capture; **zero gate/approval authority** | Human selects draft to send to Curator (`Stage 05`) | 100% Local Deterministic Witness Engine |
| **3. Lyric Curator & App 02 Corpus Curator** (`CorpusDashboardScreen` & `LyricCuratorModuleScreen`) | `04`, `05` | SAF directory stream or `LyricDraft` (`03`) | `CuratedLyricArtifact` (`05`) + SAF `CorpusAuditReport` | `base_titles`, `artifacts`, `evidence_ledger` | Submits curator endorsement (`AUTHORITY_EVIDENCE`) as prerequisite for `G1` | Line-by-line editing, defect repair/reject, `RELEASE READY` endorsement | 100% Local SAF + Room SQLite |
| **4. Creative DNA Engine** (`CreativeDnaEngineScreen`) | `06` | `CuratedLyricArtifact` + versioned `CreativeDnaProfile` | `DnaConformanceReport` (`06`) | `dna_profiles`, `evidence_ledger` | Submits `ANALYTICAL_EVIDENCE` report to Kernel for `G3` review; **never rewrites lyrics** | Creator selects active DNA profile & reviews any warnings at `G3` | 100% Local Rule Evaluator |
| **5. Music Production Console** (`MusicProductionConsoleScreen`) | `07`, `09` | `CreativeBlueprint` + external audio stems/masters via SAF | `MusicStyleSpec` (`07`), `ProductionIngestRecord` (`09`) | `music_production_specs`, `asset_registry` | Registers ingested stem/master SHA-256 hashes (`Stage 09 -> 10`) | Creator approves style spec (`07`) and confirms ingested audio takes (`09`) | 100% Local SAF Ingest (Suno prompt export optional) |
| **6. Visual Direction Studio** (`VisualDirectionStudioScreen`) | `08` | `CreativeBlueprint` + SAF artwork image files | `VisualGuideSpec` (`08`) | `visual_direction_specs`, `asset_registry` | Registers artwork asset checksums and visual spec | Creator approves visual narrative and master cover spec | 100% Local SAF + Room |

---

## 2. SCREEN-TO-ARCHITECTURE TRACEABILITY MATRIX (ALL 16 DESTINATIONS — SECTION 4.L)

| # | Route ID (`OsModuleDestination`) | Composable Screen & File | Owning Layer | Required Data (`StateFlow`) | Read / Write Capability | Human Decision Actions | UI Defect / Remediation Required |
| :---: | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **00** | `command_centre` | `CommandCentreScreen` (`CommandCentreScreen.kt:80`) | Application Shell | `projectContext`, `baseTitles`, `blueprints` | Read-only dashboard + navigation router | Project context switch | **Fix `DEFECT P0-03`:** Stage pills (`line 290`) must inspect stage details rather than bypassing prerequisites. **Fix `DEFECT P1-09`:** Remove `.coerceAtLeast(390)` and hardcoded `100%` health ring. |
| **01** | `idea_blueprint` | `IdeaBlueprintStudioScreen` (`CreativeBranchScreens.kt:79`) | Layer 3 (Creative) | `blueprints`, `projectContext` | Read/Write `creative_blueprints` | Lock Creative Blueprint (`Stage 02`) | Label seeded `BP-001..003` as `[DEMO FIXTURE]`; bind new blueprints to `activeProjectId`. |
| **02** | `lyric_generator` | `LyricGeneratorScreen` (`LyricGeneratorScreen.kt:65`) | Layer 3 (Creative) | `uiState.generatedStanzas`, `customLyricEvaluation` | Read anchors/pillars; Write draft proposals | Synthesize stanzas; run live trope check | **Preserved App 01 Baseline.** Add button to persist generated draft directly into Room `artifacts` + `evidence_ledger` (`Stage 03 -> 04`). |
| **03** | `lyric_curator` | `LyricCuratorModuleScreen` (`CreativeBranchScreens.kt:253`) | Layer 3 (Creative) | `selectedBaseTitle`, `baseTitles` | Read/Write `artifacts` revision | Curator endorsement (`Stage 05`) | Reconcile with App 02 (`ARCH-DEC-05`) so edits create a new `ArtifactEntity` revision rather than only updating `BaseTitleEntity.status`. |
| **04** | `creative_dna` | `CreativeDnaEngineScreen` (`CreativeBranchScreens.kt:393`) | Layer 3 (Creative) | `dnaProfiles`, `selectedBaseTitle` | Read/Write `dna_profiles` | Select DNA profile; run conformance check | Replace static `"VERIFIED"` factory card (`lines 474–477`) with live evaluation of the active song against the selected `CreativeDnaProfile`. |
| **05** | `music_production` | `MusicProductionConsoleScreen` (`ProductionBranchScreens.kt:69`) | Layer 3 (Creative) | `productionTracks` | Read/Write `music_production_specs` | Ingest stems via SAF; lock style spec | Add real SAF audio file picker (`OpenDocument`) to compute real WAV stream SHA-256 on ingest. |
| **06** | `visual_direction` | `VisualDirectionStudioScreen` (`ProductionBranchScreens.kt:199`) | Layer 3 (Creative) | `visualAssets` | Read/Write `visual_direction_specs` | Approve visual spec; attach cover art | Add SAF image file picker and resolution/hash verification. |
| **07** | `corpus_archive` | `CorpusDashboardScreen` (`CorpusDashboardScreen.kt:110`) | Layer 2 & 3 (Corpus / App 02) | `baseTitles`, `selectedBaseTitleArtifacts`, `uiState.report` | Read/Write `base_titles` & `artifacts` | SAF Scan, Curation Pipeline, Governor Review, Manifest Export | **Preserved App 02 Baseline.** Remediate `DEFECT P0-02` (Two-Phase transactional SAF rescan) and verify 1,000+ item scroll on physical APK. |
| **08** | `forensic_lab` | `ForensicLabScreen` (`IntelligenceAndVaultScreens.kt:72`) | Layer 4 (Forensic) | `forensicReports`, `selectedBaseTitle` | Read corpus/assets; Write `forensic_reports` | Trigger 8-scanner forensic examination | **Fix `DEFECT P1-09`:** Replace hardcoded `1%` / `VERIFIED CLEAN` generator with real `ForensicScannerEngine` (`PASS`/`WARNING`/`FAIL`/`INCONCLUSIVE`/`NOT_TESTED`). |
| **09** | `governance_console` | `GovernanceSuiteScreen` (`SystemAndKernelScreens.kt:84`) & `ProtocolConsoleScreen` | Layer 5 (Governance) | `gateEvaluationState`, `protocolStep` | Submit signed `GateDecision` to Kernel | `APPROVE`, `REJECT`, `VETO`, `REQUEST_REVISION` on `G1..G5` | **Fix `DEFECT P0-05`:** Require verified evidence prerequisites and Android Keystore signature for `G1..G5` approvals; unify with `3.2.1.0 Protocol`. |
| **10** | `evidence_vault` | `EvidenceVaultScreen` (`IntelligenceAndVaultScreens.kt:160`) | Layer 2 (Evidence) | `evidenceRecords`, `allArtifacts` | **Strictly Read-Only Inspection** | Trigger live hash-chain parity verification | Replace static `"100% VERIFIED"` banner (`line 198`) with live `IntegrityEngine.verifyEvidenceChain()` execution result. |
| **11** | `asset_registry` | `AssetRegistryScreen` (`ProductionBranchScreens.kt:309`) | Layer 2 (Asset Registry) | `registeredAssets` | Read/Write `asset_registry` | Import & register file; verify byte parity | **Fix `REQ-042`:** Compute SHA-256 from actual SAF file bytes rather than hashing title strings. |
| **12** | `master_vault` | `MasterVaultAndPlayerScreen` (`IntelligenceAndVaultScreens.kt:254`) | Layer 6 (Release & Player) | `masterReleases`, `gateEvaluationState` | Read `release_vault`; G5 seal & playback | Authorize G5 seal; play verified sealed master | **Fix `DEFECT P0-06`:** Label seeded `REL-001` as `TEST_FIXTURE`; block `sealMasterRelease` unless `G1..G5` are Keystore-signed; block playback of unsealed/tampered masters. |
| **13** | `system_services` | `SystemServicesScreen` (`SystemAndKernelScreens.kt:264`) | Supporting Branches | `kernelEvents`, `lastBackupSnapshot` | Trigger `.elyzbak` backup/restore; inspect adapters | Create/restore encrypted snapshot | Connect to real `SnapshotBackupManager` (`.elyzbak` SQLite + media archive) and persistent `evidence_ledger`. |
| **14** | `project_overview` | `ProjectAndSystemOverviewScreen` (`SystemAndKernelScreens.kt:390`) | Analytics & Telemetry | Live Room counts & gate states | **Strictly Read-Only** | Inspect project & pipeline telemetry | Remove `.coerceAtLeast(390)` and hardcoded `"100% Structurally Integrated"` progress bar (`lines 452–457`). |
| **15** | `sovereign_kernel` | `SovereignKernelScreen` (`SystemAndKernelScreens.kt:490`) | Layer 1 & Foundation | `projectContext`, `ConstitutionalLaws` | **Strictly Read-Only** | Inspect active kernel state & 14 Constitutional Laws | Display all 14 Constitutional Laws from Master Blueprint Section 15. |

---

## 3. UI CONTAINMENT, ACCESSIBILITY & STATE RULES (SECTION 4.L)

1. **Authoritative Context Strip:** Every module screen displays the active `ProjectAggregate` (`projectId`, `title`, `currentPipelineStage`, `G1..G5` status summary, and `Release Lock` badge).
2. **Strict Separation of Modes:**
   - **Playback Mode (`Master Player`):** Enabled exclusively for `SEALED_MASTER` packages passing live byte-hash verification.
   - **Editing Mode (`Generator` / `Curator`):** Creates new revisions (`v+1`); disabled on `RELEASED & FROZEN` rows.
   - **Evidence Inspection Mode (`Evidence Vault` / `ArtifactDetailModal`):** Strictly read-only (`0` mutation affordances).
   - **Governance Approval Mode (`Governance Console`):** Displays prerequisite checklist, evidence hashes, and requires explicit human signature confirmation.
3. **Required UI States per Screen:** Every data-driven screen must implement deterministic `Loading`, `Empty (Zero Records)`, `Populated (Live Data vs. Tagged Test Fixture)`, `Error (Diagnostic + Retry/Recovery)`, and `Offline (Local Sovereign Mode Active)` states.
4. **Accessibility & Bounded Scrolling:** Minimum `48.dp x 48.dp` touch targets, `contentDescription` on all icons, unique `Modifier.testTag(...)` on all interactive controls, and bounded `LazyColumn` containers (`weight(1f)` / `fillMaxSize()` inside `Scaffold(innerPadding)`) so bottom navigation never obscures list items.

---

## 4. BUILD, RELEASE AND OPERATIONAL READINESS SPECIFICATION (SECTION 4.O)

1. **Gradle & Toolchain Configuration:**
   - AGP `9.1.1`, Kotlin `2.2.10`, KSP `2.2.10-2.0.2`, Room `2.7.0`, Compile/Target SDK `36`, Min SDK `24`, Java `11`.
   - Room Schema Export: Configure `ksp { arg("room.schemaLocation", "$projectDir/schemas") }` in `app/build.gradle.kts` so every database version (`1.json`, `2.json`) is committed to version control.
2. **Signing & Keystore Hygiene:**
   - Preserve `signingConfigs` (`debugConfig` and `release`) in `app/build.gradle.kts:26–50` untouched per environment constraints.
3. **Release Artifact & Checksum Manifest:**
   - Every build candidate generates:
     - APK/AAB binary (`/app/build/outputs/apk/debug/app-debug.apk`),
     - `SHA-256` binary checksum,
     - Git source tree hash / inventory manifest, and
     - Executed test report (`BUILD_AND_RUNTIME_EVIDENCE_STATUS`).
4. **Constitutional Release Lock (Blueprint Section 18):**
   - Production G5 sealing and Master Player distribution unlock remain **locked by default** until all Phase 1–5 acceptance tests pass and the Student-Director grants explicit authorization.

---

## 5. HISTORICAL ARCHITECTURE & DATA PRESERVATION REGISTER (SECTION 4.P)

Classification of all historical archives, TSX prototypes, and baseline modules using the 6 canonical treatments (`PRESERVE AS ACTIVE`, `REINTEGRATE`, `MIGRATE`, `ARCHIVE`, `RETIRE`, `REQUIRES HUMAN DECISION`):

| Item ID | Historical Source / Artifact | Location | Classification | Functional Value & Destination Module | Migration Risks & Safeguards |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **`HIST-01`** | **App 02 — Lyric Corpus Curator (Android v38.1 Baseline)** | `CorpusDashboardScreen.kt`, `CurationWorkflowPipeline.kt`, `CorpusRepository.kt`, `CorpusModels.kt`, `CorpusSeedData.kt` | **`PRESERVE AS ACTIVE`** | Core 390-title / 510-artifact baseline, SAF directory scanner, SHA-256 evidence inspector, and 3-stage curation pipeline (`Module 07`). | **Protected Capability:** Must never be replaced by simplified CRUD screens. Remediate `DEFECT P0-01` & `DEFECT P0-02` in place. |
| **`HIST-02`** | **App 01 — Witness Lyric Generator & Trope Scanner** | `LyricGeneratorScreen.kt`, `CorpusRepository.kt:329–466` | **`PRESERVE AS ACTIVE`** | 9 Physical Witness Anchors, 5 Witness Pillars, Meter/BPM synthesis, and live prohibited-trope evaluator (`Module 02`). | Preserve 100% of anchor/pillar/trope logic; add Room draft persistence for Stage `03 -> 04`. |
| **`HIST-03`** | **3.2.1.0 Protocol Console (`Listen -> Evaluate -> Decide -> Freeze`)** | `ProtocolConsoleScreen.kt` | **`REINTEGRATE`** | Established 4-step auditory & witness inspection ritual and 4-State Verification Model (`Module 09`). | Bind steps `3..0` to canonical `G1..G5` gate evaluations upon approval of `ARCH-DEC-04`. |
| **`HIST-04`** | **Legacy React/TypeScript App 02 Curator Applet (13 files)** | `/elyzareth-app02-curator/src/*` (`main.tsx`, `parser.ts`, `db.ts`, `mockCorpus.ts`, `components/*`) | **`ARCHIVE`** | Historical web reference from which the Kotlin App 02 Curator was migrated (`TSX_SOURCE_MAPPING_REGISTER.md`). | Retain 100% read-only on disk in `/elyzareth-app02-curator/`; never delete or execute in Android runtime. |
| **`HIST-05`** | **C18 Creative Constitution & Physical Witness Anchor Doctrine** | `ConstitutionalFoundation.kt`, `CreativeDnaEntity` (`DNA-001`, `DNA-002`) | **`MIGRATE`** | Sovereign human authorship, layer monotonicity, physical anchors, and `T60 = 0.42s` dry room rules. | Separate the 14 universal Constitutional Laws (`ConstitutionalFoundation.kt`) from the versioned `CreativeDnaProfile` (`DNA-ELYZARETH-WITNESS-v1.0`) per `DEFECT P1-08`. |
| **`HIST-06`** | **Phase-A Unapproved OS Branch Additions (11 New Screens & 8 Room v2 Tables)** | `CreativeBranchScreens.kt`, `ProductionBranchScreens.kt`, `IntelligenceAndVaultScreens.kt`, `SystemAndKernelScreens.kt`, `OsBranchModels.kt`, `OsBranchRepository.kt` | **`REQUIRES HUMAN DECISION`** | Structural & UI foundation for Modules 1, 3, 4, 5, 6, 8, 9, 10, 11, 12, 13, 14. | Subject to `ARCH-DEC-03` & `ARCH-DEC-08`: retain under quarantine, strip fabricated `PASS`/`SEALED_MASTER` claims, and upgrade module-by-module under kernel contracts. |
| **`HIST-07`** | **Recovered Historical Archive Spaces (127 Exported Prototypes: v38.2, Shell v17, C17 Workbench, v32.0.0-RES)** | Referenced in Master Blueprint Preamble & Section 16 | **`ARCHIVE` / `RETIRE`** | Useful IP (deterministic containment, forensic scanner taxonomy, 5 gates, separation of playback vs. evidence) is consolidated into Master Blueprint 1.0. | **Strict Rule:** Do not merge competing state models or duplicate dashboards from old versions into the single Sovereign Kernel. |
