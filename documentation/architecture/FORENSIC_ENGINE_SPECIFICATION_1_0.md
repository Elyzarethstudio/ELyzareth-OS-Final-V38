# ELYZARETH OS — FORENSIC INTELLIGENCE ENGINE SPECIFICATION 1.0

**Assignment ID:** `EOS-ARCH-RECON-001` (Section 4.H, `DEFECT P1-09` & Deliverable #8)  
**Architectural Authority:** ELYZARETH OS — Final Master Blueprint (Section 8)  
**Decision Authority:** Student-Director / Human Creator  
**Status:** `SPECIFICATION COMPLETE — IMPLEMENTATION FROZEN PENDING APPROVAL`

---

## 1. ARCHITECTURAL MANDATE & SEPARATION OF DUTIES

The **Forensic Intelligence Engine (`Layer 4`)** is an independent, read-only analytical subsystem executed at **Pipeline Stage `11` (`Forensic Scanner`)**.

### 1.1 Constitutional Prohibitions (`DEFECT P1-09` Remediation)
1. **Zero Self-Approval:** The Forensic Engine **must never** approve Gate `G4` or Gate `G5`. It produces an immutable `ForensicScanReport` (`ANALYTICAL_EVIDENCE`), which transitions `G4` at most to `HUMAN_REVIEW_REQUIRED`.
2. **Zero Fabricated `PASS` Verdicts:** In the current prototype (`OsBranchRepository.kt:164–189, 353–371`), `runForensicScan()` inserts hardcoded `"VERIFIED CLEAN"`, `"1%"` similarity, and `"PCM 24-bit/48kHz Intact • T60 = 0.42s Verified"` strings without inspecting any file. Under this specification, any scanner that is not executed or whose required input media file is unavailable **must** return `NOT_TESTED` (or `INCONCLUSIVE`), never `PASS`.
3. **Strict Evidence Separation:** Every scanner output must explicitly separate:
   - **`measuredFacts`:** Deterministic, reproducible physical measurements (e.g., byte size, SHA-256, UTF-8 BOM presence, WAV header sample rate, exact token overlap count).
   - **`algorithmicEstimates`:** Heuristic or statistical scores (e.g., Jaccard 3-gram similarity percentage, estimated RT60 decay envelope, high-frequency spectral energy ratio).
   - **`inferences`:** Flagged risk indicators (e.g., `"Candidate title collision with bt_14"`).
   - **`humanConclusions`:** Reserved exclusively for the `HUMAN_AUDITOR` / `HUMAN_CREATOR` sign-off notes at Gate `G4`.
4. **Explicit `TEST_FIXTURE` Labelling:** Any pre-seeded sample report in `forensic_reports` must carry `recordOrigin = "TEST_FIXTURE_DEMO"` and render a prominent amber `[TEST FIXTURE — DEMONSTRATION DATA ONLY]` banner in `ForensicLabScreen`, preventing sample rows from being mistaken for real forensic clearance.

---

## 2. FIVE CANONICAL FORENSIC RESULT CLASSIFICATIONS

Every individual scanner (`SCAN-01`..`SCAN-08`) and the aggregate `ForensicScanReport` must evaluate to one of the five Master Blueprint classifications:

| Classification | Meaning | Gate `G4` Consequence |
| :--- | :--- | :--- |
| **`PASS`** | Required examination was actually executed on real target bytes/text and all defined criteria were satisfied. | Eligible for Human Auditor `G4` certification. |
| **`WARNING`** | Examination completed and detected a non-fatal anomaly (e.g., moderate phrase overlap `10%–24%`, candidate title variant) requiring explicit human review. | Requires explicit Human Auditor review note & sign-off to certify `G4`. |
| **`FAIL`** | Defined technical or constitutional criterion was violated (e.g., corrupted audio stream, `0`-byte file, broken provenance hash chain, prohibited trope violation). | **Blocks `G4` approval** until remediated via a new artifact revision. |
| **`INCONCLUSIVE`** | Examination executed but evidence was insufficient to reach a reliable determination (e.g., compressed lossy audio prevents reliable watermark/T60 analysis). | Requires explicit Human Creator / Auditor exception sign-off or higher-quality asset ingest. |
| **`NOT_TESTED`** | Examination was not executed or required input asset (e.g., audio master WAV) has not yet been ingested. | **Blocks `G4` certification for audio release** until required scans are executed (or explicitly scoped as Lyric-Only corpus audit). |

---

## 3. TECHNICAL SPECIFICATION OF THE 8 INDEPENDENT FORENSIC SERVICES

### `SCAN-01` — Lyric Similarity & Phrase Overlap Service
- **Required Input:** Target `CuratedLyricArtifact` text (`UTF-8`) + all active `ArtifactEntity` / `BaseTitleEntity` texts in `ElyzarethDatabase`.
- **Processing Method & Algorithm:**
  1. Unicode NFKC normalization, Devanagari/Latin tokenization, and stop-word-preserving line & shingle extraction.
  2. **Exact Line Overlap:** Hash-set intersection of normalized lines across all corpus titles (`excluding` earlier versions under the same `baseTitleId`).
  3. **Character & Word N-Gram Jaccard / Cosine Similarity:** Computes 3-gram and 5-gram word shingle Jaccard similarity (`J(A, B) = |A ∩ B| / |A ∪ B|`) and Longest Common Substring (`LCS`) against every other base title in the catalog.
  4. **Anti-Trope & Witness Anchor Check:** Runs `CorpusRepository.evaluateCustomLyrics()` to verify concrete physical anchor presence and zero prohibited clichés.
- **Dependencies:** Pure Kotlin (`java.text.Normalizer`, `ElyzarethDao`). Zero external cloud dependencies.
- **Output:** `LyricSimilarityResult(maxSimilarityPercent, topMatchedTitleId, sharedPhrasesList, tropeViolationsList, classification)`.
- **Thresholds & Limitations:**
  - `0%–9%` overlap + `0` trope violations -> `PASS`
  - `10%–24%` overlap -> `WARNING` (Human review of shared idioms/refrains required)
  - `>= 25%` overlap with a different song OR `>= 1` prohibited trope -> `FAIL`
  - *Limitation:* Measures lexical/structural overlap against the local catalog; a similarity score is a mathematical measurement, **not** legal proof of plagiarism.

---

### `SCAN-02` — Audio Similarity & Acoustic Fingerprinting Service
- **Required Input:** Target `AUDIO_MASTER` file (`Uri` / `File`) + registered catalog audio masters in `asset_registry`.
- **Processing Method & Algorithm:**
  1. Decodes PCM mono downmix via Android `MediaExtractor` & `MediaCodec` (or direct RIFF WAV PCM reader for uncompressed `.wav` files).
  2. Computes chromagram / spectral energy band fingerprints across 1-second overlapping windows (Short-Time Fourier Transform / Goertzel band energy ratios).
  3. Computes cross-correlation distance against existing catalog audio fingerprints.
- **Dependencies:** Android `MediaExtractor`, `MediaCodec`, pure Kotlin DSP windowing.
- **Output:** `AudioSimilarityResult(maxAcousticMatchPercent, matchedAssetId, durationDeltaMs, classification)`.
- **Failure / Unavailability States:** If no audio file is attached to the project yet, returns `NOT_TESTED`. If decoding fails, returns `FAIL`.

---

### `SCAN-03` — Watermark Detection Service
- **Required Input:** Target `AUDIO_MASTER` or `AUDIO_STEM` PCM byte stream.
- **Processing Method & Algorithm:**
  1. Inspects RIFF/WAV metadata chunks (`LIST`, `INFO`, `id3 `, `bext`, `iXML`, unknown proprietary chunks) for embedded generator tags or external distributor watermarks.
  2. Performs ultrasonic / high-frequency narrowband spectral energy inspection (`18 kHz – 24 kHz` in `48 kHz` PCM) and phase-discontinuity sweep for detectable periodic watermark beacons.
- **Confidence & Limitations Disclosure:**
  - Can reliably detect explicit metadata chunk markers (`MEASURED_FACT`) and narrowband spectral anomalies (`ALGORITHMIC_ESTIMATE`).
  - **Cannot** guarantee the absence of spread-spectrum or psychoacoustic watermarks without the proprietary decoder key; reports `PASS (No Detectable Metadata/Spectral Beacons)` or `INCONCLUSIVE` when sample rate `< 44.1 kHz`.

---

### `SCAN-04` — Audio Integrity Service
- **Required Input:** Physical audio file (`AUDIO_MASTER` / `AUDIO_STEM`) via SAF or internal storage.
- **Processing Method & Algorithm:**
  1. **Header & Container Validation:** Parses RIFF/WAV header (or `MediaFormat` via `MediaExtractor`) to measure sample rate (`Hz`), bit depth (`16/24/32-bit`), channel count, and exact data chunk byte length vs. physical file size.
  2. **Signal Integrity Pass:** Streams PCM samples to measure:
     - **Sample Peak & Clipping Count:** Consecutive samples at `±1.0 FSR` (`0 dBFS`).
     - **Integrated RMS / Crest Factor:** Verifies dynamic range and absence of brickwall clipping.
     - **Discontinuities / Dropouts:** Detects sudden zero-crossing step discontinuities or unexpected digital silence blocks (`> 500ms` mid-phrase) vs. intentional `1.8s–2.0s` P2 inter-stanza silence gaps.
     - **Estimated Room Decay (`T60`):** Measures energy decay slope across stanza-end tails (`Schroeder backward integration` estimate).
- **Criteria:**
  - `PASS`: Valid container, `0` truncated bytes, `0` hard clipping clusters (`> 3` consecutive full-scale samples), sample rate `>= 44100 Hz`.
  - `WARNING`: Sample rate `< 44100 Hz`, peak `> -0.3 dBFS`, or estimated `T60` outside `0.30s–0.60s` target window.
  - `FAIL`: Truncated/corrupted file header, unreadable PCM stream, `0`-byte file, or severe digital clipping.

---

### `SCAN-05` — Vocal Analysis Service
- **Required Input:** Isolated `AUDIO_STEM` (`LeadVocal`) or `AUDIO_MASTER`.
- **Processing Method & Algorithm:**
  1. Computes pitch contour continuity (`F0` autocorrelation tracker), harmonic-to-noise ratio (`HNR`), and natural breath-pause intervals (`150ms–600ms` low-energy inspirations between lines).
  2. Flags unnatural phase-locked buzz, metallic vocoder artifacts, or absence of natural breath pauses.
- **Strict Constitutional Caveat:**
  - Reports measured acoustic parameters (`F0 range`, `breath gap count`, `spectral flatness`) as `MEASURED_FACT` / `ALGORITHMIC_ESTIMATE`.
  - Explicitly notes in every report: *"Acoustic vocal analysis detects signal artifacts and formant anomalies; it cannot definitively prove or disprove AI authorship."*
  - If no vocal stem or audio file is present, returns `NOT_TESTED`.

---

### `SCAN-06` — Production Integrity Service
- **Required Input:** `MusicProductionEntity` + registered `AUDIO_MASTER` and `AUDIO_STEM` records in `asset_registry`.
- **Processing Method & Algorithm:**
  1. Verifies that every stem listed in the production spec is registered in `asset_registry` with `integrityStatus == "BYTE_VERIFIED"`.
  2. Verifies sample-rate, bit-depth, and duration alignment (`±50ms` tolerance) across all stems belonging to the same session.
- **Output:** `ProductionIntegrityResult(registeredStemCount, verifiedStemCount, durationAligned, sampleRateAligned, classification)`.

---

### `SCAN-07` — Provenance Verification Service
- **Required Input:** Target `projectId`, `CuratedLyricArtifact`, `AssetManifest`, and `evidence_ledger`.
- **Processing Method & Algorithm:**
  1. Traverses the DAG from every candidate release asset back to its root `ORIGINAL` evidence record (`IdeaArtifact` or initial SAF-ingested `ArtifactEntity`).
  2. Re-verifies every intermediate `currentEventHash` and `previousEventHash` in `evidence_ledger`.
  3. Re-verifies live byte-stream SHA-256 parity for all referenced files.
- **Criteria:**
  - `PASS`: Unbroken lineage from original source to current revision with 100% SHA-256 parity.
  - `FAIL`: Any orphan derived asset, missing parent hash, or file byte mismatch.

---

### `SCAN-08` — Multi-Factor Catalog Collision Detection Service
- **Purpose:** Replace simplistic filename-prefix duplicate detection with a deterministic **7-Factor Comparison Contract**.
- **Required Input:** Candidate title/artifact/asset + full corpus (`base_titles`, `artifacts`, `asset_registry`, `release_vault`).
- **Seven Comparison Factors:**
  1. **Factor 1 — Stable Canonical ID (`canonicalId` / `id`):** Exact primary key or catalog code collision (`ELYZ-0001-EN`).
  2. **Factor 2 — Exact File Byte Hash (`SHA-256`):** Identical byte stream (`100%` byte match) across different paths or titles.
  3. **Factor 3 — Normalized Title Key:** Case-folded, diacritic-stripped, punctuation-normalized Latin and Devanagari title comparison (ignoring version suffixes like `_v1`, `(Draft)`, `(Dry Room)`).
  4. **Factor 4 — Lexical Text Similarity (`SCAN-01`):** Word 3-gram Jaccard similarity `>= 50%` between lyrics under different `baseTitleId` groups.
  5. **Factor 5 — Acoustic Fingerprint Match (`SCAN-02`):** Spectral energy correlation `>= 85%` when audio files are present.
  6. **Factor 6 — Duration Proximity:** Audio duration within `±1,000 ms` combined with same BPM/meter.
  7. **Factor 7 — Metadata & Anchor Signature:** Identical `primaryLanguage` + `physicalAnchor` + opening first line.
- **Non-Destructive Collision Handling Rule:**
  - A detected collision produces a structured `CatalogCollisionCandidate(factorMatched, confidenceScore, existingId, candidateId, recommendedAction)` and sets `classification = WARNING` (or `FAIL` if an already-sealed `RELEASED & FROZEN` master shares the same catalog code).
  - **Never automatically deletes, merges, or overwrites either record.** Merging or archiving a duplicate candidate requires an explicit human decision in App 02 Curator or Gate `G4`.
