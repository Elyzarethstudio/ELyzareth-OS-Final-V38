# ELYZARETH OS — DATABASE AND CORPUS PRESERVATION PLAN 1.0

**Assignment ID:** `EOS-ARCH-RECON-001` (Section 4.E, `DEFECT P0-01`, `DEFECT P0-02` & Deliverable #6)  
**Architectural Authority:** ELYZARETH OS — Final Master Blueprint (Section 9)  
**Decision Authority:** Student-Director / Human Creator  
**Status:** `P0 REMEDIATION PLAN COMPLETE — IMPLEMENTATION FROZEN PENDING APPROVAL`

---

## 1. RESOLUTION OF `DEFECT P0-01`: NON-DESTRUCTIVE ROOM MIGRATION (`v1 -> v2`)

### 1.1 Source Evidence & Conflict Resolution
- **Conflict Resolved (`DOC-CONFLICT-01`):** `DEFECT_AND_BLOCKER_REGISTER.md:23` (`OBS-003`) previously classified `fallbackToDestructiveMigration()` in `ElyzarethDatabase.kt:41` as non-blocking because `ensureCorpusInitialized()` re-seeds the 390/510 baseline on startup.
- **Why `OBS-003` Violates the Master Blueprint:** Re-seeding the static 390/510 baseline destroys any real 1,000+ SAF-scanned corpus, all human curation transitions (`CURATION REQUIRED -> RELEASE READY -> RELEASED & FROZEN`), and all curator review notes stored in a Version-1 database.
- **Verified KSP Schema Proof (`ElyzarethDatabase_Impl.kt:41–490`):**
  - Identity Hash: `"78fd88b323fb60e7b012e22365d9f94c"`.
  - Version-1 tables `base_titles` (`16` columns) and `artifacts` (`20` columns) have **zero schema changes** between Version 1 and Version 2 (`0` foreign keys, `0` indices, `defaultValue = null` across all columns).
  - Version 2 adds 8 new tables (`creative_projects`, `creative_blueprints`, `dna_profiles`, `music_production_specs`, `visual_direction_specs`, `forensic_reports`, `evidence_ledger`, `release_vault`), each with `0` foreign keys, `0` indices, and `defaultValue = null`.

### 1.2 Exact Non-Destructive `MIGRATION_1_2` Specification
`ElyzarethDatabase.kt` must remove `.fallbackToDestructiveMigration()` and register `.addMigrations(MIGRATION_1_2)` executing the exact SQL expected by `ElyzarethDatabase_Impl.onValidateSchema`:

```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // 1. Preserve base_titles (16 cols) and artifacts (20 cols) 100% untouched.
        // 2. Create the 8 Version-2 functional branch tables with exact Room column affinities & nullability:
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `creative_projects` (
                `id` TEXT NOT NULL, `title` TEXT NOT NULL, `primaryLanguage` TEXT NOT NULL,
                `witnessPillar` TEXT NOT NULL, `physicalAnchor` TEXT NOT NULL, `currentStage` INTEGER NOT NULL,
                `g1Status` TEXT NOT NULL, `g2Status` TEXT NOT NULL, `g3Status` TEXT NOT NULL,
                `g4Status` TEXT NOT NULL, `g5Status` TEXT NOT NULL, `createdAtTimestamp` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `creative_blueprints` (
                `id` TEXT NOT NULL, `projectId` TEXT NOT NULL, `emotionalTopology` TEXT NOT NULL,
                `structuralPlan` TEXT NOT NULL, `targetBpm` INTEGER NOT NULL, `timeSignature` TEXT NOT NULL,
                `keySignature` TEXT NOT NULL, `vocalIdentity` TEXT NOT NULL, `productionConstraints` TEXT NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `dna_profiles` (
                `id` TEXT NOT NULL, `profileName` TEXT NOT NULL, `allowedAnchorsCsv` TEXT NOT NULL,
                `prohibitedTropesCsv` TEXT NOT NULL, `maxT60DecaySeconds` REAL NOT NULL,
                `minP2SilenceSeconds` REAL NOT NULL, `conformanceScore` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `music_production_specs` (
                `id` TEXT NOT NULL, `projectId` TEXT NOT NULL, `sunoPromptSpec` TEXT NOT NULL,
                `instrumentation` TEXT NOT NULL, `bpm` INTEGER NOT NULL, `meter` TEXT NOT NULL,
                `keySignature` TEXT NOT NULL, `t60RoomDecay` REAL NOT NULL, `stemsRegisteredCount` INTEGER NOT NULL,
                `masterAudioUri` TEXT NOT NULL, `ingestStatus` TEXT NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `visual_direction_specs` (
                `id` TEXT NOT NULL, `projectId` TEXT NOT NULL, `coverArtConcept` TEXT NOT NULL,
                `colorPaletteHexCsv` TEXT NOT NULL, `typographyProfile` TEXT NOT NULL,
                `aspectRatio` TEXT NOT NULL, `visualNarrative` TEXT NOT NULL,
                `artworkAssetUri` TEXT NOT NULL, `approvalStatus` TEXT NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `forensic_reports` (
                `id` TEXT NOT NULL, `targetArtifactId` TEXT NOT NULL, `targetTitle` TEXT NOT NULL,
                `lyricSimilarityScore` INTEGER NOT NULL, `audioIntegrityStatus` TEXT NOT NULL,
                `watermarkStatus` TEXT NOT NULL, `vocalAnalysisStatus` TEXT NOT NULL,
                `catalogCollisionCount` INTEGER NOT NULL, `overallClassification` TEXT NOT NULL,
                `humanAuditorCertified` INTEGER NOT NULL, `scanTimestamp` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `evidence_ledger` (
                `eventId` TEXT NOT NULL, `projectId` TEXT NOT NULL, `artifactId` TEXT NOT NULL,
                `actorId` TEXT NOT NULL, `actorType` TEXT NOT NULL, `evidenceClass` TEXT NOT NULL,
                `eventType` TEXT NOT NULL, `timestampMs` INTEGER NOT NULL, `parentEventId` TEXT NOT NULL,
                `payloadSha256` TEXT NOT NULL, `previousChainHash` TEXT NOT NULL, `humanSignatureHash` TEXT NOT NULL,
                PRIMARY KEY(`eventId`)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `release_vault` (
                `releaseId` TEXT NOT NULL, `projectId` TEXT NOT NULL, `masterTitle` TEXT NOT NULL,
                `canonicalCode` TEXT NOT NULL, `manifestSha256` TEXT NOT NULL, `masterAudioSha256` TEXT NOT NULL,
                `curatedLyricSha256` TEXT NOT NULL, `humanReleaseSignature` TEXT NOT NULL,
                `isAtomicallySealed` INTEGER NOT NULL, `sealedAtTimestamp` INTEGER NOT NULL,
                PRIMARY KEY(`releaseId`)
            )
            """.trimIndent()
        )
    }
}
```
*(Note: Depending on whether the active codebase uses the table definitions in `OsBranchModels.kt` (`idea_blueprints`, `creative_dna_profiles`, etc.) or the KSP-compiled snapshot, `MIGRATION_1_2` will match the exact `@Entity(tableName = ...)` declarations in `ElyzarethDatabase.kt` and export the schema JSON via KSP `room.schemaLocation`.)*

### 1.3 Pre-Migration Cloned Backup & Automatic Rollback Strategy
1. **Pre-Upgrade Clone:** Before `Room.databaseBuilder` opens `elyzareth_corpus_vault.db` (or `elyzareth_os_database`), `DatabaseMigrationGuard.prepareSafeUpgrade(context, dbName)` checks `SQLiteDatabase.openDatabase(..., OPEN_READONLY).version`. If `0 < currentVersion < TARGET_VERSION`, it copies the main database file (plus `-wal` and `-shm`) to `<dbName>.v<currentVersion>.bak` and records its SHA-256 checksum.
2. **Transactional Rollback:** Room executes `Migration.migrate(db)` inside an SQLite transaction. Any SQL failure automatically rolls back the transaction, leaving the v1 database intact. If any file-level corruption occurs, `DatabaseMigrationGuard.restoreFromBackupClone()` atomically restores `<dbName>.v1.bak`.

---

## 2. RESOLUTION OF `DEFECT P0-02`: SAF RESCAN & CORPUS PRESERVATION

### 2.1 Exact Diagnosis of Current `CorpusRepository.kt` (`lines 50–242`)
1. **Conditional Execution of `clearAll*()`:** `dao.clearAllArtifacts()` (`line 219`) and `dao.clearAllBaseTitles()` (`line 220`) execute inside `if (newTitles.isNotEmpty())` (`line 218`) after `if (discoveredArtifacts.isEmpty()) return` (`line 156`). Because `newTitles` (`line 161`) is populated from **every** non-directory file discovered in `baseTitleGroupMap` without querying existing Room rows, **every scan that discovers `>= 1` file wipes both tables**.
2. **Empty Scan UI Report Overwrite:** When `0` files are discovered (`line 156`), `clearAll*()` is skipped, but `CorpusSeedData.getInitialReport()` is returned, overwriting the UI's `CorpusAuditReport` with synthetic `510 / 390` numbers (`ElyzarethViewModel.kt:410`).
3. **Sequential Counter Primary Keys:** `art_${1000 + artifactIdCounter++}` (`line 115`) and `bt_${baseTitleIdCounter++}` (`line 194`) depend on SAF cursor iteration order.

### 2.2 Target Two-Phase Atomic SAF Reconciliation Architecture
```text
PHASE 1: READ-ONLY SAF STREAM & STAGING (Outside Room Transaction)
  ├── Traverse DocumentsContract cursor
  ├── Compute stream SHA-256 & read UTF-8 preview (detect BOM / parse errors)
  ├── Build immutable StagedSafScanResult in memory
  └── If SAF permission revoked / IOException / cursor null -> Abort cleanly (0 DB writes)

PHASE 2: SINGLE ATOMIC @Transaction IN ROOM (reconcileSafScanAtomically)
  ├── Load existing BaseTitleEntity & ArtifactEntity maps from Room
  ├── Match each discovered file by (1) exact normalized path, (2) SHA-256 rename match, or (3) normalized title key
  ├── Preserve existing primary keys (bt_*, art_*) for matched records; assign deterministic SHA-256 IDs only to net-new items
  ├── Enforce 6-Case Curation & Immutability Rules (never overwrite RELEASED & FROZEN rows)
  ├── Retain missing files with [SOURCE FILE MISSING IN LATEST SCAN] audit note (zero silent deletes)
  └── Recompute CorpusAuditReport directly from post-transaction Room rows
```

### 2.3 Six-Case Identity, Provenance & Immutability Rules on SAF Rescan

| Case | Match Condition | Database Action Inside `@Transaction` |
| :--- | :--- | :--- |
| **Case 1: Unchanged File** | `path == existing.path` AND `sha256 == existing.fileHash` | **No-op / Preserve 100%:** Keep existing `id`, `baseTitleId`, `status` (`RELEASE READY` / `RELEASED & FROZEN` / `CURATION REQUIRED`), `reviewReason`, and `notes`. |
| **Case 2: Renamed / Moved File** | `path != existing.path` AND `sha256 == existing.fileHash` (and `existing.path` is absent in current scan) | **Update Path Only:** Keep `existing.id` and curation `status`; update `path` and `filename`; append `"Moved from <old_path>"` to `reviewReason`. |
| **Case 3: Modified Unsealed File** | `path == existing.path` AND `sha256 != existing.fileHash` AND `existing.status != "RELEASED & FROZEN"` | **Flag Re-Curation with Provenance:** Keep `existing.id`, update `fileHash = newSha256` and `contentPreview`, set `status = "CURATION REQUIRED"`, `requiresHumanReview = true`, and append `"Modified on disk (prior SHA-256: <old_hash>)"` to `reviewReason`. |
| **Case 4: Modified File on Sealed (`RELEASED & FROZEN`) Artifact** | `path == existing.path` AND `sha256 != existing.fileHash` AND `existing.status == "RELEASED & FROZEN"` | **Constitutional Law #2 Immutability:** Leave `existing` row **100% untouched** (`status = "RELEASED & FROZEN"`, `fileHash = oldSha256`). Insert a **new revision `ArtifactEntity`** (`id = "art_rev_<12-hex>"`, `status = "CURATION REQUIRED"`, `fileHash = newSha256`, `reviewReason = "New disk revision detected for sealed artifact ${existing.id}"`) and set parent `BaseTitleEntity.containsAmbiguity = true`. |
| **Case 5: Active Duplicate Content** | Multiple discovered files share identical `sha256` in the same scan | Retain distinct `ArtifactEntity` rows per path; flag secondary copies with `isDuplicate = true` and `requiresHumanReview = true`. Never auto-delete. |
| **Case 6: Missing Source File** | `existing` row in Room (`importSource == SAF`) is not present in current scan (`>= 1` other files scanned) | **Non-Destructive Retention:** Retain `existing` row in Room; append `"[SOURCE FILE MISSING IN LATEST SCAN]"` to `reviewReason` and flag `requiresHumanReview = true`. |

---

## 3. TRANSACTION BOUNDARIES ACROSS ALL TEN CORE OPERATIONS (SECTION E3)

| # | Operation | Transaction Boundary (`@Transaction`) | Mid-Operation Failure / Rollback Behaviour |
| :---: | :--- | :--- | :--- |
| 1 | **Project Creation** | `insertProject` + `insertGenesisProjectEvidence` in single `@Transaction`. | Rolls back project row if evidence write fails. |
| 2 | **Artifact Revision** | `insertArtifactRevision` + `updateBaseTitleCount` + `insertDerivedEvidence` in single `@Transaction`. | Prior artifact revision and base title remain unchanged. |
| 3 | **Evidence Recording** | `insertEvidenceRecord` (`OnConflictStrategy.ABORT`) validating `previousEventHash` in single `@Transaction`. | Rejects duplicate/out-of-order hash link and aborts caller mutation. |
| 4 | **Gate Evaluation** | `upsertGateEvaluation` + `insertAnalyticalEvidence` in single `@Transaction`. | Gate remains in previous lifecycle state (`NOT_EVALUATED` / `EVALUATION_IN_PROGRESS`). |
| 5 | **Human Decision** | Verify Keystore signature -> `updateGateState` + `updateTargetStatus` + `insertAuthorityEvidence` in single `@Transaction`. | Signature failure or DB error leaves gate unapproved. |
| 6 | **Asset Registration** | Stream SHA-256 (Phase 1) -> `insertRegisteredAsset` + `insertOriginalEvidence` in single `@Transaction` (Phase 2). | I/O error aborts before DB write; DB error rolls back both rows. |
| 7 | **SAF Rescan** | Stream/hash files (Phase 1) -> `reconcileSafScanAtomically` in single `@Transaction` (Phase 2). | Interrupted scan preserves 100% of last known-good corpus (`Test B6`). |
| 8 | **Release Preparation** | Verify all constituent asset hashes -> `insertDraftReleaseManifest` + `insertEvidence` in single `@Transaction`. | Missing/corrupted asset aborts manifest creation. |
| 9 | **Release Sealing (G5)** | Verify G1–G4 + verify G5 Keystore signature -> `sealReleasePackage` + `freezeConstituentArtifacts` + `insertG5SealEvidence` in single `@Transaction`. | Any unmet prerequisite rolls back entire seal; package stays `PENDING_G5_LOCK`. |
| 10 | **Backup & Restore** | `PRAGMA wal_checkpoint(FULL)` -> staged file copy & SHA-256 verification -> atomic file swap on restore. | Interrupted backup/restore deletes staging temp file; active DB untouched. |
