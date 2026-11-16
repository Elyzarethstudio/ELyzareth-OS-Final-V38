# ELYZARETH OS — TSX & TYPESCRIPT SOURCE MAPPING REGISTER

**Reference:** ELYZARETH_OS_STRUCTURE_1.0 (Section 4 — TSX Source Mapping)  
**Revision:** 2026-11-14  
**Authority:** Student-Director / Human Creator

---

## 1. Consolidation & Preservation Decision

The repository includes `/elyzareth-app02-curator/`, a historical React + TypeScript + Vite prototype of **App 02 — Lyric Corpus Curator**.

Per Directive Section 4:
1. **No TSX file was blindly copied into the Android runtime**, nor was any functional source silently omitted.
2. Every TypeScript/TSX file in `/elyzareth-app02-curator/src/` has been inspected, mapped to its authoritative Kotlin/Jetpack Compose counterpart in `:app` and `:core:*`, verified by automated Robolectric tests, and **retained in `/elyzareth-app02-curator/`** as an archived historical reference snapshot.

---

## 2. Complete TSX / TS Source Mapping Register

| Source ID | Original filename | Original location | Functional purpose | Destination branch | Dependencies | Migration status | Preservation status | Verification |
|---|---|---|---|---|---|---|---|---|
| `TSX-001` | `main.tsx` | `/elyzareth-app02-curator/src/main.tsx` | Root React shell for App 02 Curator: manages dry-run simulation, folder scan, JSON manifest export, and 3.2.1.0 protocol readiness banner | **Application Shell** (`MainActivity.kt`), **Corpus & Archive** (`CorpusDashboardScreen.kt`), **ViewModel** (`ElyzarethViewModel.kt`) | `CorpusRepository`, `ElyzarethDatabase`, `SovereignKernel` | **Integrated** | **Retained** (Archived in `/elyzareth-app02-curator` & integrated into Kotlin `:app`) | Passed in `CurationLifecycleVerificationTest` & `SovereignKernelAndOsIntegrationTest` |
| `TSX-002` | `Header.tsx` | `/elyzareth-app02-curator/src/components/Header.tsx` | Displays OS version header, Read-Only Dry Run & Human Governor status badges, SAF folder selector trigger, and 510-artifact baseline reset button | **Corpus & Archive** (`CorpusDashboardScreen.kt`) & **Application Shell** (`MainActivity.kt` Top Bar) | `ActivityResultContracts.OpenDocumentTree`, `ElyzarethViewModel.scanSafFolder`, `resetToBaseline` | **Integrated** | **Retained** | Verified via `CorpusDashboardScreen` & `ExampleRobolectricTest` |
| `TSX-003` | `ReportSummary.tsx` | `/elyzareth-app02-curator/src/components/ReportSummary.tsx` | Renders First-Pass Discovery & Evidence Report metrics (Files, Base Titles, Parsed vs Failed, Duplicates, Orphans, Human Review count, Language breakdown, G1–G5 lock badge) | **Corpus & Archive** (`CorpusDashboardScreen.kt`) & **Forensic Badges** (`ForensicBadges.kt`) | `CorpusAuditReport`, `CorpusSeedData.getInitialReport()`, `GateLockRow` | **Integrated** | **Retained** | Verified in `ExampleRobolectricTest` (`corpus seed data contains baseline 390 titles and 510 artifacts`) |
| `TSX-004` | `CorpusTable.tsx` | `/elyzareth-app02-curator/src/components/CorpusTable.tsx` | Searchable, filterable, language-isolated table of Base Title groups (Hindi Witness Vault, English, Multilingual) with expandable artifact version rows and status badges | **Corpus & Archive** (`CorpusDashboardScreen.kt` & `CurationWorkflowPipeline.kt`) | `ElyzarethDao.getAllBaseTitles()`, `ElyzarethViewModel.baseTitles`, `LazyColumn` (`base_titles_list`) | **Integrated** | **Retained** | Verified in `CurationLifecycleVerificationTest` (`verify corpus inventory bounds with 1000 plus simulated SAF records`) |
| `TSX-005` | `ArtifactDetailModal.tsx` | `/elyzareth-app02-curator/src/components/ArtifactDetailModal.tsx` | Read-only forensic evidence inspector modal displaying file path, size, SHA-256 hash, G1–G5 lock status, and text preview | **Corpus & Archive** (`CorpusDashboardScreen.kt` & `CurationWorkflowPipeline.kt`) & **Evidence Vault** (`EvidenceVaultScreen`) | `ArtifactEntity`, `ProblemDiagnosticData`, `EvidenceLedgerEntity` | **Integrated** | **Retained** | Verified in `CurationLifecycleVerificationTest` & `SovereignKernelAndOsIntegrationTest` |
| `TS-006` | `types.ts` | `/elyzareth-app02-curator/src/types.ts` | TypeScript interfaces for `ArtifactStatus`, `ArtifactEvidence`, `CorpusArtifact`, `BaseTitleGroup`, and `IngestionReport` | **Data & Persistence** (`CorpusModels.kt`) & **Foundation Layer** (`ConstitutionalFoundation.kt`) | `androidx.room.Entity`, `:core:foundation` | **Integrated** | **Retained** | Verified across all Room DAO & Repository tests |
| `TS-007` | `parser.ts` | `/elyzareth-app02-curator/src/parser.ts` | Non-destructive file list parser, base title normalizer, version label detector, and file hash calculator | **Data & Persistence** (`CorpusRepository.kt`: `scanStorageAccessFrameworkDirectory`, `readEvidenceAndHash`) & **Sovereign Kernel** (`IntegrityEngine`) | `java.security.MessageDigest` (real SHA-256), Android `DocumentsContract` | **Integrated** (Upgraded from simple JS bit-shift hash to real cryptographic SHA-256 in Kotlin) | **Retained** | Verified in `SovereignKernelAndOsIntegrationTest` (`computeSha256Hex`) |
| `TS-008` | `db.ts` | `/elyzareth-app02-curator/src/db.ts` | Browser `localStorage` persistence wrapper for report, base titles, and artifacts | **Data & Persistence** (`ElyzarethDatabase.kt`, `ElyzarethDao.kt`, `OsBranchDao.kt`) | AndroidX Room SQLite (`elyzareth_corpus_vault.db`) | **Integrated** (Upgraded from browser `localStorage` to ACID Room SQLite persistence) | **Retained** | Verified in `CurationLifecycleVerificationTest` (simulated restart persistence) |
| `TS-009` | `mockCorpus.ts` | `/elyzareth-app02-curator/src/mockCorpus.ts` | Deterministic generator of the 390-base-title / 510-artifact corpus baseline across Hindi, English, and Multilingual anchors | **Data & Persistence** (`CorpusSeedData.kt`) | `BaseTitleEntity`, `ArtifactEntity` | **Integrated** | **Retained** | Verified in `ExampleRobolectricTest` (390 titles / 510 artifacts) |
