# ELYZARETH OS — SAF RESCAN PRESERVATION VERIFICATION 1.1 (PRE-IMPLEMENTATION GATE)

**Protocol:** GPT–ARGON Engineering Protocol  
**Directive:** `ELYZARETH OS — DATABASE & SAF REMEDIATION: PRE-IMPLEMENTATION GATE`  
**Operation:** Read-Only Verification and Test-Design Correction  
**Implementation Status:** FROZEN  
**Authority:** Student-Director / Human Creator

---

## 1. Correction of Prior SAF Deletion Claim (`[CORRECTED / SUPERSEDED CLAIM]`)

### 1.1 Prior Claim (Superseded)
In `SAF_RESCAN_PRESERVATION_VERIFICATION_1_0.md`, the summary stated:
> *"Every SAF scan unconditionally wipes 100% of rows in both `artifacts` and `base_titles` before inserting the newly scanned list."*

### 1.2 Corrected Forensic Statement (`[VERIFIED FACT]`)
Direct re-inspection of `/app/src/main/java/com/example/data/repository/CorpusRepository.kt` (`lines 50–242`) establishes that table clearing is **conditional**, governed by three sequential gates in `scanStorageAccessFrameworkDirectory()`:

1. **Gate 1 — Outer Exception Catch (`lines 152–154`):**
   ```kotlin
   } catch (e: Exception) {
       return@withContext CorpusSeedData.getInitialReport()
   }
   ```
   If `DocumentsContract.getTreeDocumentId(treeUri)` or `context.contentResolver.query(...)` throws an exception, the function returns early. `clearAllArtifacts()` and `clearAllBaseTitles()` are **NOT executed**.
2. **Gate 2 — Empty Discovery Guard (`lines 156–158`):**
   ```kotlin
   if (discoveredArtifacts.isEmpty()) {
       return@withContext CorpusSeedData.getInitialReport()
   }
   ```
   If the SAF cursor returns zero non-directory files (`mime != DocumentsContract.Document.MIME_TYPE_DIR`), the function returns early. `clearAllArtifacts()` and `clearAllBaseTitles()` are **NOT executed**.
3. **Gate 3 — Non-Empty `newTitles` Guard (`lines 161, 174–223`):**
   ```kotlin
   val newTitles = mutableListOf<BaseTitleEntity>()
   ...
   for ((titleName, group) in baseTitleGroupMap) {
       ...
       newTitles.add(titleEntity)
   }
   if (newTitles.isNotEmpty()) {
       dao.clearAllArtifacts()
       dao.clearAllBaseTitles()
       dao.insertBaseTitles(newTitles)
       dao.insertArtifacts(updatedArtifacts)
   }
   ```
   - **Critical Naming Fact (`[VERIFIED FACT]`):** The local variable `newTitles` (`line 161`) does **not** mean *"titles that are new compared to the database"*. `scanStorageAccessFrameworkDirectory()` never queries `ElyzarethDao` to check which titles already exist in Room. Instead, `newTitles` contains **every `BaseTitleEntity` constructed from the files discovered in the current scan**.
   - **Exact Execution Condition (`[VERIFIED FACT]`):** Whenever a SAF scan discovers **at least one (`>= 1`) non-directory file** (whether that file is brand new, modified, or 100% unchanged from a previous scan), `discoveredArtifacts` is non-empty, `baseTitleGroupMap` has `>= 1` entry, `newTitles.isNotEmpty()` evaluates to `true`, and `dao.clearAllArtifacts()` + `dao.clearAllBaseTitles()` **ARE EXECUTED**.

---

## 2. Exact Scenario-by-Scenario Behavior Matrix of Current Code (`[VERIFIED FACT]`)

| # | SAF Scan Scenario | Value of `discoveredArtifacts` | Value of `newTitles.isNotEmpty()` | Do `clearAllArtifacts()` & `clearAllBaseTitles()` Execute? | Exact Resulting Database State in Current Code (`CorpusRepository.kt:50–242`) |
|---:|---|---|---|---|---|
| **1** | **Same folder scanned again (unchanged files, `>= 1` file)** | Non-empty (`N` files) | `true` | **YES — EXECUTES** | **All existing `base_titles` and `artifacts` rows are deleted** and recreated from scratch with `status = "PARSED"`/`"FAILED"` and `notes = ""` / `"Flagged for human governor review"`. **All prior human curation statuses (`RELEASE READY`, `RELEASED & FROZEN`, `REJECTED & ARCHIVED`) and Governor notes are wiped.** |
| **2** | **One new file added to existing folder (`N + 1` files)** | Non-empty (`N + 1` files) | `true` | **YES — EXECUTES** | **All `N` previously curated titles and artifacts are deleted** and re-inserted alongside the `1` new file. All prior human curation decisions on the `N` existing files are lost, and sequential IDs (`bt_$titleIdx`, `art_${idx+1000}`) shift if the new file appears before the end of the cursor. |
| **3** | **One existing file modified on disk (`N` files)** | Non-empty (`N` files) | `true` | **YES — EXECUTES** | **All `N` titles and artifacts are deleted and re-inserted.** Even if the modified file belonged to a `RELEASED & FROZEN` master, the frozen record is wiped and replaced with a fresh `PARSED` record carrying the new `fileHash`, erasing its prior frozen provenance. |
| **4** | **One file removed from folder (`N - 1 >= 1` files remain)** | Non-empty (`N - 1` files) | `true` | **YES — EXECUTES** | **All `N` titles and artifacts are deleted**, and only the `N - 1` remaining files are inserted. The removed file's `BaseTitleEntity` and `ArtifactEntity` records are permanently erased from Room, even if the removed file was previously `RELEASED & FROZEN`. |
| **5** | **Files returned in a different SAF cursor order** | Non-empty (`N` files) | `true` | **YES — EXECUTES** | Because `id = "art_${discoveredArtifacts.size + 1000}"` (`line 115`) and `titleId = "bt_$titleIdx"` (`line 175`) depend on cursor iteration order, clearing and re-inserting assigns **different primary keys (`id`) to the same files/titles** whenever cursor order varies. |
| **6** | **No files discovered (empty folder, or folder containing only subdirectories)** | Empty (`0` files) | Not reached (returns at `line 157`) | **NO — DOES NOT EXECUTE** | Database tables `base_titles` and `artifacts` remain untouched in Room, **however** `scanStorageAccessFrameworkDirectory()` returns `CorpusSeedData.getInitialReport()` (`line 157`), which resets `_uiState.value.report` (`ElyzarethViewModel.kt:410`) back to the synthetic `510 / 390` header numbers! |
| **7** | **Only previously known titles discovered** | Non-empty (`N` files) | `true` | **YES — EXECUTES** | Because `CorpusRepository.kt` never queries existing database rows before building `newTitles`, discovering only previously known titles still makes `newTitles.isNotEmpty() == true`, triggering full deletion and re-insertion of both tables. |

---

## 3. Strengthened SAF Transaction & Recovery Architecture

### 3.1 Current Non-Transactional Vulnerability (`[VERIFIED FACT]` + `[INFERRED RISK]`)
In `CorpusRepository.kt` (`lines 219–222`), the four DAO calls execute as **four separate, independent SQLite transactions**:
```kotlin
dao.clearAllArtifacts()              // Transaction 1: DELETE FROM artifacts
dao.clearAllBaseTitles()             // Transaction 2: DELETE FROM base_titles
dao.insertBaseTitles(newTitles)      // Transaction 3: INSERT INTO base_titles ...
dao.insertArtifacts(updatedArtifacts) // Transaction 4: INSERT INTO artifacts ...
```
- **`[INFERRED RISK]`:** If the process is killed, runs out of memory/disk, or throws an exception after Transaction 1 or Transaction 2 completes but before Transaction 3 and Transaction 4 finish, the user's corpus database is left **completely empty or half-written** with no automatic rollback!
- Furthermore, if an individual file stream throws an unhandled `SecurityException` or `Error` mid-loop during SAF reading, partial state must never reach the database.

### 3.2 Mandatory Two-Phase Transactional SAF Reconciliation Design
To guarantee that **an interrupted or partially failed SAF scan always preserves the last known-good corpus**:

1. **Phase 1 — Read-Only SAF Staging Outside the Database Transaction:**
   - All `ContentResolver.query(...)` cursor iteration and `openInputStream(fileUri)` SHA-256 stream reads execute in memory **before** any database mutation is opened.
   - Per-file `IOException` / `SecurityException` errors during `openInputStream` are caught at the individual file boundary (recording a diagnostic `FAILED` / `UNREADABLE` candidate) while fatal directory-level failures (e.g., revoked SAF tree permission or cursor crash) abort Phase 1 immediately and return a structured `SafScanFailureResult` without touching either Room table or overwriting `_uiState.value.report` with fake `510/390` seed numbers.

2. **Phase 2 — Single Atomic `@Transaction` Reconciliation in Room:**
   - Only after Phase 1 completes and produces a validated reconciliation plan does `ElyzarethDao` execute a single `@Transaction` method (`@Transaction suspend fun reconcileSafCorpusTransaction(...)`).
   - Inside that single SQLite transaction (`BEGIN IMMEDIATE` ... `COMMIT` / `ROLLBACK`):
     - If initial synthetic seed records (`390/510`) are present, only those synthetic seed rows are deleted.
     - Existing curated `BaseTitleEntity` and `ArtifactEntity` records are updated or inserted selectively via `@Insert(onConflict = OnConflictStrategy.ABORT)` / `@Update`.
     - If **any** constraint violation, SQLite disk-full error, or runtime exception occurs anywhere inside Phase 2, SQLite automatically executes `ROLLBACK`, leaving 100% of the prior corpus and human curation records intact in their exact pre-scan state.

---

## 4. Deterministic Identity & Provenance Specification

To satisfy the constitutional requirement that **a changed source file must never overwrite an already sealed historical artifact** and that identity is invariant to SAF cursor ordering:

### 4.1 Canonical Normalization Rules
1. **Canonical Path (`canonicalPath`):**
   - Extract relative document path from `DocumentsContract.getDocumentId(fileUri)` relative to `DocumentsContract.getTreeDocumentId(treeUri)`, normalize Unicode to `NFC`, replace backslashes with `/`, trim leading/trailing slashes, and prefix with `"/ELYZARETH_VAULT/SAF_Import/"`.
2. **Canonical Base Title Key (`canonicalTitleKey`):**
   - Normalize Unicode to `NFC`, strip extension and version suffix (`(?i)[_-]v\d+.*$`), collapse underscores/whitespace to single spaces, trim, and lowercase (`Locale.ROOT`) for lookup matching while preserving original casing in `BaseTitleEntity.baseTitle`.
3. **Deterministic Primary Keys (With Legacy ID Continuity):**
   - When matching an existing database record (including records initially created with legacy IDs `bt_1` or `art_1001`), **retain the existing database row's `id`** so no historical foreign/logical reference ever breaks.
   - For newly discovered titles/artifacts not yet in Room:
     - `BaseTitleEntity.id = "bt_" + sha256(canonicalTitleKey).take(16)` (with collision suffix `_2` if two distinct titles ever hash-collide on 64 bits).
     - `ArtifactEntity.id = "art_" + sha256("$canonicalPath::$fileHash").take(16)`.

### 4.2 Exact Rules for All 6 Identity & Provenance Cases

| Case | Detection Condition | Required Reconciliation Action |
|---|---|---|
| **1. Unchanged File** | `canonicalPath` matches existing artifact AND `fileHash` (`SHA-256`) is identical | **Preserve 100% of human curation state:** keep existing `id`, `status`, `requiresHumanReview`, `reviewReason`, and `parseError` on the artifact, and keep `id`, `status`, `notes`, `containsAmbiguity`, and `g1Score`–`g5Score` on `BaseTitleEntity`. Update `lastObservedTimestamp`. |
| **2. Renamed / Moved File (Identical Content)** | No artifact matches `canonicalPath`, but exactly one previously known artifact (whose old path is now absent from the scan) has the identical 64-hex-char `fileHash` | **Preserve identity and curation history:** retain existing `ArtifactEntity.id`, `status`, and curation fields; update `path` and `filename` to the new location; append `"[RENAMED FROM ${oldFilename}]"` to `reviewReason` / `notes`. |
| **3. Changed File Content on a NON-FROZEN Artifact** | `canonicalPath` matches existing artifact whose `status != "RELEASED & FROZEN"` (and parent title `status != "RELEASED & FROZEN"`), AND `fileHash` has changed | Update the artifact with the new `fileHash`, `sizeBytes`, `lastModifiedMs`, and `contentPreview`; transition `status` to `"CURATION REQUIRED"` (`requiresHumanReview = true`); preserve prior `reviewReason`/`notes` history and append `"[CONTENT MODIFIED: ${oldHash.take(15)}... -> ${newHash.take(15)}...; RE-VERIFICATION REQUIRED]"`. |
| **4. Changed File Content on a `RELEASED & FROZEN` Artifact (Constitutional Immutability Rule)** | `canonicalPath` matches an existing artifact where the artifact OR its parent `BaseTitleEntity` has `status == "RELEASED & FROZEN"`, AND `fileHash` has changed | **NEVER overwrite or mutate the frozen historical `ArtifactEntity` row.**<br>1. Keep the original `ArtifactEntity` row (`id = existing.id`, `status = "RELEASED & FROZEN"`, `fileHash = oldHash`) in `artifacts` as an immutable historical record.<br>2. Insert a **new `ArtifactEntity` row** (`id = "art_" + sha256("$canonicalPath::$newHash").take(16)`, `versionLabel = "${existing.versionLabel} (Modified Unverified Revision)"`, `status = "CURATION REQUIRED"`, `requiresHumanReview = true`, `reviewReason = "Modified source detected after G5/Freeze of ${existing.id} (${oldHash.take(15)}...). New review cycle required."`).<br>3. On the parent `BaseTitleEntity`, preserve the frozen attestation in `notes` and append a notice that an unverified post-freeze revision artifact exists requiring human governance review. |
| **5. Identical Hashes Under Different Paths (Co-existing Duplicates)** | Two or more files present in the same scan have different `canonicalPath` values but identical 64-hex-char `fileHash` | Create/retain separate `ArtifactEntity` rows (keyed by their distinct `canonicalPath`), and mark **both** rows with `isDuplicateCandidate = true`, `requiresHumanReview = true`, and `reviewReason = "SHA-256 content collision (${fileHash.take(19)}...) with ${otherFilename}"`. |
| **6. Missing Source Files (Present in Room, Absent in Current Scan)** | An existing non-seed `BaseTitleEntity` or `ArtifactEntity` in Room is not found by `canonicalPath` or `fileHash` in the current scan | **Never delete curated or frozen records.** Retain the row in Room and append `"[SOURCE FILE MISSING IN LATEST SCAN]"` to `reviewReason` / `notes` (per Student-Director Decision Recommendation #7). |
