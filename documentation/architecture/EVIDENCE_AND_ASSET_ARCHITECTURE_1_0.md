# ELYZARETH OS — EVIDENCE AND ASSET ARCHITECTURE 1.0

**Assignment ID:** `EOS-ARCH-RECON-001` (Section 4.D, Section 4.F & Deliverable #5)  
**Architectural Authority:** ELYZARETH OS — Final Master Blueprint (Sections 7 & 12)  
**Decision Authority:** Student-Director / Human Creator  
**Status:** `SPECIFICATION COMPLETE — IMPLEMENTATION FROZEN PENDING APPROVAL`

---

## 1. PERSISTENT APPEND-ONLY EVIDENCE LEDGER ARCHITECTURE (SECTION 4.D)

### 1.1 Architectural Gap in Current Prototype (`DEFECT P0-04` & `REQ-027`..`REQ-031`)
In the current repository:
1. `SovereignKernel._eventLedger` (`SovereignKernel.kt:64–82, 187`) is an in-memory `MutableStateFlow<List<KernelEvent>>` capped at 200 items (`take(200)`) that vanishes on app termination.
2. `EvidenceLedgerEntity` (`OsBranchModels.kt:129–144`) has only 8 fields, uses `OnConflictStrategy.REPLACE` (`OsBranchDao.kt:58`), and is seeded with 2 static demonstration records (`EVID-001`, `EVID-002`).

### 1.2 Target 14-Field Immutable `EvidenceLedgerEntity` Schema
Every consequential event across all 6 layers must be persisted transactionally into the Room `evidence_ledger` table with the following 14 canonical fields:

| # | Column Name | Kotlin / SQLite Type | Purpose & Invariant |
| :---: | :--- | :--- | :--- |
| 1 | `eventId` | `String` (`TEXT NOT NULL PRIMARY KEY`) | Deterministic ULID/sequential ledger ID (`EVID-00000001`). |
| 2 | `sequenceNumber` | `Long` (`INTEGER NOT NULL UNIQUE`) | Strictly monotonic sequence number (`1, 2, 3...`) enforcing total chronology. |
| 3 | `projectId` | `String` (`TEXT NOT NULL`) | Associated canonical project ID (`PROJ-ELYZ-001` or `"GLOBAL_CORPUS"`). |
| 4 | `artifactId` | `String` (`TEXT NOT NULL`) | Target artifact, base title, asset, or gate ID affected by the event. |
| 5 | `actorId` | `String` (`TEXT NOT NULL`) | Authenticated principal identifier (`Student-Director`, `SovereignKernel`, `SAF_Ingest_Engine`). |
| 6 | `actorType` | `String` (`TEXT NOT NULL`) | Strictly one of: `HUMAN`, `SYSTEM`, `AI`. |
| 7 | `evidenceClass` | `String` (`TEXT NOT NULL`) | Strictly one of: `ORIGINAL`, `DERIVED`, `ANALYTICAL`, `AUTHORITY`. |
| 8 | `eventType` | `String` (`TEXT NOT NULL`) | Canonical verb (`CREATED`, `INGESTED`, `REVISED`, `SCANNED`, `APPROVED`, `REJECTED`, `VETOED`, `SEALED`, `CHECKPOINT`). |
| 9 | `timestampIso` | `String` (`TEXT NOT NULL`) | UTC ISO-8601 timestamp (`yyyy-MM-dd'T'HH:mm:ss.SSS'Z'`). |
| 10 | `parentEventId` | `String?` (`TEXT`) | Causal parent event ID (`null` only for the Genesis Ledger event `EVID-00000000`). |
| 11 | `payloadVersion` | `Int` (`INTEGER NOT NULL`) | Schema version of the serialized payload (`1`). |
| 12 | `canonicalPayloadJson` | `String` (`TEXT NOT NULL`) | RFC 8785 (JCS) canonical JSON serialization of the event payload (sorted keys, UTF-8 no whitespace drift). |
| 13 | `canonicalPayloadHash` | `String` (`TEXT NOT NULL`) | `SHA-256(canonicalPayloadJson)`. |
| 14 | `previousEventHash` | `String` (`TEXT NOT NULL`) | `currentEventHash` of row `sequenceNumber - 1` (or Genesis constant for row `1`). |
| 15 | `currentEventHash` | `String` (`TEXT NOT NULL`) | `SHA-256(sequenceNumber || "|" || eventId || "|" || projectId || "|" || artifactId || "|" || actorId || "|" || actorType || "|" || evidenceClass || "|" || eventType || "|" || timestampIso || "|" || (parentEventId ?: "") || "|" || canonicalPayloadHash || "|" || previousEventHash)`. |
| 16 | `signatureReference` | `String?` (`TEXT`) | Base64 Android Keystore `SHA256withECDSA` signature over `currentEventHash` (mandatory whenever `actorType == "HUMAN"` or `evidenceClass == "AUTHORITY"`). |
| 17 | `sourceAssetId` | `String?` (`TEXT`) | Source asset/artifact ID for lineage traversal. |
| 18 | `derivedAssetId` | `String?` (`TEXT`) | Derived asset/artifact ID produced by this event. |

---

### 1.3 Four Mandatory Evidence Classes
1. **`ORIGINAL` Evidence:** Initial creative seed (`IdeaArtifact`), initial SAF-ingested source lyric files (`ArtifactEntity`), raw imported audio masters/stems, and original human prompts.
2. **`DERIVED` Evidence:** Generated lyric drafts (`LyricDraft`), human-edited lyric revisions (`CuratedLyricArtifact`), style/visual specifications, and transformed media assets. Every `DERIVED` event **must** specify a non-null `parentEventId` and `sourceAssetId`.
3. **`ANALYTICAL` Evidence:** Deterministic outputs from the 8 Forensic Intelligence scanners (`ForensicScanReport`), Creative DNA conformance checks (`DnaConformanceReport`), and SAF corpus audit summaries.
4. **`AUTHORITY` Evidence:** Authenticated human decisions (`APPROVE`, `REJECT`, `VETO`, `REQUEST_REVISION`, `REQUEST_EVIDENCE`, `DEFER`, `REOPEN_FOR_REVIEW`, and `G5_SEAL_RELEASE`). Every `AUTHORITY` event **must** carry a non-null, verifiable Keystore `signatureReference`.

---

### 1.4 Database-Level Append-Only Protection & Threat Model
A SHA-256 column in an ordinary SQLite table does not prevent `UPDATE` or `DELETE` calls. Target enforcement combines three layers:
1. **DAO Compile-Time Restriction:** `OsBranchDao` / `EvidenceLedgerDao` exposes only `@Insert(onConflict = OnConflictStrategy.ABORT)` and `@Query("SELECT ...")`. Zero `@Update`, `@Delete`, `@Upsert`, or `DELETE FROM evidence_ledger` methods exist on the DAO.
2. **SQLite Engine Triggers (Registered in `RoomDatabase.Callback.onOpen`):**
   ```sql
   CREATE TRIGGER IF NOT EXISTS trg_evidence_ledger_no_update
   BEFORE UPDATE ON evidence_ledger
   BEGIN
       SELECT RAISE(ABORT, 'CONSTITUTIONAL_VIOLATION_LAW_02: evidence_ledger is append-only; updates are prohibited.');
   END;

   CREATE TRIGGER IF NOT EXISTS trg_evidence_ledger_no_delete
   BEFORE DELETE ON evidence_ledger
   BEGIN
       SELECT RAISE(ABORT, 'CONSTITUTIONAL_VIOLATION_LAW_02: evidence_ledger is append-only; deletes are prohibited.');
   END;
   ```
3. **Hash-Chain & Keystore Checkpoint Verification (`verifyEvidenceChain()`):**
   - Traverses `evidence_ledger` ordered by `sequenceNumber ASC`, recomputing `canonicalPayloadHash` and `currentEventHash` for every row and verifying that `row[i].previousEventHash == row[i-1].currentEventHash`.
   - Every 50 events (and at every gate sign-off), writes a `CHECKPOINT` event signed by the Android Keystore hardware-backed private key.
4. **Honest Local-Device Threat Model:**
   - Against application bugs, rogue UI screens, AI adapters, or accidental repository mutations: **100% prevented** by DAO visibility, SQLite `RAISE(ABORT)` triggers, and `@Transaction` atomicity.
   - Against unprivileged external apps on Android: Protected by Android application sandbox (`0600` file permissions) and hardware-backed Android Keystore (`ECDSA P-256` private key never leaves the TEE/StrongBox).
   - Against a user with root access (`su`) who manually edits the raw SQLite file on disk and recomputes all SHA-256 hashes: Recomputing SHA-256 hashes alone **still fails verification** because the attacker cannot forge the hardware-backed Android Keystore `SHA256withECDSA` signatures on `AUTHORITY` and `CHECKPOINT` rows without unlocking and invoking the Keystore key. (If the device owner intentionally signs falsified payloads using their own biometric/credential, external audit compares the local ledger against previously exported/archived signed manifests).

---

## 2. UNIFIED ASSET REGISTRY & MEDIA LIFECYCLE CONTRACT (SECTION 4.F)

### 2.1 Twelve Canonical Asset Categories (`AssetType`)
1. `ORIGINAL_LYRIC` (Ingested source `.txt` / `.md` / `.json` manuscript)
2. `REVISED_LYRIC` (Curator-refined lyric revision)
3. `CREATIVE_PROMPT` (Lyric or Music Style engineering prompt specification)
4. `AUDIO_MASTER` (Rendered 24-bit/48kHz WAV/FLAC master mix)
5. `ALTERNATE_TAKE` (Alternate vocal or arrangement recording)
6. `AUDIO_STEM` (Isolated track stem: Lead Vocal, Felt Piano, Acoustic Guitar, Room Mic, Bass, Sarangi)
7. `INSTRUMENTAL` (Master mix minus lead vocal)
8. `ARTWORK_MASTER` (3000x3000px album/single cover art)
9. `VIDEO_VISUAL` (Storyboard plate, lyric canvas, or visual reel)
10. `PRODUCTION_METADATA` (BPM, meter, LUFS, T60, arrangement sheet)
11. `FORENSIC_REPORT_BUNDLE` (Serialized JSON/PDF forensic examination report)
12. `RELEASE_PACKAGE_ARCHIVE` (G5-sealed master bundle manifest)

---

### 2.2 Complete 17-Field `RegisteredAssetEntity` Schema
Replaces the 8-field prototype in `OsBranchModels.kt:146–164`:

| # | Field | Type | Description |
| :---: | :--- | :--- | :--- |
| 1 | `assetId` | `String` (PK) | Stable deterministic ID (`AST-<16-hex>`). |
| 2 | `projectId` | `String` | Owning canonical project ID (`PROJ-ELYZ-001`). |
| 3 | `assetCode` | `String` | Human-readable catalog code (`ELYZ-AST-2026-001-v1`). |
| 4 | `assetType` | `String` | One of the 12 canonical `AssetType` values. |
| 5 | `classification` | `String` | `SOURCE` (original capture/import) or `DERIVED` (generated/edited/rendered). |
| 6 | `parentAssetId` | `String?` | Parent `assetId` (`null` for original `SOURCE` assets; mandatory for `DERIVED` assets). |
| 7 | `versionNumber` | `Int` | Monotonic version (`1, 2, 3...`). |
| 8 | `fileName` | `String` | Original file name (`01_LeadVocal_Dry.wav`). |
| 9 | `mimeType` | `String` | Verified MIME type (`audio/wav`, `text/plain`, `image/png`, `application/json`). |
| 10 | `fileSizeBytes` | `Long` | Exact byte length measured from the physical stream (`> 0`). |
| 11 | `durationMs` | `Long?` | Audio/video duration in milliseconds (`null` for text/image assets). |
| 12 | `checksumSha256` | `String` | **Real SHA-256 computed over the physical file byte stream** (fixes `REQ-042`). |
| 13 | `storageLocation` | `String` | Persisted SAF URI (`content://...`) or internal vault path (`files/vault/assets/...`). |
| 14 | `creationSource` | `String` | `HUMAN_AUTHORED`, `EXTERNAL_RECORDING`, `AI_DRAFT_ASSISTED`, `SYSTEM_GENERATED`. |
| 15 | `importSource` | `String` | `SAF_DOCUMENT_TREE`, `SAF_SINGLE_FILE`, `INTERNAL_STUDIO`, `TEST_FIXTURE_DEMO`. |
| 16 | `transformationHistory` | `String` | Structured lineage log (`[v1 Ingested -> v2 Normalized UTF-8]`). |
| 17 | `integrityStatus` | `String` | `BYTE_VERIFIED`, `UNVERIFIED_METADATA_ONLY`, `MISSING_FILE_BYTES`, `HASH_MISMATCH_CORRUPTED`. |
| 18 | `archiveStatus` | `String` | `ACTIVE`, `SEALED_FROZEN`, `ARCHIVED`, `SUPERSEDED`. |

---

### 2.3 Database Record vs. Physical File Bytes & Reconciliation Rules
1. **Strict Distinction (`REQ-043`):** A database row in `asset_registry` is a *claim of registration*; the asset is `BYTE_VERIFIED` **if and only if** `AssetIntegrityVerifier.verifyAssetBytes(asset)` opens `storageLocation`, streams the bytes through `MessageDigest("SHA-256")`, confirms `measuredSize == asset.fileSizeBytes`, and confirms `measuredSha256 == asset.checksumSha256`.
2. **Handling File Changes (`REQ-044`):**
   - **Renamed / Moved File (Same `checksumSha256`, new path):** Updates `fileName` and `storageLocation`, appends a move entry to `transformationHistory`, and records a `DERIVED` provenance event without invalidating the asset's hash.
   - **Modified File Bytes (Same path, different `checksumSha256`):** **Never overwrites the existing `RegisteredAssetEntity` row.** If the asset is `SEALED_FROZEN`, the original row remains locked and a new `RegisteredAssetEntity` (`versionNumber + 1`, `parentAssetId = original.assetId`) is registered.
   - **Missing File (`FileNotFoundException` / revoked SAF URI):** Sets `integrityStatus = "MISSING_FILE_BYTES"`, retains the database row and audit history, and blocks G4/G5 release sealing.
   - **Duplicate File Bytes (Same `checksumSha256` imported twice):** Detects existing `checksumSha256` and prompts the user to either link the existing `assetId` or register as a flagged duplicate copy.

---

### 2.4 Ten Canonical Asset Operations (`REQ-045`)
| Operation | Behavior & Immutability Guarantee |
| :--- | :--- |
| **`1. Import`** | Streams external file via SAF (`OpenDocument`), copies or persists read permission, measures byte size, MIME, duration, and stream SHA-256. |
| **`2. Register`** | Inserts `RegisteredAssetEntity` (`versionNumber = 1`) and `ORIGINAL` evidence record inside a single Room `@Transaction`. |
| **`3. Verify`** | Re-streams physical bytes from `storageLocation` and updates `integrityStatus` (`BYTE_VERIFIED` vs `HASH_MISMATCH_CORRUPTED` / `MISSING_FILE_BYTES`). |
| **`4. Derive`** | Creates a child asset (`classification = "DERIVED"`, `parentAssetId = source.assetId`) and records parent-to-child edge in `evidence_ledger`. |
| **`5. Revise`** | Creates `versionNumber = parent.versionNumber + 1` while marking prior version `archiveStatus = "SUPERSEDED"` (or leaving `SEALED_FROZEN` untouched). |
| **`6. Compare`** | Computes line-by-line text diff (for lyrics/prompts) or metadata/hash/duration comparison (for audio/artwork) between any two versions. |
| **`7. Archive`** | Soft-archives an unused draft asset (`archiveStatus = "ARCHIVED"`) without deleting its database row or ledger history. |
| **`8. Restore`** | Restores an `ARCHIVED` asset to `ACTIVE` after passing `Verify` byte-parity check. |
| **`9. Export`** | Exports the asset and its JSON provenance certificate via Android Share / SAF document creation. |
| **`10. Release`** | Locks all constituent assets of a project (`archiveStatus = "SEALED_FROZEN"`) inside the atomic G5 `ReleaseManifest` sealing transaction. |
