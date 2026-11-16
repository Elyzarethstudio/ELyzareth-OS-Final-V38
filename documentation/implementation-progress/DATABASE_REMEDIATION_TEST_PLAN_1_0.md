# ELYZARETH OS — DATABASE REMEDIATION TEST PLAN 1.1 (PRE-IMPLEMENTATION GATE)

**Protocol:** GPT–ARGON Engineering Protocol  
**Directive:** `ELYZARETH OS — DATABASE & SAF REMEDIATION: PRE-IMPLEMENTATION GATE`  
**Operation:** Read-Only Verification and Test-Design Correction  
**Implementation Status:** FROZEN (11 Test Specifications Prepared; Execution Pending Approval)  
**Authority:** Student-Director / Human Creator

---

## 1. Overview of the 11 Mandatory Acceptance Tests

This test plan incorporates the original 8 verification tests (`A1–A3`, `B1–B5`) plus the **3 mandatory additional acceptance conditions** (`A4`, `B6`, `B7`) required by the Pre-Implementation Gate:

| Test ID | Suite | Test Name | Primary Requirement Verified |
|---|---|---|---|
| **A1** | Suite A (Room Migration) | `v1_to_v2_migration_preserves_all_base_titles_and_artifacts_columns_losslessly` | Byte-for-byte preservation of all 16 `BaseTitleEntity` & 20 `ArtifactEntity` columns across `v1 -> v2`. |
| **A2** | Suite A (Room Migration) | `v1_to_v2_migration_with_1050_curated_corpus_records_prevents_seed_overwrite` | 1,050 SAF-scanned records + `RELEASED & FROZEN` states survive `v1 -> v2` + `ensureCorpusInitialized()`. |
| **A3** | Suite A (Room Migration) | `destructive_migration_fallback_is_disabled_and_unhandled_version_throws_explicitly` | Confirms `.fallbackToDestructiveMigration()` is removed. |
| **A4 (NEW)** | Suite A (Database Recovery) | `database_migration_failure_and_rollback_recovery_on_cloned_db_preserves_original_vault` | Failed migration on a cloned DB rolls back cleanly and restores from `.bak` without corrupting or replacing the original database. |
| **B1** | Suite B (SAF Rescan) | `initial_saf_scan_replaces_390_510_fallback_seed_cleanly` | First SAF scan removes 390/510 synthetic seed and leaves only real scanned records. |
| **B2** | Suite B (SAF Rescan) | `repeated_saf_scan_of_unchanged_folder_preserves_all_human_curation_decisions` | Rescan of unchanged folder preserves `RELEASE READY`, `RELEASED & FROZEN`, `REJECTED & ARCHIVED`, and Governor notes. |
| **B3** | Suite B (SAF Rescan) | `saf_rescan_with_modified_non_frozen_file_preserves_notes_and_flags_curation_required` | Altered non-frozen file transitions to `CURATION REQUIRED` while preserving historical Governor notes. |
| **B4** | Suite B (SAF Rescan) | `saf_rescan_with_removed_or_reordered_files_retains_curated_records_and_stable_ids` | Missing source files are retained with `[SOURCE FILE MISSING IN LATEST SCAN]` and reordered cursors do not shift IDs. |
| **B5** | Suite B (SAF Rescan) | `saf_scan_detects_duplicate_filenames_renamed_files_and_identical_sha256_collisions` | Detects filename duplicates, exact SHA-256 content collisions across distinct paths, and renamed files. |
| **B6 (NEW)** | Suite B (SAF Partial Failure) | `interrupted_or_partially_failed_saf_scan_rolls_back_atomically_and_preserves_last_known_good_corpus` | Mid-scan failure or transaction exception leaves 100% of prior corpus intact (zero partial deletion). |
| **B7 (NEW)** | Suite B (Frozen Immutability) | `modified_source_file_on_frozen_artifact_preserves_immutable_frozen_record_and_creates_new_reviewable_revision` | Changed source file on a `RELEASED & FROZEN` artifact never overwrites the sealed row; creates a new `CURATION REQUIRED` revision while the frozen row remains intact. |

---

## 2. Detailed Specifications for Every Test (`Setup`, `Input`, `Expected Database State`, `Failure Condition`, `Acceptance Criteria`)

### Test A1 — `v1_to_v2_migration_preserves_all_base_titles_and_artifacts_columns_losslessly`
- **Setup:** Create a standalone v1 SQLite file (`vault_v1_master.db`) with `PRAGMA user_version = 1` and only the v1 `base_titles` and `artifacts` tables. Insert 4 `BaseTitleEntity` rows (`RELEASED & FROZEN`, `RELEASE READY`, `CURATION REQUIRED`, `REJECTED & ARCHIVED`) and 5 `ArtifactEntity` rows with populated `fileHash`, `notes`, `reviewReason`, and `parseError` values. Copy `vault_v1_master.db` to `vault_v1_clone_a1.db`.
- **Input:** Open `vault_v1_clone_a1.db` via Room `ElyzarethDatabase` (`version = 2`) with `.addMigrations(MIGRATION_1_2)` and without `.fallbackToDestructiveMigration()`.
- **Expected Database State:** `PRAGMA user_version == 2`; all 10 tables exist; `base_titles` has `4` rows and `artifacts` has `5` rows matching `vault_v1_master.db` across all 16 and 20 columns respectively.
- **Failure Condition:** `RoomOpenDelegate.onValidateSchema()` throws `IllegalStateException`, or any row/column in `base_titles` or `artifacts` differs from `vault_v1_master.db`.
- **Acceptance Criteria:** `100%` column-by-column equality assertion passes for all 4 titles and 5 artifacts.

### Test A2 — `v1_to_v2_migration_with_1050_curated_corpus_records_prevents_seed_overwrite`
- **Setup:** Create a v1 SQLite database cloned to `vault_v1_clone_a2.db` containing `1,050` SAF-ingested `BaseTitleEntity` rows (including `50` marked `RELEASED & FROZEN`) and `1,200` `ArtifactEntity` rows.
- **Input:** Open `vault_v1_clone_a2.db` at `version = 2` via `MIGRATION_1_2`, then invoke `CorpusRepository.ensureCorpusInitialized()`.
- **Expected Database State:** `dao.getBaseTitlesCount() == 1050`; `dao.getArtifactsCount() == 1200`; all `50` `RELEASED & FROZEN` rows retain their exact `status` and `notes`.
- **Failure Condition:** `base_titles` count drops to `390` (seed overwrite) or `0` (destructive drop).
- **Acceptance Criteria:** `1,050` titles and `1,200` artifacts survive upgrade and initialization intact.

### Test A3 — `destructive_migration_fallback_is_disabled_and_unhandled_version_throws_explicitly`
- **Setup:** Create a cloned SQLite file (`vault_v99_clone_a3.db`) containing `base_titles` and `artifacts` with `PRAGMA user_version = 99` (an unmigrated future/unknown version).
- **Input:** Attempt to open `vault_v99_clone_a3.db` via `ElyzarethDatabase.getDatabase(...)` and query `dao.getBaseTitlesCount()`.
- **Expected Database State:** Room refuses to drop tables, throws `IllegalStateException("A migration from 99 to 2 was required but not found...")`, and the underlying `base_titles` rows inside `vault_v99_clone_a3.db` remain intact when inspected directly via SQLite.
- **Failure Condition:** Database opens silently by dropping `base_titles` and `artifacts`.
- **Acceptance Criteria:** `IllegalStateException` is thrown and raw SQLite inspection confirms `base_titles` row count in `vault_v99_clone_a3.db` is unchanged.

### Test A4 (NEW) — `database_migration_failure_and_rollback_recovery_on_cloned_db_preserves_original_vault`
- **Setup:** Create a populated v1 SQLite database (`vault_v1_master.db`) with `390` titles and `10` human-curated `RELEASED & FROZEN` overrides. Clone it to `vault_v1_clone_a4.db`. Create a pre-migration backup snapshot (`vault_v1_clone_a4.db.pre_migrate_v1.bak`).
- **Input:** Simulate a mid-migration failure (injecting a faulty `Migration(1, 2)` that throws `SQLiteException` after creating 4 of the 8 new tables, or simulating corrupted schema validation).
- **Expected Database State:**
  1. SQLite rolls back the uncommitted `Migration(1, 2)` transaction automatically (`PRAGMA user_version` remains `1`, partial tables are rolled back).
  2. Recovery manager verifies/restores from `vault_v1_clone_a4.db.pre_migrate_v1.bak`.
  3. All `390` titles and `10` `RELEASED & FROZEN` records in `vault_v1_clone_a4.db` match `vault_v1_master.db` byte-for-byte.
- **Failure Condition:** `vault_v1_clone_a4.db` is left at an inconsistent schema version, corrupted, or has any `base_titles` / `artifacts` rows lost.
- **Acceptance Criteria:** Pre-migration state (`PRAGMA user_version == 1` and all `390` titles + `10` frozen overrides) is 100% intact and readable after failure recovery.

---

### Test B1 — `initial_saf_scan_replaces_390_510_fallback_seed_cleanly`
- **Setup:** Initialize Room with the default `390` base titles and `510` artifacts synthetic seed (`CorpusSeedData.generateSeedCorpus()`).
- **Input:** Run `scanStorageAccessFrameworkDirectory()` against a simulated SAF directory containing `1,050` lyric files across `1,000` distinct base titles.
- **Expected Database State:** `0` synthetic seed records remain; `base_titles` contains `1,000` rows and `artifacts` contains `1,050` rows.
- **Failure Condition:** Database contains `1,390` titles (`390 seed + 1,000 scanned`) or retains only the `390` seed titles.
- **Acceptance Criteria:** Exact counts `baseTitles == 1000` and `artifacts == 1050`, with all paths under `/ELYZARETH_VAULT/SAF_Import/`.

### Test B2 — `repeated_saf_scan_of_unchanged_folder_preserves_all_human_curation_decisions`
- **Setup:** Ingest `50` files via SAF scan. Curate Title 1 to `RELEASE READY` (`notes = "Governor approved"`), Title 2 to `RELEASED & FROZEN` (`notes = "Sealed v1"`), Title 3 to `REJECTED & ARCHIVED` (`notes = "Rejected corrupt take"`), and Artifact 4 to `CURATION REQUIRED` (`reviewReason = "Check line 2"`).
- **Input:** Run `scanStorageAccessFrameworkDirectory()` a second time against the identical 50-file SAF directory.
- **Expected Database State:** Total counts remain `50`; Title 1 is still `RELEASE READY`, Title 2 is still `RELEASED & FROZEN`, Title 3 is still `REJECTED & ARCHIVED`, Artifact 4 is still `CURATION REQUIRED`, and all `id`, `notes`, and `reviewReason` values are unchanged.
- **Failure Condition:** Any curated title/artifact reverts to `PARSED`, loses its `notes`/`reviewReason`, or gets duplicated.
- **Acceptance Criteria:** 100% of human curation fields (`status`, `notes`, `containsAmbiguity`, `requiresHumanReview`, `reviewReason`) match pre-rescan values.

### Test B3 — `saf_rescan_with_modified_non_frozen_file_preserves_notes_and_flags_curation_required`
- **Setup:** Ingest `"Salt_the_Steps_v1.txt"` (`fileHash = "sha256_aaaa..."`), and promote its title to `RELEASE READY` (`notes = "Governor verified v1"`).
- **Input:** Modify the file contents of `"Salt_the_Steps_v1.txt"` (`newHash = "sha256_bbbb..."`) and run a SAF rescan.
- **Expected Database State:** The artifact's `fileHash` updates to `"sha256_bbbb..."`, `status` transitions from `RELEASE READY` to `CURATION REQUIRED`, `requiresHumanReview = true`, `containsAmbiguity = true`, and `notes` retains `"Governor verified v1"` plus the modification notice (`[CONTENT MODIFIED: sha256_aaaa... -> sha256_bbbb...]`).
- **Failure Condition:** Modified file remains `RELEASE READY` without human re-review, OR prior `"Governor verified v1"` note is erased.
- **Acceptance Criteria:** Status is `CURATION REQUIRED`, `containsAmbiguity == true`, and `notes` contains both the prior Governor note and the hash-change audit trail.

### Test B4 — `saf_rescan_with_removed_or_reordered_files_retains_curated_records_and_stable_ids`
- **Setup:** Ingest 5 files (`A.txt`..`E.txt`). Curate `C.txt` to `RELEASE READY` and `D.txt` to `RELEASED & FROZEN`. Record their primary keys (`id`).
- **Input:** Remove `D.txt` from the SAF folder, add `00_First.txt`, reverse the cursor return order of the remaining files, and run a SAF rescan.
- **Expected Database State:**
  1. `A.txt`, `B.txt`, `C.txt`, and `E.txt` retain their exact original primary keys (`id`) despite the reversed cursor order and new `00_First.txt` file.
  2. `C.txt` retains `status == "RELEASE READY"`.
  3. `D.txt` (`RELEASED & FROZEN`) is **not** deleted from Room; it remains in `base_titles`/`artifacts` with `status == "RELEASED & FROZEN"` and its notes/reviewReason appended with `"[SOURCE FILE MISSING IN LATEST SCAN]"`.
- **Failure Condition:** `D.txt` is deleted from Room, or `C.txt` gets a shifted ID / loses its curation status.
- **Acceptance Criteria:** `D.txt` survives in Room with the missing-source flag, and all existing IDs remain invariant to cursor order.

### Test B5 — `saf_scan_detects_duplicate_filenames_renamed_files_and_identical_sha256_collisions`
- **Setup & Input:**
  1. Scan Folder Pass 1 containing `"Blue_Thread_v1.txt"` (`sha256_1111...`, curated to `RELEASE READY`).
  2. In Pass 2, rename `"Blue_Thread_v1.txt"` to `"Neela_Dhaaga_Renamed_v1.txt"` (same `sha256_1111...`), and add two distinct files `"SongX.txt"` and `"MirrorCopyOfSongX.txt"` with identical byte contents (`sha256_2222...`).
- **Expected Database State:**
  1. `"Neela_Dhaaga_Renamed_v1.txt"` matches the existing `sha256_1111...` artifact, preserves its `id` and `RELEASE READY` curation history, updates its filename, and logs `"[RENAMED FROM Blue_Thread_v1.txt]"`.
  2. Both `"SongX.txt"` and `"MirrorCopyOfSongX.txt"` are created as distinct artifact rows and both are flagged `isDuplicateCandidate = true`, `requiresHumanReview = true` with `reviewReason` citing the `sha256_2222...` collision.
- **Failure Condition:** Renamed file loses its curation history, or identical-content files under different names are not flagged as duplicate candidates.
- **Acceptance Criteria:** Rename continuity and SHA-256 content-collision flags both pass.

### Test B6 (NEW) — `interrupted_or_partially_failed_saf_scan_rolls_back_atomically_and_preserves_last_known_good_corpus`
- **Setup:** Populate Room with `100` curated SAF titles (including `20` `RELEASED & FROZEN` and `30` `RELEASE READY` titles) representing the **last known-good corpus**.
- **Input:**
  - **Sub-case B6a (Phase 1 SAF I/O Interruption):** Simulate a SAF cursor crash / revoked URI permission after reading 40 of 200 files in the directory.
  - **Sub-case B6b (Phase 2 Database Transaction Interruption):** Simulate a `SQLiteException` thrown mid-reconciliation after 50 titles have been processed inside the reconciliation transaction.
- **Expected Database State:**
  - In both B6a and B6b, `dao.getAllBaseTitles()` and `dao.getAllArtifacts()` contain the exact `100` original curated titles and their artifacts in their exact pre-scan states.
  - `_uiState.value.report` retains the last known-good report (not overwritten with `CorpusSeedData.getInitialReport()`).
- **Failure Condition:** Database is left empty (`0` rows), partially updated (`40` or `50` rows), or reset to the 390/510 seed report.
- **Acceptance Criteria:** 100% atomic rollback to the 100-title last known-good corpus in both failure modes.

### Test B7 (NEW) — `modified_source_file_on_frozen_artifact_preserves_immutable_frozen_record_and_creates_new_reviewable_revision`
- **Setup:** Ingest `"Kagaz_Ki_Naav_v1.txt"` (`id = "art_frozen_01"`, `fileHash = "sha256_original_sealed_hash"`, `contentPreview = "Original sealed lyrics"`). Promote both the artifact and its parent `BaseTitleEntity` (`id = "bt_frozen_01"`) to `status = "RELEASED & FROZEN"`, `notes = "Sealed by Human Governor"`.
- **Input:** Modify the bytes of `"Kagaz_Ki_Naav_v1.txt"` on disk (`newHash = "sha256_modified_unapproved_hash"`, `newContent = "Altered lyrics after seal"`) and execute a SAF rescan.
- **Expected Database State:**
  1. **Original Frozen Artifact Preserved Intact:** Row `id = "art_frozen_01"` still exists in `artifacts` with `status == "RELEASED & FROZEN"`, `fileHash == "sha256_original_sealed_hash"`, and `contentPreview == "Original sealed lyrics"`.
  2. **New Reviewable Revision Created:** A second `ArtifactEntity` row exists under `baseTitleId = "bt_frozen_01"` with `fileHash == "sha256_modified_unapproved_hash"`, `status == "CURATION REQUIRED"`, `requiresHumanReview == true`, and `reviewReason` referencing the sealed artifact `art_frozen_01`.
  3. **Parent Base Title Provenance Preserved:** `bt_frozen_01` retains its original `"Sealed by Human Governor"` attestation in `notes` while surfacing the new unverified revision for human review.
- **Failure Condition:** `art_frozen_01` has its `fileHash`, `contentPreview`, or `status` overwritten by the modified file.
- **Acceptance Criteria:** Original sealed artifact row is 100% immutable (`0` columns altered), and the modified file is isolated as a new `CURATION REQUIRED` revision record.
