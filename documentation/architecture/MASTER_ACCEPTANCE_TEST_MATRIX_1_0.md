# ELYZARETH OS — MASTER VERIFICATION AND ACCEPTANCE TEST MATRIX 1.0

**Assignment ID:** `EOS-ARCH-RECON-001` (Section 4.N, Master Blueprint Section 14 & Deliverable #10)  
**Architectural Authority:** ELYZARETH OS — Final Master Blueprint  
**Decision Authority:** Student-Director / Human Creator  
**Status:** `COMPLETE VERIFICATION PLAN — IMPLEMENTATION FROZEN PENDING APPROVAL`

---

## 1. HONEST RECONCILIATION OF CURRENT TEST & BUILD EVIDENCE

Per Section 4.N (*"A successful compilation is not proof of functional correctness. A JVM test is not proof of Android runtime behaviour."*), the current repository's verification status is classified across the 6 distinct evidence tiers:

| Evidence Tier | Current Repository Status | Verified Scope & Limitations |
| :--- | :--- | :--- |
| **1. Compiled** | **`VERIFIED` (JVM/Gradle)** | `:core:foundation`, `:core:kernel`, `:core:shared`, and `:app` compile cleanly (`AGP 9.1.1`, `Kotlin 2.2.10`, `KSP`). |
| **2. Unit Tested (Pure JVM)** | **`PARTIAL` (2 tests)** | `ExampleUnitTest` (`2+2`) and pure logic assertions in `SovereignKernelAndOsIntegrationTest` pass. Missing unit tests for sequential stage blocking, Keystore ECDSA verification, hash-chain tampering, and 8 forensic scanners. |
| **3. Robolectric Tested (Simulated Android on JVM)** | **`PARTIAL` (11 tests)** | `CurationLifecycleVerificationTest` (2 tests), `ExampleRobolectricTest` (4 tests), `SovereignKernelAndOsIntegrationTest` (4 Robolectric/Compose tests), and `GreetingScreenshotTest` (1 test) pass on JVM (`sdk = [36]`). Missing `MIGRATION_1_2` (`A1–A4`) and SAF Rescan Preservation (`B1–B7`) tests. |
| **4. Instrumentation Tested (`androidTest`)** | **`NOT EXECUTED` (0 tests)** | No `androidTest` suite exists or has been executed against a hardware TEE/StrongBox Android Keystore or real `DocumentsContract` SAF provider. |
| **5. Emulator Verified** | **`UNVERIFIED IN CONTAINER`** | Container environment has no direct `adb` / emulator attachment; streaming preview installs `app-debug.apk` for human visual inspection. |
| **6. Physical-Device Verified** | **`PENDING HUMAN DEVICE TEST`** | Screenshot provided by Student-Director confirmed App 02 launch with the 390/510 baseline, and identified the bounded scroll & 1,000+ SAF rescan acceptance requirements still awaiting live device sign-off. |

---

## 2. COMPLETE ACCEPTANCE TEST MATRIX across ALL 6 CATEGORIES (`N1` – `N6`)

Every test in the target acceptance suite is mapped below with its **Test ID**, **Category**, **Blueprint / Contract Reference**, **Setup & Input**, **Expected Result**, and **Current Execution Status**.

---

### Category `N1` — Sovereign Kernel, Governance & Domain Unit Tests (`UT-*`)

| Test ID | Test Name | Blueprint / Contract Ref | Setup & Input | Required Result | Current Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **`UT-KERN-01`** | **Skip Pipeline Stages Rejected** | Blueprint Sec 14 #1 · `SYS-CON-02` | Project at `STAGE_01_IDEA_INCEPTION`; call `requestStageTransition(STAGE_05_LYRIC_CURATOR)` or `STAGE_14_MASTER_RELEASE`. | **Rejected** (`StageTransitionResult.Rejected`); `currentPipelineStage` remains `STAGE_01`; rejection logged in ledger. | `NOT IMPLEMENTED` (`DEFECT P0-03` currently allows skip) |
| **`UT-KERN-02`** | **Unauthorized Actor Stage Transition Rejected** | Blueprint Sec 14 #2 · `SYS-CON-02` | Actor `AI_GENERATOR` or `PRODUCTION_ADAPTER` requests stage transition `05 -> 06`. | **Rejected** (`INSUFFICIENT_ACTOR_CAPABILITY`); state unchanged. | `NOT IMPLEMENTED` |
| **`UT-KERN-03`** | **Concurrent Transition Conflict Serialization** | Blueprint Sec 14 #7 · `SYS-CON-13` | Launch 2 concurrent coroutines attempting conflicting transitions (`02 -> 03` and `02 -> 01`) on the same project. | **Safely serialized or rejected** via `GovernorMutex`; exactly 1 valid transition commits and 1 rejects cleanly. | `NOT IMPLEMENTED` |
| **`UT-GATE-01`** | **Gate Evaluation Out of Sequence Rejected** | Blueprint Sec 14 #8 · `SYS-CON-04` | Gates `G1..G5` at `NOT_EVALUATED`; attempt to evaluate or approve `G3` or `G5` before `G1` & `G2`. | **Rejected** (`GATE_SEQUENCE_VIOLATION`). | `PARTIAL` (Only `G5` checks `G1..G4`; `G1..G4` order not enforced) |
| **`UT-GATE-02`** | **Gate Sign-Off Without Valid Evaluation Package Rejected** | Blueprint Sec 14 #9 · `SYS-CON-04` | Gate `G1` is `NOT_EVALUATED` (no `CreativeBlueprint` or `CuratedLyricArtifact` linked); human submits `APPROVE`. | **Rejected** (`MISSING_PREREQUISITE_EVIDENCE`); gate cannot transition directly from `NOT_EVALUATED` to `APPROVED`. | `NOT IMPLEMENTED` (`DEFECT P0-05`) |
| **`UT-POL-01`** | **Constitutional Laws vs. Versioned Creative DNA Evaluation** | Blueprint Sec 15 · `DEFECT P1-08` | Verify all 14 Constitutional Laws exist in `ConstitutionalLaws.ALL_LAWS`, and evaluate a lyric stanza against `DNA-ELYZARETH-WITNESS-v1.0` vs. a custom `CreativeDnaProfile`. | 14 laws present; DNA engine produces a reasoned `DnaConformanceReport` without mutating the input lyric. | `PARTIAL` (6 laws tested in `SovereignKernelAndOsIntegrationTest`) |
| **`UT-EVID-01`** | **Canonical Payload Serialization & Hash-Chain Continuity** | Blueprint Sec 7 · `SYS-CON-06` | Append 10 sequential events to `IntegrityEngine`; then modify 1 character in event #4's payload and run `verifyEvidenceChain()`. | **Tampering detected** at event #4 (`HASH_CHAIN_PARITY_BREACH` at sequence `4..10`). | `NOT IMPLEMENTED` |
| **`UT-ASSET-01`** | **Real Byte-Stream SHA-256 vs. Missing File Detection** | Blueprint Sec 12 · `SYS-CON-07` | Register a temp file with known bytes (`SHA-256 = H1`), verify `BYTE_VERIFIED`, then delete/modify the temp file and re-run `verifyAssetBytes()`. | Detects `MISSING_FILE_BYTES` / `HASH_MISMATCH_CORRUPTED` immediately. | `NOT IMPLEMENTED` (`REQ-042`) |
| **`UT-FOR-01`** | **8 Forensic Scanners & `NOT_TESTED` Honesty** | Blueprint Sec 8 · `SYS-CON-11` | Run `ForensicScannerEngine` on (a) lyric-only project (no audio file), and (b) project with valid WAV + high-overlap lyric. | (a) Lyric scanners return `PASS`/`WARNING`, audio scanners return `NOT_TESTED` (never fabricated `PASS`); (b) high overlap returns `WARNING`/`FAIL`. | `NOT IMPLEMENTED` (`DEFECT P1-09`) |
| **`UT-REC-01`** | **Interrupted Transaction Recovery Manager** | Blueprint Sec 5 · `SYS-CON-17` | Simulate uncommitted transition journal entry on startup and invoke `RecoveryManager.recoverOnBoot()`. | Rolls back incomplete state, logs `RECOVERY_AUDIT_RECORD`, and never fabricates approvals. | `NOT IMPLEMENTED` |

---

### Category `N2` — Database Migration & SAF Corpus Preservation Tests (`DB-A1..A4`, `DB-B1..B7`, `DB-05..06`)

*(Full specifications established in `DATABASE_REMEDIATION_TEST_PLAN_1_0.md` v1.1 and `DATABASE_AND_CORPUS_PRESERVATION_PLAN_1_0.md`)*

| Test ID | Test Name | Blueprint / Contract Ref | Target Verification | Current Status |
| :--- | :--- | :--- | :--- | :--- |
| **`DB-A1`** | **v1 → v2 Seeded Baseline Upgrade (`390 / 510`)** | `DEFECT P0-01` · `SYS-CON-08` | Upgrade v1 DB (`390` titles, `510` artifacts) via `MIGRATION_1_2`; verify `0` rows lost and 8 new v2 tables created. | `SPECIFIED (Awaiting Code Auth)` |
| **`DB-A2`** | **v1 → v2 Curated & Frozen State Preservation** | `DEFECT P0-01` · `SYS-CON-08` | Verify custom `RELEASE READY`, `RELEASED & FROZEN`, `notes`, and `reviewReason` survive `v1 -> v2` migration byte-for-byte. | `SPECIFIED (Awaiting Code Auth)` |
| **`DB-A3`** | **v1 → v2 Large SAF Corpus (`1,050` Artifacts)** | `DEFECT P0-01` · `SYS-CON-08` | Verify `800` titles / `1,050` SAF-scanned artifacts upgrade `v1 -> v2` without triggering `510` baseline re-seed. | `SPECIFIED (Awaiting Code Auth)` |
| **`DB-A4`** | **Migration Failure & `.v1.bak` Rollback Recovery** | `DEFECT P0-01` · `SYS-CON-08` | Inject faulty SQL mid-migration on cloned v1 DB; verify SQLite transaction rollback and `.v1.bak` recovery with `0` data loss. | `SPECIFIED (Awaiting Code Auth)` |
| **`DB-B1`** | **Unchanged SAF Rescan Idempotence** | `DEFECT P0-02` · `SYS-CON-09` | Rescan identical `50` files (`10` curated/frozen); verify `0` rows deleted and all curation states preserved. | `SPECIFIED (Awaiting Code Auth)` |
| **`DB-B2`** | **Incremental SAF Addition (`+1` File)** | `DEFECT P0-02` · `SYS-CON-09` | Add `1` new file to `50` existing files; verify existing `50` IDs/states untouched and `1` new row added as `CURATION REQUIRED`. | `SPECIFIED (Awaiting Code Auth)` |
| **`DB-B3`** | **Modified Unsealed File Re-Curation & Provenance** | `DEFECT P0-02` · `SYS-CON-09` | Modify file bytes of a `RELEASE READY` artifact; verify status transitions to `CURATION REQUIRED` while preserving prior notes & logging old hash. | `SPECIFIED (Awaiting Code Auth)` |
| **`DB-B4`** | **Removed Source File Non-Destructive Retention** | `DEFECT P0-02` · `SYS-CON-09` | Remove 1 file from SAF folder; verify Room retains the record with `[SOURCE FILE MISSING IN LATEST SCAN]` note. | `SPECIFIED (Awaiting Code Auth)` |
| **`DB-B5`** | **Cursor Reordering & Empty Scan Resilience** | `DEFECT P0-02` · `SYS-CON-10` | Reverse SAF cursor order (`IDs remain stable`); scan empty folder (`0 rows deleted`, live DB report retained). | `SPECIFIED (Awaiting Code Auth)` |
| **`DB-B6`** | **Interrupted / Failed SAF Scan Atomic Rollback** | `DEFECT P0-02` · `SYS-CON-09` | Throw simulated `IOException` in Phase 1 and `SQLiteException` in Phase 2 `@Transaction`; verify 100% rollback to last known-good corpus. | `SPECIFIED (Awaiting Code Auth)` |
| **`DB-B7`** | **Modified File on `RELEASED & FROZEN` Artifact** | Blueprint Law #2 · `SYS-CON-09` | Modify external file of a `RELEASED & FROZEN` artifact; verify sealed row remains 100% untouched and a new `CURATION REQUIRED` revision row is inserted. | `SPECIFIED (Awaiting Code Auth)` |
| **`DB-05`** | **Evidence Ledger SQLite Append-Only Trigger Enforcement** | Blueprint Sec 14 #5 · `SYS-CON-06` | Execute raw `UPDATE evidence_ledger SET ...` and `DELETE FROM evidence_ledger WHERE ...` against Room DB. | Both throw `SQLiteConstraintException` (`RAISE(ABORT)`); row remains untouched. |
| **`DB-06`** | **Evidence Write Failure Blocks Domain Transaction** | Blueprint Sec 14 #6 · `SYS-CON-13` | Force `insertEvidenceRecord` failure inside a gate approval or asset registration `@Transaction`. | Entire domain mutation rolls back; gate/asset state remains unchanged. |

---

### Category `N3` — Cross-Layer Integration Tests (`IT-*`)

| Test ID | Test Name | Layers Tested | Expected Result | Current Status |
| :--- | :--- | :--- | :--- | :--- |
| **`IT-PIPE-01`** | **Creative Module -> Kernel -> Persistent Evidence Ledger** | Layer 3 -> Layer 1 -> Layer 2 | Creating a blueprint (`Stage 02`) and curating a lyric (`Stage 05`) records hash-chained events in `evidence_ledger` and advances stage only when prerequisites pass. | `PARTIAL` (In-memory event tested; persistent hash chain pending) |
| **`IT-FOR-G4`** | **Forensic Report -> G4 Human Certification** | Layer 4 -> Layer 5 -> Layer 1 | Running `ForensicScannerEngine` writes `ForensicScanReport` and moves `G4` to `HUMAN_REVIEW_REQUIRED` (never `APPROVED`); human signed decision transitions `G4 -> APPROVED`. | `NOT IMPLEMENTED` |
| **`IT-REL-01`** | **G1–G4 + Complete Manifest + Human Signature -> G5 Seal** | Layer 5 -> Layer 1 -> Layer 6 | `sealMasterRelease()` succeeds only when `G1..G4 == APPROVED`, all manifest assets pass byte-hash verification, and `HUMAN_CREATOR` signature verifies. | `NOT IMPLEMENTED` (`DEFECT P0-06`) |
| **`IT-PLAY-01`** | **Sealed Release Package -> Master Player Authorization** | Layer 6 -> Layer 1 | `PlaybackAuthorizationGuard` grants playback token for verified sealed release and blocks unsealed or demo fixture releases. | `NOT IMPLEMENTED` |
| **`IT-BAK-01`** | **Full `.elyzbak` Encrypted Backup & Restore Round-Trip** | `:core:shared` + `:app` Room & Assets | Creates encrypted `.elyzbak` archive, wipes active DB, restores from `.elyzbak`, and verifies 100% row, hash-chain, signature, and media file parity. | `NOT IMPLEMENTED` |
| **`IT-OFFLINE-01`** | **100% Offline Core Governance & Curation Execution** | All Layers | With all external adapters disabled/unavailable, complete full curation, forensic scan, and G1–G5 governance cycle locally. | `IMPLEMENTED AND VERIFIED` (for baseline App 01/02) |

---

### Category `N4` — Constitutional Security & Adversarial Tests (`SEC-*`)

| Test ID | Adversarial Scenario | Blueprint Section 14 Ref | Required Result |
| :--- | :--- | :--- | :--- |
| **`SEC-01`** | **Unauthorized Stage Transition / Direct DAO Bypass Attempt** | Sec 14 #2 | Blocked by kernel capability check & internal DAO encapsulation. |
| **`SEC-02`** | **Forged / Hash-Only Gate Approval Signature** | Sec 14 #13 | `SovereignSignatureEngine.verify()` returns `false`; transaction aborted; `SECURITY_SIGNATURE_REJECTED` logged. |
| **`SEC-03`** | **Replayed Approval Signature from Prior Revision or Different Gate** | Sec 4.I / Sec 4.N4 | Rejected because signed 7-tuple envelope binds `targetArtifactId`, `targetArtifactVersion`, `targetArtifactSha256`, and `gateId`. |
| **`SEC-04`** | **AI-Generated Approval Attempt (`actorType = AI`)** | Sec 14 #3 · Law #8 | Rejected immediately with `CONSTITUTIONAL_VIOLATION_LAW_08`. |
| **`SEC-05`** | **Human Veto Override by AI or Automated Retry** | Sec 14 #4 · Law #7 | `VETOED` gate rejects all automated transitions; remains locked until human `REOPEN_FOR_REVIEW` on a new revision. |
| **`SEC-06`** | **G5 Sealing with Failed G1–G4 or Incomplete Manifest** | Sec 14 #10, #11, #12 | Blocked atomically; `release_vault` status remains `PENDING_G5_LOCK`. |
| **`SEC-07`** | **Modified Sealed Asset Byte Tampering (`1-byte flip`)** | Sec 14 #14 · `SYS-CON-16` | Post-seal verification and Master Player pre-playback check detect SHA-256 mismatch (`INTEGRITY_FAILURE_MODIFIED_SEALED_ASSET`) and block playback. |
| **`SEC-08`** | **Unsealed Master Playback Attempt** | Sec 14 #15 · Law #9 | `PlaybackAuthorizationGuard` blocks playback for any release where `atomicSealStatus != "SEALED_MASTER"` or `G5 != APPROVED`. |
| **`SEC-09`** | **Signing-Key Rotation / Historical Verification Continuity** | Sec 4.I / Sec 4.N4 | Rotate Keystore key `V1 -> V2`; verify historical `V1` approvals still verify against pinned certificate while new approvals verify under `V2`. |

---

### Category `N5` & `N6` — Compose UI, Instrumentation & Physical-Device Verification (`UI-*`, `DEV-*`)

| Test ID | Environment | Verification Target | Acceptance Criteria |
| :--- | :--- | :--- | :--- |
| **`UI-01`** | Robolectric Compose (`createComposeRule`) | **16-Destination Navigation & BackHandler** | Every drawer item, bottom bar tab, and Command Centre grid tile navigates cleanly and `BackHandler` returns to previous screen / Command Centre (**Currently Passing** in `SovereignKernelAndOsIntegrationTest`). |
| **`UI-02`** | Robolectric Compose + Physical Device | **App 02 Corpus Bounded Vertical Scroll (`1,050+` items)** | `base_titles_list` scrolls from item `#1` to the final record (`#1,050+`) with the final item completely visible above the fixed `bottom_nav_bar` (**Robolectric Passing** in `CurationLifecycleVerificationTest`; **Physical APK test required**). |
| **`UI-03`** | Robolectric Compose | **Truthful Gate & Demo Fixture Badges** | Seeded OS branch rows visibly display `[TEST FIXTURE / DEMO]`; unverified gates display `NOT EVALUATED` (never pre-verified green badges). |
| **`UI-04`** | Robolectric Compose | **Read-Only Historical & Sealed Record Inspection** | Opening a `RELEASED & FROZEN` artifact or `EvidenceVaultScreen` renders zero edit/delete buttons. |
| **`DEV-01`** | Physical Android Device | **Real 1,000+ File SAF Ingestion & Rescan Persistence** | Select user's real 1,000+ lyric folder via SAF -> verify all files ingested -> curate 5 items -> force-stop & relaunch app -> rescan same SAF folder -> verify all 5 curated items retain their exact status and notes, and scroll reaches the final item above the bottom navigation bar. |
| **`DEV-02`** | Physical Android Device | **Hardware-Backed Android Keystore Signing & Process Death** | Sign G1 approval on device -> kill process -> relaunch -> verify signature and gate state persist in Room and pass `SHA256withECDSA` verification. |
| **`DEV-03`** | Physical Android Device | **Large Audio Stem SAF Import & Backup/Restore** | Import WAV master/stems via SAF, verify stream SHA-256, create `.elyzbak` backup, restore, and verify byte parity in Airplane Mode. |
