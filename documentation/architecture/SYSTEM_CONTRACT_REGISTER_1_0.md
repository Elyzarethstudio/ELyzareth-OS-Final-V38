# ELYZARETH OS — SYSTEM CONTRACT REGISTER 1.0

**Assignment ID:** `EOS-ARCH-RECON-001` (Section 5 & Deliverable #3)  
**Architectural Authority:** ELYZARETH OS — Final Master Blueprint  
**Decision Authority:** Student-Director / Human Creator  
**Scope:** Complete specification of all **20 Cross-Cutting Engineering Contracts** (`SYS-CON-01` through `SYS-CON-20`)

---

## CONTRACT SPECIFICATION MATRIX (`SYS-CON-01` – `SYS-CON-20`)

Every cross-cutting contract below defines its **Purpose**, **Owning Layer & Module**, **Inputs**, **Outputs**, **Constitutional Invariants**, **Failure Handling**, **Security Implications**, and **Acceptance Tests**.

---

### `SYS-CON-01` — Canonical Project Identity Contract
- **Purpose:** Establish a single, stable, versioned project aggregate (`ProjectAggregate`) shared across all 16 OS screens and all 14 pipeline stages.
- **Ownership:** `:core:foundation` (model) · `:core:kernel` (active context manager) · `:app` (`creative_projects` Room table).
- **Inputs:** `projectId: String` (deterministic UUID/ULID or `PROJ-ELYZ-XXX`), `title: String`, `primaryLanguage: String`, `creatorPrincipalId: String`, `activeDnaProfileId: String`.
- **Outputs:** Immutable `ProjectAggregate` containing current `PipelineStage`, active artifact revision pointers per stage (`01`..`14`), and current `G1..G5` gate states.
- **Invariants:**
  1. Every artifact, blueprint, production track, visual spec, forensic report, evidence record, and release package must carry a non-null `projectId` foreign reference.
  2. Only one `ProjectAggregate` is active in the UI context at a time, persisted in Room so app restarts restore the exact active project.
- **Failure Handling:** Requesting an unknown `projectId` returns `KernelError.ProjectNotFound` without mutating active state.
- **Security Implications:** Prevents cross-project artifact substitution (e.g., attaching Project A's G4 forensic clearance to Project B's release package).
- **Acceptance Tests:** `UT-KERN-01`, `IT-UI-01`.

---

### `SYS-CON-02` — Canonical State Transition Contract
- **Purpose:** Ensure `SovereignKernel` is the sole authority capable of mutating `currentPipelineStage` or `GateLifecycleState`.
- **Ownership:** `:core:kernel` (`GovernorStateMachine`).
- **Inputs:** `StageTransitionRequest(projectId, fromStage, toStage, actorPrincipal, evidenceIds, commandId, reason)`.
- **Outputs:** `StageTransitionResult.Success(newStage, ledgerEventId)` or `StageTransitionResult.Rejected(errorCode, missingPrerequisites, ledgerEventId)`.
- **Invariants:**
  1. Forward transitions require `toStage.stageNumber == fromStage.stageNumber + 1`.
  2. All mandatory output artifacts and gate approvals for `fromStage` must be verified in Room before `toStage` is committed.
  3. Both accepted and rejected transition attempts are recorded in `evidence_ledger`.
- **Failure Handling:** Invalid, out-of-sequence, or concurrent conflicting transition requests are rejected atomically with zero mutation to `creative_projects`.
- **Security Implications:** Eliminates `DEFECT P0-03` (arbitrary UI stage jumping).
- **Acceptance Tests:** `UT-KERN-02`, `SEC-01`.

---

### `SYS-CON-03` — Pipeline Stage Contract
- **Purpose:** Define strict entry prerequisites, artifact schemas, validation predicates, and re-entry rules for each of the 14 canonical stages (`STAGE_01`..`STAGE_14`).
- **Ownership:** `:core:foundation` (`PipelineStageSpec`) · `:core:kernel` (`StageValidator`).
- **Inputs:** `projectId: String`, `targetStage: PipelineStage`, `stageArtifactId: String`, `artifactSha256: String`.
- **Outputs:** `StageValidationReport(stage, isSatisfied, verifiedArtifactId, unmetCriteria)`.
- **Invariants:**
  1. A UI navigation event (`navigateToDestination`) never satisfies a `PipelineStageContract`.
  2. Backward re-entry to an earlier stage (`e.g., Stage 12 -> Stage 05`) creates a new artifact revision and invalidates downstream gate approvals (`G1..G5 -> HUMAN_REVIEW_REQUIRED`) while preserving historical approvals in the ledger.
- **Failure Handling:** Missing or corrupted stage output artifacts block advancement to the next stage.
- **Security Implications:** Prevents unvalidated or empty placeholders from advancing a project toward release.
- **Acceptance Tests:** `UT-KERN-03`, `IT-PIPE-01`.

---

### `SYS-CON-04` — G1–G5 Gate Evidence Contract
- **Purpose:** Enforce mandatory evidence packages and prerequisite verifications for each constitutional gate (`G1` through `G5`).
- **Ownership:** `:core:foundation` (`GateEvidenceSpec`) · `:core:kernel` (`PolicyEngine`).
- **Inputs:** `projectId: String`, `gate: GovernanceGateId`, `candidateArtifactIds: List<String>`, `evidenceEventIds: List<String>`.
- **Outputs:** `GateEvaluationPackage(gate, lifecycleState, verifiedEvidenceHashes, missingEvidence, isReadyForHumanReview)`.
- **Invariants:**
  1. `G1` requires verified `IdeaArtifact`, `CreativeBlueprint`, `CuratedLyricArtifact`, and Curator endorsement.
  2. `G2` requires unbroken SHA-256 hash-chain continuity across source prompts/files and derived revisions.
  3. `G3` requires a `DnaConformanceReport` evaluated against the active versioned `CreativeDnaProfile`.
  4. `G4` requires a `ForensicScanReport` with zero `FAIL` verdicts and explicit human auditor review of any `WARNING`, `INCONCLUSIVE`, or `NOT_TESTED` classifications.
  5. `G5` requires `G1..G4 == APPROVED`, a complete `ReleaseManifest`, 100% verified physical asset hashes, and an authenticated `HUMAN_CREATOR` Keystore signature.
- **Failure Handling:** If any prerequisite evidence is missing or fails hash parity, the gate cannot transition to `APPROVED`.
- **Security Implications:** Eliminates `DEFECT P0-05` (rubber-stamp string approvals).
- **Acceptance Tests:** `UT-GATE-01`..`UT-GATE-05`, `SEC-02`.

---

### `SYS-CON-05` — Human Approval and Signature Contract
- **Purpose:** Cryptographically bind every human governance decision (`APPROVE`, `REJECT`, `VETO`, `REQUEST_REVISION`, `SEAL_RELEASE`) to the human actor and exact evidence state.
- **Ownership:** `:core:shared` (`SovereignSecurityManager`) · `:core:kernel` (`DecisionAuthorityService`).
- **Inputs:** `SigningPayload(actorId, role, projectId, targetArtifactId, artifactVersion, gateId, evidenceMerkleRoot, policyVersion, decisionVerb, reason, timestampIso)`.
- **Outputs:** `HumanSignOffRecord(signOffId, canonicalPayloadJson, payloadSha256, publicKeyFingerprint, ecdsaSignatureBase64, verifiedAtMs)`.
- **Invariants:**
  1. Signatures are generated using an Android Keystore hardware-backed private key (`SHA256withECDSA`)—never a concatenated SHA-256 string (`DEFECT P1-07`).
  2. `DecisionAuthorityService` verifies `Signature.verify(publicKey)` over `canonicalPayloadJson` before persisting any gate approval or G5 seal.
  3. AI actors (`AI_GENERATOR`, `AI_ANALYZER`) cannot hold or invoke a human signing key.
- **Failure Handling:** Invalid, expired, replayed, or unverifiable signatures are rejected and logged as `SECURITY_SIGNATURE_REJECTED`.
- **Security Implications:** Guarantees non-repudiation and prevents synthetic or forged approvals.
- **Acceptance Tests:** `UT-SEC-01`, `SEC-02`, `SEC-03`, `SEC-04`, `SEC-05`.

---

### `SYS-CON-06` — Evidence Append-Only Contract
- **Purpose:** Guarantee that historical records in `evidence_ledger` cannot be updated, overwritten, or deleted by application code, repositories, or UI screens.
- **Ownership:** `:core:kernel` (`IntegrityEngine`) · `:app` (`ElyzarethDatabase` & `OsBranchDao`).
- **Inputs:** `NewEvidenceEvent(projectId, artifactId, actorId, actorType, evidenceClass, eventType, parentEventId, canonicalPayloadJson, signatureRef)`.
- **Outputs:** Persisted `EvidenceLedgerEntity` containing `previousEventHash` and `currentEventHash = SHA256(previousEventHash || canonicalPayloadHash || timestampIso)`.
- **Invariants:**
  1. `OsBranchDao.insertEvidenceRecord` uses `@Insert(onConflict = OnConflictStrategy.ABORT)`.
  2. SQLite `BEFORE UPDATE` and `BEFORE DELETE` triggers on `evidence_ledger` raise `RAISE(ABORT, 'CONSTITUTIONAL_VIOLATION: evidence_ledger is append-only')`.
  3. Corrections to historical artifacts append a new `EvidenceLedgerEntity` referencing `parentEventId`.
- **Failure Handling:** Any attempt to mutate or delete an existing ledger row throws `SQLiteConstraintException` and aborts the transaction.
- **Security Implications:** Eliminates `DEFECT P0-04` and enforces Constitutional Law #2 at the SQLite engine level.
- **Acceptance Tests:** `UT-EVID-01`, `DB-05`, `SEC-07`.

---

### `SYS-CON-07` — Asset Identity and Lifecycle Contract
- **Purpose:** Manage all 12 media and document asset types with real byte-level SHA-256 verification and parent-child lineage tracking.
- **Ownership:** `:app` (`AssetRegistryRepository` & `RegisteredAssetEntity`).
- **Inputs:** Physical file stream (`Uri` or internal storage `File`), `projectId`, `assetType`, `sourceOrDerived`, `parentAssetId`.
- **Outputs:** `RegisteredAssetEntity` with verified `checksumSha256` (computed over actual file bytes), `fileSizeBytes`, `mimeType`, `version`, and `integrityStatus`.
- **Invariants:**
  1. A registered asset is valid (`INTEGRITY_VERIFIED`) only when its physical file exists and its current byte stream SHA-256 matches `checksumSha256`.
  2. Importing a modified file never overwrites an original asset; it creates a new version (`version + 1`) linked via `parentAssetId`.
- **Failure Handling:** Missing files transition the asset's `integrityStatus` to `MISSING_SOURCE_BYTES` and block G4/G5 clearance without deleting the database record.
- **Security Implications:** Eliminates `DEFECT P0-06` / `REQ-042` (synthetic string hashing of asset titles).
- **Acceptance Tests:** `UT-ASSET-01`, `IT-ASSET-01`.

---

### `SYS-CON-08` — Database Migration and Preservation Contract
- **Purpose:** Guarantee zero-loss schema upgrades across all Room database versions (`v1 -> v2` and future `v2 -> v3+`).
- **Ownership:** `:app` (`ElyzarethDatabase.kt`).
- **Inputs:** Existing on-disk SQLite database file (`elyzareth_os_database`).
- **Outputs:** Upgraded database passing Room `onValidateSchema` with 100% preservation of all existing tables, rows, columns, and primary keys.
- **Invariants:**
  1. `.fallbackToDestructiveMigration()` is strictly prohibited (`DEFECT P0-01`).
  2. Before opening/migrating the database on version upgrade, a verified byte-for-byte backup clone (`elyzareth_os_database.v<N>.bak`) is created.
  3. Every version increment has an explicit, deterministic `Migration(N, N+1)` class tested against real historical data.
- **Failure Handling:** If a migration throws an exception, SQLite rolls back the transaction (`user_version` remains `N`) and the backup clone `.bak` is available for immediate recovery.
- **Security Implications:** Protects user-authored creative works, corpus curations, and audit trails across app updates.
- **Acceptance Tests:** `DB-A1`, `DB-A2`, `DB-A3`, `DB-A4`.

---

### `SYS-CON-09` — SAF Import and Rescan Contract
- **Purpose:** Reconcile external Storage Access Framework (`SAF`) directories with the Room corpus (`base_titles`, `artifacts`) non-destructively and atomically.
- **Ownership:** `:app` (`CorpusRepository.kt` & `ElyzarethDao.kt`).
- **Inputs:** `treeUri: Uri` selected by the Human Creator.
- **Outputs:** Updated `CorpusAuditReport` computed from live Room rows after atomic reconciliation.
- **Invariants:**
  1. **Two-Phase Execution:** Phase 1 performs all SAF cursor traversal and stream SHA-256 hashing in memory outside any database transaction. Phase 2 executes all database upserts inside a single `@Transaction reconcileSafScanAtomically(...)`.
  2. **Zero Unconditional Wipe:** `clearAllArtifacts()` and `clearAllBaseTitles()` are never called during `scanSafFolder()` (reserved solely for explicit user-confirmed `"Corpus Baseline"` reset).
  3. **Curation State Preservation:** Unchanged files preserve their exact `id`, `status`, `curatedContent`, `auditNotes`, and `gateTimestamp`.
  4. **Sealed Immutability:** If an external file changes for a `RELEASED & FROZEN` artifact, the frozen row remains untouched and a new `CURATION REQUIRED` revision row is inserted (`Test B7`).
- **Failure Handling:** Mid-scan SAF I/O failure aborts in Phase 1 before touching the DB; Phase 2 DB failure rolls back the `@Transaction` completely (`Test B6`).
- **Security Implications:** Eliminates `DEFECT P0-02`.
- **Acceptance Tests:** `DB-B1` through `DB-B7`.

---

### `SYS-CON-10` — Corpus Version and Duplicate Detection Contract
- **Purpose:** Provide deterministic primary-key continuity and multi-factor duplicate/collision classification across corpus versions.
- **Ownership:** `:app` (`CorpusRepository.kt`) · `ForensicScannerEngine`.
- **Inputs:** Discovered or existing `ArtifactEntity` and `BaseTitleEntity` records.
- **Outputs:** Multi-factor collision classification (`EXACT_BYTE_DUPLICATE`, `NORMALIZED_TITLE_VARIANT`, `HIGH_TEXT_SIMILARITY`, `DISTINCT_WORK`).
- **Invariants:**
  1. Existing database rows retain their primary keys (`bt_1`, `art_1001`) across rescans regardless of SAF cursor order (`Test B5`).
  2. Newly discovered titles/artifacts receive deterministic SHA-256-derived IDs (`bt_<12-hex>`, `art_<16-hex>`).
  3. Flagging a duplicate candidate sets `isDuplicate = true` for human review and **never** automatically deletes or overwrites either artifact.
- **Failure Handling:** Ambiguous filename encodings or missing headers flag `containsAmbiguity = true` for human review.
- **Security Implications:** Prevents silent loss of alternate song versions or bilingual takes.
- **Acceptance Tests:** `UT-CORP-01`, `DB-B5`.

---

### `SYS-CON-11` — Forensic Report Contract
- **Purpose:** Standardize analytical outputs from the 8 forensic scanners while separating measured facts from algorithmic estimates and human conclusions.
- **Ownership:** `ForensicScannerEngine` (`Layer 4`).
- **Inputs:** Target project/song assets (`Lyric`, `Audio Master`, `Stems`, `AssetManifest`).
- **Outputs:** Immutable `ForensicScanReport` containing per-scanner `ForensicScannerResult(scannerId, classification, measuredFacts, algorithmicEstimate, confidenceScore, limitationsNote)`.
- **Invariants:**
  1. Allowed classifications are strictly: `PASS`, `WARNING`, `FAIL`, `INCONCLUSIVE`, `NOT_TESTED`.
  2. An unimplemented scanner or missing input media file **must** return `NOT_TESTED` (never a fabricated `PASS`, fixing `DEFECT P1-09`).
  3. A `ForensicScanReport` cannot approve Gate `G4`; only an authenticated human (`HUMAN_AUDITOR` / `HUMAN_CREATOR`) can certify `G4`.
- **Failure Handling:** Corrupted media files produce `FAIL` (Audio Integrity) or `INCONCLUSIVE` with exact byte-offset diagnostic details.
- **Security Implications:** Prevents fake forensic clearance from unlocking release gates.
- **Acceptance Tests:** `UT-FOR-01`..`UT-FOR-08`, `IT-FOR-G4`.

---

### `SYS-CON-12` — AI Adapter Isolation Contract
- **Purpose:** Sandbox all optional AI and external production adapters so they can assist creation without ever acquiring governance or persistence authority.
- **Ownership:** `:core:shared` (`IsolatedExternalAdapter`).
- **Inputs:** Read-only `AdapterRequest(requestId, adapterId, inputPayload, timeoutMs)`.
- **Outputs:** `AdapterResponse.Proposal(outputPayload, modelIdentity, promptSha256, latencyMs)` or `AdapterResponse.Unavailable(reason, offlineFallbackUsed)`.
- **Invariants:**
  1. Adapters have zero reference to `SovereignKernel` state mutators, `DecisionAuthorityService`, `SovereignSecurityManager` signing keys, or Room DAOs.
  2. Every AI/external output is tagged with `actorType = AI` and `modelIdentity` in `EvidenceBundle`.
  3. When offline or disabled, the core OS operates with 100% capability for curation, governance, forensics, and playback.
- **Failure Handling:** Timeouts or provider errors return `AdapterResponse.Unavailable` cleanly without blocking offline workflows.
- **Security Implications:** Enforces Constitutional Laws #1, #8, and #13.
- **Acceptance Tests:** `SEC-05`, `IT-OFFLINE-01`.

---

### `SYS-CON-13` — Transaction and Idempotency Contract
- **Purpose:** Ensure all multi-table mutations execute atomically (`ACID`) and duplicate UI/background commands are idempotent.
- **Ownership:** `:core:kernel` & `:app` repositories.
- **Inputs:** `commandId: String` (unique per user action) + mutation payload.
- **Outputs:** Committed transaction receipt (`transactionId`, `committedAtMs`, `evidenceEventId`) or clean rollback.
- **Invariants:**
  1. Every operation that mutates both domain tables and `evidence_ledger` executes inside a single Room `@Transaction`.
  2. If `evidence_ledger` insertion fails, the entire domain mutation rolls back automatically (Blueprint Section 14: *"Evidence write failure -> Transaction blocked"*).
  3. Re-submitting an already committed `commandId` returns the existing receipt without duplicating rows.
- **Failure Handling:** Uncaught exceptions inside `@Transaction` blocks trigger SQLite rollback and record a diagnostic event.
- **Security Implications:** Prevents state/evidence desynchronization where a state change occurs without an audit trail.
- **Acceptance Tests:** `DB-06`, `UT-TX-01`.

---

### `SYS-CON-14` — Backup and Restore Contract
- **Purpose:** Provide real, verifiable, encrypted snapshots of the Room database, evidence ledger, and registered media files.
- **Ownership:** `:core:shared` (`SnapshotBackupManager`) & `:app` (`BackupArchiveService`).
- **Inputs:** Active SQLite database (`WAL` checkpointed), registered media files directory, and encryption key reference.
- **Outputs:** Physical archive bundle (`.elyzbak`) containing encrypted SQLite snapshot, media blobs, and a signed `BackupSnapshotManifest` with per-file SHA-256 hashes.
- **Invariants:**
  1. Before copying the SQLite file, `PRAGMA wal_checkpoint(FULL)` is executed so all committed transactions are flushed.
  2. Restore validates the manifest signature and every file's SHA-256 checksum in a staging directory before swapping the active database.
  3. Restore **never** fabricates missing approvals, signatures, or source files (Constitutional Law #12).
- **Failure Handling:** Checksum mismatch or interrupted restore aborts in the staging directory, leaving the existing database untouched.
- **Security Implications:** Replaces the mock count-hashing prototype in `SystemUtilitiesAndServices.kt:49–76`.
- **Acceptance Tests:** `IT-BAK-01`, `IT-BAK-02`.

---

### `SYS-CON-15` — Release Manifest and Sealing Contract
- **Purpose:** Define the atomic G5 sealing ceremony that transforms an approved project into an immutable `ReleasePackage`.
- **Ownership:** `:core:kernel` (`ReleaseSealingAuthority`) & `:app` (`ReleaseVaultRepository`).
- **Inputs:** `projectId`, verified `G1..G4` approval IDs, complete `AssetManifest`, and `HUMAN_CREATOR` G5 Keystore signature.
- **Outputs:** Sealed, read-only `ReleasePackage` (`atomicSealStatus = "SEALED_MASTER"`) with `sealedPackageHash` (Merkle root over all asset hashes + G1–G5 sign-off hashes + manifest metadata).
- **Invariants:**
  1. Sealing is rejected if any gate `G1..G4` is not `APPROVED`, any manifest asset is missing/unverified, or the G5 signature fails Keystore verification.
  2. Once sealed, the `ReleasePackage` row and its referenced `ArtifactEntity` / `RegisteredAssetEntity` rows are permanently read-only (`RELEASED & FROZEN`).
  3. Any post-release correction requires creating a new release version (`v2.0`) with `parentReleaseId` pointing to the prior sealed package.
- **Failure Handling:** Partial failure during sealing rolls back the `@Transaction` and leaves the package in `PENDING_G5_LOCK`.
- **Security Implications:** Eliminates `DEFECT P0-06`.
- **Acceptance Tests:** `IT-REL-01`, `SEC-06`, `SEC-07`.

---

### `SYS-CON-16` — Master Player Authorization Contract
- **Purpose:** Ensure only genuinely sealed, untampered G5 master packages can be loaded and played in the Master Player.
- **Ownership:** `:core:kernel` (`PlaybackAuthorizationGuard`) & `:app` (`MasterVaultAndPlayerScreen`).
- **Inputs:** `releaseId: String`.
- **Outputs:** `PlaybackAuthorizationToken(releaseId, verifiedMasterUri, verifiedPackageHash, expiresAtMs)` or `PlaybackDenied(reason)`.
- **Invariants:**
  1. Before starting playback, `PlaybackAuthorizationGuard` verifies: (a) `atomicSealStatus == "SEALED_MASTER"`, (b) `recordOrigin == "PRODUCTION_VERIFIED"` (or explicitly permitted demo fixture mode), (c) G5 human signature is valid, and (d) the physical audio file's current SHA-256 matches the sealed manifest hash.
  2. If a sealed audio file on disk has been modified by even 1 byte, playback is immediately blocked with `INTEGRITY_FAILURE_MODIFIED_SEALED_ASSET`.
- **Failure Handling:** Unsealed or tampered releases cannot be played (Blueprint Section 14: *"Modified sealed asset -> Integrity failure"*, *"Unsealed master playback -> Blocked"*).
- **Security Implications:** Enforces Constitutional Law #9 (Release Exclusivity).
- **Acceptance Tests:** `SEC-07`, `SEC-08`, `IT-PLAY-01`.

---

### `SYS-CON-17` — Error and Recovery Contract
- **Purpose:** Ensure deterministic, fail-closed error handling and crash recovery across all layers.
- **Ownership:** `:core:kernel` (`RecoveryManager`).
- **Inputs:** Uncaught exceptions, interrupted write-ahead journal entries, or startup integrity check results.
- **Outputs:** `RecoveryReport(recoveredTransactions, rolledBackOperations, quarantinedRecords, systemHealthStatus)`.
- **Invariants:**
  1. All governance and security errors **fail closed** (defaulting to locked/unapproved state, never unlocked/approved).
  2. On application startup, `RecoveryManager` verifies the `evidence_ledger` hash chain and checks for any uncommitted transition markers, rolling back incomplete operations and logging a `RECOVERY_AUDIT_RECORD`.
- **Failure Handling:** Database or ledger corruption transitions the affected project into `QUARANTINED_INTEGRITY_HOLD` and prompts the Human Creator to inspect or restore from `.bak`.
- **Security Implications:** Prevents crash states from leaving gates unlocked.
- **Acceptance Tests:** `UT-REC-01`, `DB-B6`.

---

### `SYS-CON-18` — Offline-First Behaviour Contract
- **Purpose:** Guarantee that 100% of core creative governance, corpus curation, forensic verification, evidence hashing, and master playback operate without internet connectivity.
- **Ownership:** System-wide (`:core:foundation`, `:core:kernel`, `:core:shared`, `:app`).
- **Inputs:** Local Room SQLite database, local SAF/app storage, local Android Keystore.
- **Outputs:** Full operational readiness (`OfflineMode = ACTIVE_SOVEREIGN_LOCAL`).
- **Invariants:**
  1. Zero network calls are required for any Stage `01`..`14` transition or `G1`..`G5` gate evaluation.
  2. `AndroidManifest.xml` does not require `android.permission.INTERNET` for core governance operation.
- **Failure Handling:** Airplane mode or zero connectivity causes zero degradation in app startup, SAF scanning, curation, or gate signing.
- **Security Implications:** Enforces Constitutional Law #6 (Offline-First Core Autonomy).
- **Acceptance Tests:** `IT-OFFLINE-01`.

---

### `SYS-CON-19` — Audit and Observability Contract
- **Purpose:** Guarantee truthful, provenance-backed telemetry and UI status reporting across all screens.
- **Ownership:** `:core:kernel` & `:app` (`CommandCentreScreen`, `ProjectAndSystemOverviewScreen`, `SystemServicesScreen`).
- **Inputs:** Live Room table counts, live `evidence_ledger` parity check results, and live `GateVerification` records.
- **Outputs:** `SystemHealthSnapshot` reflecting actual measured repository state.
- **Invariants:**
  1. UI screens must **never** use `.coerceAtLeast(390)` or hardcoded `"100% Structurally Integrated"` / `"VERIFIED CLEAN"` strings to mask empty or unverified state (`DEFECT P1-09`).
  2. Every seed record loaded for demonstration or baseline testing must carry `recordOrigin = "TEST_FIXTURE_DEMO"` and render a visible `[DEMO / TEST FIXTURE]` badge in the UI.
- **Failure Handling:** If an integrity check has not been run in the current session, the UI reports `INTEGRITY: UNMEASURED IN SESSION` rather than `100% VERIFIED`.
- **Security Implications:** Prevents misleading dashboards from inducing human sign-off on unverified works.
- **Acceptance Tests:** `UI-03`, `UI-06`.

---

### `SYS-CON-20` — Schema and Policy Versioning Contract
- **Purpose:** Version all domain schemas, Creative DNA profiles, and constitutional policy rules so historical approvals remain permanently interpretable.
- **Ownership:** `:core:foundation` & `:app` (`ElyzarethDatabase`).
- **Inputs:** `schemaVersion: Int`, `policyVersion: String` (`e.g., "CONST-1.0"`, `"DNA-ELYZ-1.0"`).
- **Outputs:** Version-tagged domain records and gate approvals.
- **Invariants:**
  1. Every domain entity table includes `schemaVersion: Int` and `recordOrigin: String`.
  2. Every `GateVerification` and `DnaConformanceReport` records the exact `policyVersion` active when the evaluation was performed.
  3. Updating a `CreativeDnaProfile` creates a new `policyVersion` (`1.1`) without retroactively invalidating already-sealed `RELEASED & FROZEN` masters that were approved under `1.0`.
- **Failure Handling:** Unknown future schema versions trigger a read-only compatibility warning rather than destructive deletion.
- **Security Implications:** Enforces Constitutional Laws #10 (Version Integrity) and #11 (Deterministic Governance).
- **Acceptance Tests:** `UT-POL-01`, `DB-A2`.
