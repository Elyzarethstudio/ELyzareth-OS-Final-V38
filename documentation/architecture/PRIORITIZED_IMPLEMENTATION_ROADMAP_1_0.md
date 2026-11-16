# ELYZARETH OS — PRIORITIZED IMPLEMENTATION ROADMAP 1.0

**Assignment ID:** `EOS-ARCH-RECON-001` (Section 8, Section 9 & Deliverable #11)  
**Architectural Authority:** ELYZARETH OS — Final Master Blueprint  
**Decision Authority:** Student-Director / Human Creator  
**Implementation Status:** **`FROZEN — AWAITING STUDENT-DIRECTOR AUTHORIZATION`**

---

## 1. PHASE ARCHITECTURE & DEPENDENCY GRAPH

Per Section 8, implementation is structured into **seven dependency-ordered phases (`PHASE 0` through `PHASE 6`)**. No downstream phase may begin until all acceptance criteria and test gates of its prerequisite phase are verified.

```text
[PHASE 0: BASELINE_EVIDENCE_FREEZE]  (Completed in Read-Only Audit)
                 │
                 ▼
[PHASE 1: KERNEL_AND_DATA_SAFETY_BASELINE]  (P0 Blockers: DB Migration, SAF Rescan, Kernel Guards, G5 Lock)
                 │
                 ▼
[PHASE 2: SOVEREIGN_KERNEL_1_0]             (P1 Blockers: Persistent Ledger, Keystore ECDSA, Recovery, DNA Separation)
                 │
                 ▼
[PHASE 3: CREATIVE_PRODUCTION_2_0]          (P2: 14-Stage Creative Contracts, Real Asset Registry, Offline Adapters)
                 │
                 ▼
[PHASE 4: FORENSIC_INTELLIGENCE_3_0]        (P2: 8 Independent Scanners, 7-Factor Collision, G4 Audit Certification)
                 │
                 ▼
[PHASE 5: GOVERNANCE_AND_RELEASE_1_0]       (P2/P3: Full G1-G4 Enforcement, Atomic G5 Sealing, Master Player Unlock)
                 │
                 ▼
[PHASE 6: ELYZARETH_OS_FINAL_1_0_CANDIDATE] (P3/P4: Full Regression, Physical Device 1,000+ SAF & Keystore Sign-Off)
```

---

## 2. COMPLETE ROADMAP ITEM REGISTER (WITH ALL 13 MANDATORY ATTRIBUTES PER ITEM)

Every roadmap task below specifies: **(1) Unique ID**, **(2) Priority**, **(3) Blueprint Reference**, **(4) Current Repository Status**, **(5) Affected Files**, **(6) Required Correction**, **(7) Dependencies**, **(8) Risk**, **(9) Acceptance Criteria**, **(10) Required Tests**, **(11) Evidence Needed**, **(12) Human Decision Requirement**, and **(13) Implementation Authorization Status**.

---

### PHASE 0 — Baseline and Evidence Freeze (`Deliverable: BASELINE_EVIDENCE_FREEZE`)

#### `RM-P0-00` — Repository Inventory, Schema Verification & Contradiction Reconciliation
- **Priority:** `P0`
- **Blueprint Reference:** Section 13 (Phase 0) & Assignment Sections 3–7
- **Current Repository Status:** `IMPLEMENTED AND VERIFIED` (Documentation completed in `EOS-ARCH-RECON-001`)
- **Affected Files:** `/documentation/architecture/*_1_0.md`, `/documentation/implementation-progress/*`
- **Required Correction:** Reconcile all 84 blueprint requirements, document `DEFECT P0-01`..`DEFECT P1-10`, verify KSP `ElyzarethDatabase_Impl.kt` schema, and freeze code changes.
- **Dependencies:** None
- **Risk:** None (Read-only inspection and documentation)
- **Acceptance Criteria:** All 12 architecture deliverables created; zero application/database files modified.
- **Required Tests:** Static source & KSP inspection
- **Evidence Needed:** Complete file inventory and line-number citations
- **Human Decision Requirement:** Student-Director review of `EOS-ARCH-RECON-001` deliverables
- **Implementation Authorization Status:** `AUTHORIZED (DOCUMENTATION ONLY — COMPLETED)`

---

### PHASE 1 — P0 Data Preservation and Kernel Safety (`Deliverable: KERNEL_AND_DATA_SAFETY_BASELINE`)

#### `RM-P0-01` — Non-Destructive Room Database Migration (`MIGRATION_1_2`) & Cloned `.v1.bak` Recovery
- **Priority:** `P0 — Constitutional & Data-Integrity Blocker`
- **Blueprint Reference:** Section 9, Section 14, `DEFECT P0-01`, `SYS-CON-08`
- **Current Repository Status:** `DEFECTIVE` (`ElyzarethDatabase.kt:29, 41` uses `fallbackToDestructiveMigration()`)
- **Affected Files:** `/app/src/main/java/com/example/data/local/ElyzarethDatabase.kt`, `/app/build.gradle.kts` (schema export), `/app/src/test/java/com/example/DatabaseMigrationVerificationTest.kt`
- **Required Correction:** Remove `.fallbackToDestructiveMigration()`, implement pre-upgrade `.v1.bak` file cloning with SHA-256 verification, register `MIGRATION_1_2` creating the 8 v2 tables while preserving `base_titles` and `artifacts` 100% intact, and enable Room schema export.
- **Dependencies:** `RM-P0-00`
- **Risk:** High if omitted (destroys existing v1 user corpus on upgrade).
- **Acceptance Criteria:** Upgrading a populated v1 database (`390/510` or `1,050+` curated records) to v2 preserves 100% of rows and column values; simulated faulty migration rolls back cleanly to `.v1.bak`.
- **Required Tests:** `DB-A1`, `DB-A2`, `DB-A3`, `DB-A4`
- **Evidence Needed:** Executed JUnit/Robolectric test report on real file-backed SQLite databases
- **Human Decision Requirement:** Student-Director authorization to execute Phase 1 database remediation
- **Implementation Authorization Status:** `NOT GRANTED (AWAITING APPROVAL)`

---

#### `RM-P0-02` — Two-Phase Atomic SAF Rescan & Immutable Frozen Artifact Protection
- **Priority:** `P0 — Constitutional & Data-Integrity Blocker`
- **Blueprint Reference:** Section 7, Section 9, Constitutional Law #2, `DEFECT P0-02`, `SYS-CON-09`, `SYS-CON-10`
- **Current Repository Status:** `DEFECTIVE` (`CorpusRepository.kt:218–222` executes `clearAllArtifacts()` / `clearAllBaseTitles()` without `@Transaction` whenever `newTitles.isNotEmpty()`)
- **Affected Files:** `/app/src/main/java/com/example/data/repository/CorpusRepository.kt`, `/app/src/main/java/com/example/data/local/ElyzarethDao.kt`, `/app/src/test/java/com/example/SafRescanPreservationTest.kt`
- **Required Correction:**
  1. Separate SAF stream reading/hashing (Phase 1) from a single Room `@Transaction reconcileSafScanAtomically(...)` (Phase 2).
  2. Remove `clearAll*()` from `scanSafFolder()`.
  3. Preserve existing `bt_*` and `art_*` IDs and curation states (`RELEASE READY`, `RELEASED & FROZEN`, `notes`) for matched files.
  4. Enforce Constitutional Law #2: if an external file changes on a `RELEASED & FROZEN` artifact, keep the frozen row untouched and insert a new `CURATION REQUIRED` revision row.
  5. Retain missing files with `[SOURCE FILE MISSING IN LATEST SCAN]` audit note.
- **Dependencies:** `RM-P0-01`
- **Risk:** High if omitted (wipes user curation work on every SAF rescan).
- **Acceptance Criteria:** All 7 SAF scenarios (`DB-B1` through `DB-B7`) pass with zero loss of curated or frozen records.
- **Required Tests:** `DB-B1`, `DB-B2`, `DB-B3`, `DB-B4`, `DB-B5`, `DB-B6`, `DB-B7`
- **Evidence Needed:** Executed Robolectric test suite (`SafRescanPreservationTest`) + physical-device 1,000+ SAF scan verification (`DEV-01`)
- **Human Decision Requirement:** Student-Director authorization to execute Phase 1 SAF remediation
- **Implementation Authorization Status:** `NOT GRANTED (AWAITING APPROVAL)`

---

#### `RM-P0-03` — Sovereign Kernel Guarded Stage Transitions & Prerequisites Enforcement
- **Priority:** `P0 — Constitutional Blocker`
- **Blueprint Reference:** Section 3, Section 5, Section 14 (#1, #2, #7), `DEFECT P0-03`, `SYS-CON-02`, `SYS-CON-03`
- **Current Repository Status:** `DEFECTIVE` (`SovereignKernel.kt:85–100` unconditionally overwrites `currentPipelineStage`)
- **Affected Files:** `/core/foundation/src/main/java/com/example/foundation/ConstitutionalFoundation.kt`, `/core/kernel/src/main/java/com/example/kernel/SovereignKernel.kt`, `/app/src/main/java/com/example/ui/commandcentre/CommandCentreScreen.kt`, `/app/src/main/java/com/example/viewmodel/ElyzarethViewModel.kt`
- **Required Correction:**
  1. Enforce sequential progression (`target == current + 1`) or controlled backward revision re-entry.
  2. Require stage output artifact verification and `G1..G5` gate prerequisites before advancing stages.
  3. Serialize concurrent transitions with `Mutex` and command idempotency keys.
  4. Change Command Centre stage pills (`CommandCentreScreen.kt:290`) from direct stage-jump mutators to read-only stage inspectors / guarded transition requests.
- **Dependencies:** `RM-P0-00`
- **Risk:** High if omitted (allows UI to bypass the entire 14-stage pipeline).
- **Acceptance Criteria:** Out-of-order, unauthenticated, or prerequisite-missing stage transitions are deterministically rejected and logged.
- **Required Tests:** `UT-KERN-01`, `UT-KERN-02`, `UT-KERN-03`, `SEC-01`
- **Evidence Needed:** Executed JUnit test results in `SovereignKernelTransitionTest`
- **Human Decision Requirement:** Student-Director approval of `COMPLETE_TARGET_ARCHITECTURE_1_0.md`
- **Implementation Authorization Status:** `NOT GRANTED (AWAITING APPROVAL)`

---

#### `RM-P0-04` — Gate Default Lock (`NOT_EVALUATED`) & Quarantine of Premature G5 / Demo Fixtures
- **Priority:** `P0 — Constitutional Blocker`
- **Blueprint Reference:** Section 4, Section 14 (#8–#15), Section 18, `DEFECT P0-05`, `DEFECT P0-06`, `DEFECT P1-09`
- **Current Repository Status:** `DEFECTIVE` (`ElyzarethViewModel.kt:76–79` defaults G1–G4 to `"VERIFIED"`; `OsBranchRepository.kt:265, 425` pre-seeds `"SEALED_MASTER"` and allows unchecked `sealMasterRelease()`)
- **Affected Files:** `/core/kernel/src/main/java/com/example/kernel/SovereignKernel.kt`, `/app/src/main/java/com/example/viewmodel/ElyzarethViewModel.kt`, `/app/src/main/java/com/example/data/repository/OsBranchRepository.kt`, `/app/src/main/java/com/example/ui/intelligence/IntelligenceAndVaultScreens.kt`
- **Required Correction:**
  1. Initialize all gates (`G1..G5`) to `NOT_EVALUATED`.
  2. Tag all seeded rows in `OsBranchRepository` with `recordOrigin = "TEST_FIXTURE_DEMO"` and change seeded `REL-001` from `"SEALED_MASTER"` to `"DEMO_FIXTURE_UNSEALED"`.
  3. Block `OsBranchRepository.sealMasterRelease()` and Master Player playback unless `SovereignKernel.canSealMasterRelease()` and post-seal hash verification succeed.
- **Dependencies:** `RM-P0-03`
- **Risk:** Critical if omitted (violates Blueprint Section 18 prohibition on premature G5 and Master Player unlock).
- **Acceptance Criteria:** Zero gates start pre-verified; `sealMasterRelease()` and Master Player playback reject unverified or demo-fixture records.
- **Required Tests:** `GOV-T01`, `GOV-T02`, `GOV-T07`, `SEC-06`, `SEC-08`, `UI-03`
- **Evidence Needed:** JUnit & Robolectric Compose verification
- **Human Decision Requirement:** `ARCH-DEC-03` & Phase 1 authorization
- **Implementation Authorization Status:** `NOT GRANTED (AWAITING APPROVAL)`

---

### PHASE 2 — Evidence, Identity and Security (`Deliverable: SOVEREIGN_KERNEL_1_0`)

#### `RM-P1-05` — Persistent Append-Only Evidence Ledger & SQLite Immutability Triggers
- **Priority:** `P1 — Core Architecture Blocker`
- **Blueprint Reference:** Section 7, Section 14 (#5, #6), `DEFECT P0-04`, `SYS-CON-06`, `SYS-CON-13`
- **Current Repository Status:** `DEFECTIVE` (`SovereignKernel._eventLedger` is volatile in-memory `take(200)`; `OsBranchDao` uses `OnConflictStrategy.REPLACE`)
- **Affected Files:** `/core/kernel/src/main/java/com/example/kernel/SovereignKernel.kt`, `/app/src/main/java/com/example/data/model/OsBranchModels.kt`, `/app/src/main/java/com/example/data/local/OsBranchDao.kt`, `/app/src/main/java/com/example/data/local/ElyzarethDatabase.kt`
- **Required Correction:** Expand `evidence_ledger` to the 18-column canonical schema (`Migration(2, 3)`), wire `SovereignKernel` to persist every event via `KernelPersistencePort` inside the caller's `@Transaction`, enforce `OnConflictStrategy.ABORT`, add SQLite `BEFORE UPDATE` / `BEFORE DELETE` triggers, and implement `verifyEvidenceChain()`.
- **Dependencies:** `RM-P0-01`, `RM-P0-03`
- **Risk:** High (without persistent hash-chained evidence, audit trails do not survive restart).
- **Acceptance Criteria:** Events survive restart; `UPDATE`/`DELETE` on `evidence_ledger` throws `SQLiteConstraintException`; tampering with any payload breaks `verifyEvidenceChain()`.
- **Required Tests:** `UT-EVID-01`, `DB-05`, `DB-06`, `IT-PIPE-01`
- **Evidence Needed:** Robolectric SQLite trigger and hash-chain verification report
- **Human Decision Requirement:** Phase 2 authorization
- **Implementation Authorization Status:** `NOT GRANTED (AWAITING APPROVAL)`

---

#### `RM-P1-06` — Android Keystore Asymmetric Digital Signatures (`SHA256withECDSA`) & 7-Tuple Human Sign-Off
- **Priority:** `P1 — Core Architecture Blocker`
- **Blueprint Reference:** Section 9, Section 10, Section 14 (#3, #13), `DEFECT P1-07`, `SYS-CON-05`
- **Current Repository Status:** `DEFECTIVE` (`SovereignSecurityManager.signPayload()` computes a plain SHA-256 string hash)
- **Affected Files:** `/core/shared/src/main/java/com/example/shared/SystemUtilitiesAndServices.kt`, `/core/kernel/src/main/java/com/example/kernel/SovereignKernel.kt`
- **Required Correction:** Implement `SovereignSignatureEngine` using `AndroidKeyStore` (`ECDSA P-256`, `SHA256withECDSA`) with 7-tuple canonical JSON signing envelopes and cryptographic signature verification before any gate approval or G5 seal is accepted.
- **Dependencies:** `RM-P1-05`
- **Risk:** High (prevents forged or synthetic gate approvals).
- **Acceptance Criteria:** Plain hashes or tampered payloads fail `verify()`; valid ECDSA signatures pass and bind identity, project, artifact version, gate, evidence hash, policy version, and timestamp.
- **Required Tests:** `GOV-T03`, `GOV-T04`, `SEC-02`, `SEC-03`, `SEC-04`, `SEC-09`
- **Evidence Needed:** JUnit cryptographic verification tests + physical-device Keystore test (`DEV-02`)
- **Human Decision Requirement:** Phase 2 authorization
- **Implementation Authorization Status:** `NOT GRANTED (AWAITING APPROVAL)`

---

#### `RM-P1-07` — Separation of 14 Universal Constitutional Laws from Versioned Creative DNA Profiles
- **Priority:** `P1 — Core Architecture Blocker`
- **Blueprint Reference:** Section 4 (G3), Section 6, Section 15, `DEFECT P1-08`, `SYS-CON-20`
- **Current Repository Status:** `DEFECTIVE` (`ConstitutionalFoundation.kt` defines only 6 laws and hardcodes artist-specific imagery/T60 rules as universal laws)
- **Affected Files:** `/core/foundation/src/main/java/com/example/foundation/ConstitutionalFoundation.kt`, `/app/src/main/java/com/example/data/repository/OsBranchRepository.kt`
- **Required Correction:** Encode all 14 universal Constitutional Laws from Master Blueprint Section 15 in `ConstitutionalFoundation.kt`, and preserve the established Elyzareth Physical Witness Anchors, 5 Pillars, prohibited tropes, and `T60 = 0.42s` rules in the versioned canonical `CreativeDnaProfile` (`DNA-ELYZARETH-WITNESS-v1.0`).
- **Dependencies:** `RM-P0-00`
- **Risk:** Medium (architectural clarity & G3 policy versioning).
- **Acceptance Criteria:** All 14 Constitutional Laws are enforced in `:core:foundation`, and `DNA-ELYZARETH-WITNESS-v1.0` preserves 100% of existing Elyzareth lyric/acoustic rules.
- **Required Tests:** `UT-POL-01`
- **Evidence Needed:** Unit test verification
- **Human Decision Requirement:** Phase 2 authorization
- **Implementation Authorization Status:** `NOT GRANTED (AWAITING APPROVAL)`

---

#### `RM-P1-08` — Real Encrypted Database & Media Backup/Restore Engine (`.elyzbak`)
- **Priority:** `P1 — Core Architecture Blocker`
- **Blueprint Reference:** Section 9, Section 14 (#16), `SYS-CON-14`, `SYS-CON-17`
- **Current Repository Status:** `PROTOTYPE ONLY` (`SnapshotBackupManager.generateSnapshot()` only hashes 4 integers in memory)
- **Affected Files:** `/core/shared/src/main/java/com/example/shared/SystemUtilitiesAndServices.kt`, `/app/src/main/java/com/example/viewmodel/ElyzarethViewModel.kt`
- **Required Correction:** Implement `PRAGMA wal_checkpoint(FULL)`, physical SQLite + media file bundling, signed `BackupSnapshotManifest`, `AES-256-GCM` encryption, staged restore verification, and Constitutional Law #12 signature checking on restore.
- **Dependencies:** `RM-P1-05`, `RM-P1-06`
- **Risk:** Medium
- **Acceptance Criteria:** Full round-trip backup and restore preserves all database rows, evidence chains, and media files; tampered backup bundles are rejected in staging.
- **Required Tests:** `IT-BAK-01`, `UT-REC-01`
- **Evidence Needed:** Robolectric file-system backup/restore round-trip test
- **Human Decision Requirement:** Phase 2 authorization
- **Implementation Authorization Status:** `NOT GRANTED (AWAITING APPROVAL)`

---

### PHASE 3 — Creative Production and Asset Lifecycle (`Deliverable: CREATIVE_PRODUCTION_2_0`)

#### `RM-P2-09` — Unified 18-Field Asset Registry with Real Stream SHA-256 & Lineage Verification
- **Priority:** `P2 — Functional Subsystem Gap`
- **Blueprint Reference:** Section 3 (Stages 09–10), Section 12, `REQ-041`..`REQ-046`, `SYS-CON-07`
- **Current Repository Status:** `DEFECTIVE` / `PROTOTYPE ONLY` (`registerNewAsset()` hashes title strings instead of file bytes)
- **Affected Files:** `/app/src/main/java/com/example/data/model/OsBranchModels.kt`, `/app/src/main/java/com/example/data/repository/OsBranchRepository.kt`, `/app/src/main/java/com/example/ui/production/ProductionBranchScreens.kt`
- **Required Correction:** Upgrade `RegisteredAssetEntity` to the 18-field schema, integrate SAF file selection (`OpenDocument`), compute real byte-stream SHA-256 hashes, verify physical file existence (`BYTE_VERIFIED` vs `MISSING_FILE_BYTES`), and enforce parent-child asset lineage.
- **Dependencies:** `RM-P1-05`
- **Risk:** Medium
- **Acceptance Criteria:** Every registered asset has a real byte-stream SHA-256; modifying or deleting the underlying file transitions status to `HASH_MISMATCH_CORRUPTED` / `MISSING_FILE_BYTES`.
- **Required Tests:** `UT-ASSET-01`, `SEC-07`
- **Evidence Needed:** Robolectric & SAF stream test
- **Human Decision Requirement:** Phase 3 authorization & UI spec confirmation (`ARCH-DEC-08`)
- **Implementation Authorization Status:** `NOT GRANTED (AWAITING APPROVAL)`

---

#### `RM-P2-10` — End-to-End Stage 01–09 Creative Production Module Contracts & App 01/02 Integration
- **Priority:** `P2 — Functional Subsystem Gap`
- **Blueprint Reference:** Section 3 (Stages 01–09), Section 6, `REQ-047`..`REQ-053`, `SYS-CON-01`, `SYS-CON-12`
- **Current Repository Status:** `PARTIALLY IMPLEMENTED` (App 01 & App 02 work; Modules 1, 3, 4, 5, 6 are isolated prototypes)
- **Affected Files:** `CreativeBranchScreens.kt`, `ProductionBranchScreens.kt`, `LyricGeneratorScreen.kt`, `CorpusDashboardScreen.kt`, `ElyzarethViewModel.kt`
- **Required Correction:** Bind all creative modules to the active `ProjectAggregate` (`projectId`), persist App 01 generated lyric drafts directly into Room (`Stage 03 -> 04`), unify Module 03 (`LyricCuratorModuleScreen`) with the authoritative App 02 Corpus Curator (`ARCH-DEC-05`), and implement real dynamic evaluation in `CreativeDnaEngineScreen` (`Stage 06`).
- **Dependencies:** `RM-P1-07`, `RM-P2-09`
- **Risk:** Medium (must preserve 100% of existing App 01 & App 02 workflows).
- **Acceptance Criteria:** A song can progress sequentially from Stage `01` (`IdeaArtifact`) -> `02` (`CreativeBlueprint`) -> `03` (`LyricDraft`) -> `04` (`EvidenceBundle`) -> `05` (`CuratedLyricArtifact`) -> `06` (`DnaConformanceReport`) -> `07` (`MusicStyleSpec`) -> `08` (`VisualGuideSpec`) -> `09` (`ProductionIngestRecord`) -> `10` (`AssetManifest`) with full Room persistence and kernel validation.
- **Required Tests:** `IT-PIPE-01`, `IT-OFFLINE-01`
- **Evidence Needed:** End-to-end integration test across Stages `01..10`
- **Human Decision Requirement:** `ARCH-DEC-05` & `ARCH-DEC-08`
- **Implementation Authorization Status:** `NOT GRANTED (AWAITING APPROVAL)`

---

### PHASE 4 — Forensic Intelligence (`Deliverable: FORENSIC_INTELLIGENCE_3_0`)

#### `RM-P2-11` — Implementation of the 8 Independent Forensic Scanners & 7-Factor Collision Engine
- **Priority:** `P2 — Functional Subsystem Gap`
- **Blueprint Reference:** Section 8, `DEFECT P1-09`, `REQ-054`..`REQ-061`, `SYS-CON-11`
- **Current Repository Status:** `DEFECTIVE` (`OsBranchRepository.runForensicScan()` returns hardcoded `1%` similarity and `VERIFIED CLEAN` strings)
- **Affected Files:** `/app/src/main/java/com/example/data/repository/OsBranchRepository.kt`, `/app/src/main/java/com/example/ui/intelligence/IntelligenceAndVaultScreens.kt`, new package `com.example.domain.forensic`
- **Required Correction:** Implement the 8 independent forensic analysis services (`SCAN-01` N-Gram/Jaccard Lyric Similarity, `SCAN-02` Audio Similarity, `SCAN-03` RIFF/Spectral Watermark Inspection, `SCAN-04` WAV/PCM Audio Integrity & Clipping/T60 Inspector, `SCAN-05` Vocal Formant/Breath Inspector, `SCAN-06` Stem/Master Alignment, `SCAN-07` Provenance DAG Verifier, `SCAN-08` 7-Factor Catalog Collision Detector) using the 5 mandatory classifications (`PASS`, `WARNING`, `FAIL`, `INCONCLUSIVE`, `NOT_TESTED`) and strict separation of measured facts vs. algorithmic estimates.
- **Dependencies:** `RM-P2-09`, `RM-P2-10`
- **Risk:** Medium
- **Acceptance Criteria:** Scanners return real measured results on corpus text and WAV streams, return `NOT_TESTED` when audio assets are absent, never self-approve `G4`, and never auto-delete duplicate candidates.
- **Required Tests:** `UT-FOR-01`, `IT-FOR-G4`, `GOV-T06`
- **Evidence Needed:** JUnit & Robolectric forensic test suite with positive/negative text and WAV fixtures
- **Human Decision Requirement:** Phase 4 authorization
- **Implementation Authorization Status:** `NOT GRANTED (AWAITING APPROVAL)`

---

### PHASE 5 — Complete Governance and Release (`Deliverable: GOVERNANCE_AND_RELEASE_1_0`)

#### `RM-P2-12` — Complete G1–G4 Enforcement, Protocol 3.2.1.0 Binding & Atomic G5 Sealing Ceremony
- **Priority:** `P2 — Functional Subsystem Gap`
- **Blueprint Reference:** Section 4, Section 14 (#8–#15), `DEFECT P0-05`, `DEFECT P0-06`, `SYS-CON-04`, `SYS-CON-15`, `SYS-CON-16`
- **Current Repository Status:** `PARTIALLY IMPLEMENTED` / `DEFECTIVE`
- **Affected Files:** `SovereignKernel.kt`, `ProtocolConsoleScreen.kt`, `SystemAndKernelScreens.kt`, `IntelligenceAndVaultScreens.kt`, `OsBranchRepository.kt`
- **Required Correction:**
  1. Unify `3.2.1.0 Protocol` steps with `G1..G5` gate evaluations (`ARCH-DEC-04`).
  2. Enforce all 11 G5 prerequisites (`G1..G4 == APPROVED`, complete `ReleaseManifest`, live byte-stream SHA-256 verification of all constituent assets, hardware Keystore `HUMAN_CREATOR` signature, atomic Room `@Transaction` sealing, and post-seal verification).
  3. Wire `MasterVaultAndPlayerScreen` to real Android `MediaPlayer` / `Media3` audio playback guarded by `PlaybackAuthorizationGuard` (blocking any unsealed or byte-modified asset).
- **Dependencies:** `RM-P1-06`, `RM-P2-11`
- **Risk:** High (must satisfy every constitutional invariant before unlocking).
- **Acceptance Criteria:** All 8 governance tests (`GOV-T01`..`GOV-T08`) and security tests (`SEC-01`..`SEC-09`) pass.
- **Required Tests:** `GOV-T01`..`GOV-T08`, `IT-REL-01`, `IT-PLAY-01`, `SEC-01`..`SEC-09`
- **Evidence Needed:** Full integration and security test execution report
- **Human Decision Requirement:** Explicit Student-Director authorization to unlock Phase 5 G5 & Master Player implementation (`ARCH-DEC-04`)
- **Implementation Authorization Status:** `NOT GRANTED (AWAITING APPROVAL)`

---

### PHASE 6 — Structural Cleanup, Final Verification & Production Acceptance (`Deliverable: ELYZARETH_OS_FINAL_1_0_CANDIDATE`)

#### `RM-P3-13` — Directory Consolidation, Unused Dependency Cleanup & Full Physical-Device Acceptance
- **Priority:** `P3` / `P4`
- **Blueprint Reference:** Section 14, Section 16, `DEFECT P1-10`
- **Current Repository Status:** `REQUIRES ARCHITECTURAL DECISION`
- **Affected Files:** Duplicate `.gitkeep` directories (`/creative-factory/`, `/devtools/`), `app/build.gradle.kts` (unused Firebase/network dependencies), physical device test verification (`DEV-01`..`DEV-03`)
- **Required Correction:** Execute approved directory cleanup (`ARCH-DEC-01`, `ARCH-DEC-02`, `ARCH-DEC-06`, `ARCH-DEC-07`), run full 17-point Master Blueprint Section 14 regression suite, generate reproducible signed APK/manifest, and verify 1,000+ SAF scan and bounded scroll on the Student-Director's physical Android device.
- **Dependencies:** `RM-P2-12`
- **Risk:** Low
- **Acceptance Criteria:** 100% of unit, database, integration, security, UI, and physical-device acceptance tests pass.
- **Required Tests:** All `UT-*`, `DB-*`, `IT-*`, `SEC-*`, `UI-*`, and `DEV-01`..`DEV-03`
- **Evidence Needed:** Physical-device verification sign-off by Student-Director
- **Human Decision Requirement:** `ARCH-DEC-01`, `ARCH-DEC-02`, `ARCH-DEC-06`, `ARCH-DEC-07` & Final Release Acceptance
- **Implementation Authorization Status:** `NOT GRANTED (AWAITING APPROVAL)`
