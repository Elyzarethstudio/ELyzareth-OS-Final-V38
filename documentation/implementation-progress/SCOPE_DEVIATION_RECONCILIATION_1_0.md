# ELYZARETH OS — SCOPE DEVIATION RECONCILIATION 1.0

**Reference:** Master Blueprint 1.1 (Section 4 & Section 5 — Scope Deviation & Creative Policy Preservation)  
**Authority:** Student-Director / Human Creator  
**Execution Mode:** READ-ONLY AUDIT AND DOCUMENTATION  
**Implementation Freeze:** ACTIVE

**Allowed Classification Vocabulary (Strict):**
- `EXISTING BASELINE` — Present in the original `elyzareth-os.zip` baseline.
- `NEW UNAPPROVED ADDITION` — Added by Studio outside the `STRUCTURE ONLY` authorization.
- `APPROVED ADDITION` — Explicitly requested and approved by the Student-Director.
- `DECISION REQUIRED` — Requires a formal architectural or policy ruling by the Student-Director.

> **Preservation Guarantee:** Classifying an item as `NEW UNAPPROVED ADDITION` or `DECISION REQUIRED` does **not** authorize deleting it. All 17 items below remain preserved in the repository until the Student-Director authorizes specific quarantine or refactoring steps.

---

## 1. Reconciled Register of All Reported Scope Deviations

| Item # | File Path(s) | Exact Description of Code / Feature | Classification | Disposition & Preservation Rule |
|---:|---|---|---|---|
| **SD-01** | `/core/foundation/build.gradle.kts`<br>`/core/foundation/src/main/java/com/example/foundation/ConstitutionalFoundation.kt` | Gradle module `:core:foundation` and Kotlin definitions for `ConstitutionalLaw` (`LAW-01`–`LAW-06`), `GateDefinition` (`G1`–`G5`), `PipelineStageSpec` (`1`–`14`), and `VerificationState`. | `NEW UNAPPROVED ADDITION` (`DECISION REQUIRED` under `ARCH-DEC-02` & `ARCH-DEC-03`) | Preserve source file; do not treat as final approved Foundation contract until ratified. |
| **SD-02** | `/core/kernel/build.gradle.kts`<br>`/core/kernel/src/main/java/com/example/kernel/SovereignKernel.kt` | Gradle module `:core:kernel` and Kotlin implementation of `SovereignKernel` governor state machine, gate transition evaluator, release lock check, SHA-256 event hashing, and recovery checkpoint state. | `NEW UNAPPROVED ADDITION` (`DECISION REQUIRED` under `ARCH-DEC-02` & `ARCH-DEC-03`) | Preserve source file; requires alignment with Master Blueprint 1.1 Section 5 & 7 before production promotion. |
| **SD-03** | `/core/shared/build.gradle.kts`<br>`/core/shared/src/main/java/com/example/shared/SystemUtilitiesAndServices.kt` | Gradle module `:core:shared` containing `SovereignSecurityManager`, `SnapshotBackupManager`, `BackgroundJobCoordinator`, and `OfflineFirstIntegrations` with simulated adapter states. | `NEW UNAPPROVED ADDITION` (`DECISION REQUIRED` under `ARCH-DEC-02` & `ARCH-DEC-03`) | Preserve source file; simulated adapter statuses must not be reported as live hardware/cloud integrations. |
| **SD-04** | `/app/src/main/java/com/example/data/model/OsBranchModels.kt` | 8 experimental Room `@Entity` tables (`IdeaBlueprintEntity`, `CreativeDnaEntity`, `MusicProductionEntity`, `VisualDirectionEntity`, `ForensicReportEntity`, `EvidenceLedgerEntity`, `RegisteredAssetEntity`, `MasterReleaseEntity`). | `NEW UNAPPROVED ADDITION` | Preserve source file; mark schemas as experimental pending Phase 1/2 entity specification. |
| **SD-05** | `/app/src/main/java/com/example/data/local/OsBranchDao.kt`<br>`/app/src/main/java/com/example/data/local/ElyzarethDatabase.kt` (v1 -> v2) | Added `OsBranchDao` and incremented `ElyzarethDatabase` to `version = 2` with `.fallbackToDestructiveMigration()` (line 48). | `NEW UNAPPROVED ADDITION` (`DECISION REQUIRED` — Critical Data Preservation Risk) | Preserve files during freeze; must add non-destructive `Migration(1, 2)` and remove `fallbackToDestructiveMigration()` immediately upon approval (see `DATABASE_PRESERVATION_AUDIT_1_0.md`). |
| **SD-06** | `/app/src/main/java/com/example/data/repository/OsBranchRepository.kt` | Repository containing `seedOsBranchesIfNeeded()` which inserts illustrative sample records (`"Sovereign Horizon (Nocturne in D Minor)"`, `"T60 = 0.42s"`, `"440Hz + 528Hz"`, pre-approved `G1`–`G4` flags) into Room on launch. | `NEW UNAPPROVED ADDITION` | Preserve source file; quarantine/disable `seedOsBranchesIfNeeded()` upon approval so sample data and unapproved acoustic policies are never injected as real system data. |
| **SD-07** | `/app/src/main/java/com/example/navigation/ElyzarethRoutes.kt`<br>`/app/src/main/java/com/example/MainActivity.kt`<br>`/app/src/main/java/com/example/viewmodel/ElyzarethViewModel.kt` | Expanded original `MainActivity.kt` and `ElyzarethViewModel.kt` with a 16-destination `OsModuleDestination` navigation shell, modal drawer, top bar, and 5-tab bottom bar while preserving original `Corpus`, `Generator`, and `Protocol` tabs. | `DECISION REQUIRED` (Modified `EXISTING BASELINE` + `NEW UNAPPROVED ADDITION`) | Preserve all existing methods and original 3 tabs; review navigation shell against official UI specification. |
| **SD-08** | `/app/src/main/java/com/example/ui/commandcentre/CommandCentreScreen.kt` | Compose UI screen for Command Centre with 3x4 module grid, 14-stage pipeline strip, and `"100% System Health"` indicator implemented prior to individual UI specification sign-off. | `NEW UNAPPROVED ADDITION` | Preserve source file; treat as non-canonical draft until Command Centre UI specification is approved. |
| **SD-09** | `/app/src/main/java/com/example/ui/creative/CreativeBranchScreens.kt` | Invented Compose UI screens for Module 1 (`IdeaBlueprintStudioScreen`), Module 3 (`LyricCuratorModuleScreen`), and Module 4 (`CreativeDnaEngineScreen`) created from composite thumbnails before detailed screen specifications existed. | `NEW UNAPPROVED ADDITION` | Preserve source file; do not treat invented layouts as approved UI designs. |
| **SD-10** | `/app/src/main/java/com/example/ui/production/ProductionBranchScreens.kt` | Invented Compose UI screens for Module 5 (`MusicProductionConsoleScreen`), Module 6 (`VisualDirectionStudioScreen`), and Module 11 (`AssetRegistryScreen`), including hardcoded `"T60 = 0.42s"` acoustic constraints. | `NEW UNAPPROVED ADDITION` | Preserve source file; quarantine unapproved `"T60 = 0.42s"` policy and replace layouts when detailed specs are provided. |
| **SD-11** | `/app/src/main/java/com/example/ui/intelligence/IntelligenceAndVaultScreens.kt` | Invented Compose UI screens for Module 8 (`ForensicLabScreen`), Module 10 (`EvidenceVaultScreen`), and Module 12 (`MasterVaultAndPlayerScreen`), including a premature one-click `"Seal Master (G5)"` button and Master Player toggle. | `NEW UNAPPROVED ADDITION` | Preserve source file; **must disable G5 sealing and Master Player unlocking** per Master Blueprint 1.1 Section 18 (*"No premature implementation of G5. No premature unlocking of Master Player."*). |
| **SD-12** | `/app/src/main/java/com/example/ui/services/SystemAndKernelScreens.kt` | Invented Compose UI screens for Module 9 (`GovernanceSuiteScreen`), Module 13 (`SystemServicesScreen`), Module 14 (`ProjectAndSystemOverviewScreen`), and `SovereignKernelScreen`. | `NEW UNAPPROVED ADDITION` | Preserve source file; do not treat invented layouts as approved UI designs. |
| **SD-13** | `/app/src/main/java/com/example/ui/generator/LyricGeneratorScreen.kt`<br>`/app/src/main/java/com/example/data/repository/CorpusRepository.kt` (`generateWitnessLyrics`, `evaluateCustomLyrics`)<br>`/app/src/main/java/com/example/data/model/CorpusModels.kt` (`PhysicalAnchor`, `WitnessPillar`) | Physical-Witness Lyric Generator featuring 9 tangible physical anchors (`Blue Thread`, `Dry Salt`, `Porcelain`, etc.), 5 Witness Pillars, a deterministic 2-stanza local template engine, and a live regex Banned Trope Scanner. | `EXISTING BASELINE` (`DECISION REQUIRED` under `ARCH-DEC-04` for Creative Policy review) | **Strictly preserve.** Existed in original `elyzareth-os.zip`. See Section 2 below for full Creative Policy Preservation analysis. |
| **SD-14** | `/app/src/main/java/com/example/ui/protocol/ProtocolConsoleScreen.kt` | `3.2.1.0 Protocol & Gate Audit` screen (`3 Listen -> 2 Evaluate -> 1 Decide -> 0 Freeze`), `T60 decay 0.42s dry` acoustic check description, and `4-State Verification Model` card. | `EXISTING BASELINE` (`DECISION REQUIRED` under `ARCH-DEC-04` for Creative Policy review) | **Strictly preserve.** Existed in original `elyzareth-os.zip`. See Section 2 below for full Creative Policy Preservation analysis. |
| **SD-15** | `/app/src/main/java/com/example/data/local/CorpusSeedData.kt` | Deterministic generator producing `390` base titles and `510` artifacts (`Corpus Lyric #1 (Dry Room)`) when `base_titles` count is `0`. | `EXISTING BASELINE` | **Strictly preserve.** Functions as initial empty-DB fallback seed prior to local SAF Corpus Scan. |
| **SD-16** | `/app/src/test/java/com/example/SovereignKernelAndOsIntegrationTest.kt` | Robolectric & Compose UI test suite added to test the Phase-A `:core:*` and `OsBranchRepository` additions. | `NEW UNAPPROVED ADDITION` (`DECISION REQUIRED`) | Preserve test file; update assertions when `ARCH-DEC-03` quarantine/cleanup is authorized. |
| **SD-17** | `/public/elyzareth-os-structure-v1.0.apk`<br>`/public/app-debug.apk`<br>`/public/elyzareth-os-structure.yaml`<br>`/public/elyzareth-os-structure.yml`<br>`/.github/workflows/main.yml`<br>`/.github/workflows/elyzareth-structure-freeze.yaml` | Structure baseline APK copies in `/public/` and GitHub `.yaml` / `.yml` workflows and manifests. | `APPROVED ADDITION` | Explicitly requested by Student-Director (*"Can you keep this structure apk in public. Also YAML and yml for GitHub"*). Retain. |

---

## 2. Creative Policy Preservation Audit (Section 5 Directive)

Per Section 5 of the directive, the existing **Physical-Witness Generator** (`SD-13`) and **3.2.1.0 Protocol** (`SD-14`) from the original `elyzareth-os.zip` baseline have been audited in read-only mode. Neither has been deleted, rewritten, or automatically promoted into global OS policy.

### 2.1 Existing Behaviour

#### A. Physical-Witness Lyric Generator (`LyricGeneratorScreen.kt` + `CorpusRepository.kt`)
1. **9 Physical Witness Anchors (`DEFAULT_PHYSICAL_ANCHORS` in `CorpusModels.kt`):**
   - `Blue Thread` (`Neela Dhaaga`), `Dry Salt` (`Namak`), `Unvarnished Wood` (`Kora Lakdi`), `Clear Glass` (`Saaf Kaanch`), `Iron Key` (`Lohe Ki Chaabi`), `River Stone` (`Nadi Ka Patthar`), `Cracked Porcelain` (`Toota चीनी मिट्टी`), `Heavy Hammer` (`Hathoda`), `Paper Boat` (`Kagaz Ki Naav`).
2. **5 Witness Pillars (`WitnessPillar` enum):**
   - `DOMESTIC`, `ENVIRONMENTAL`, `RECOVERY`, `CULTURAL`, `TRANSCENDENTAL`.
3. **Tempo & Acoustic Meter Selection:**
   - Meters: `4/4`, `3/4`, `6/8`.
   - BPMs: `68`, `72`, `80`, `88`.
4. **Deterministic Stanza Template (`CorpusRepository.generateWitnessLyrics`, lines 360–433):**
   - Places the user's custom seed line (or `"The <anchor> lies on the floor."`) as Line 1 of Stanza 1, followed by 3 hardcoded lines determined solely by the selected `WitnessPillar` (e.g., `"Cold floorboards under bare heels."`, `"The salt cellar sits beside the lamp."`, `"No cinematic swell, only dry wood."`), plus a hardcoded Stanza 2 (`"Measured tempo at <bpm> BPM, <meter> time. Envelope Class A morphology intact..."`).
5. **Forensic Trope Scanner (`CorpusRepository.evaluateCustomLyrics`, lines 435–464):**
   - Deducts `25` points for each regex match across 4 prohibited categories (`metaphor`, `cinematic swell`, `pop melodrama`, `speculative AI filler`) and deducts `30` points if none of 12 physical witness keywords (`thread`, `salt`, `wood`, `glass`, `key`, `stone`, `porcelain`, `hammer`, `dhaaga`, `naav`, `iron`, `floor`) appear in the verse.

#### B. 3.2.1.0 Protocol Console (`ProtocolConsoleScreen.kt`)
1. **Stop Condition Mandate Banner:** States that Pass 1 is `DISCOVERY + EVIDENCE ONLY` and `G1–G5` scores remain `NOT MEASURED` until Human Governor confirmation.
2. **4 Sequential Countdown Steps (`uiState.protocolStep` from `3` down to `0`):**
   - `Step 3 — LISTEN (Acoustic Morphology)`: References `STFT spectral centroids`, `T60 decay 0.42s dry`, `phase coherence`, `zero synthetic cinematic swells`.
   - `Step 2 — EVALUATE (Witness Constraints)`: References `Blue thread, dry salt, porcelain, iron key` and the `4-State Verification Model`.
   - `Step 1 — DECIDE (Human Governor Authority)`: Human review of duplicates, orphans, and Hindi/English classifications.
   - `Step 0 — FREEZE (Final Master Lock)`: Locks G5 state master in Room.
3. **4-State Verification Model Card:** Defines `1. SPEC`, `2. IMPLEMENTED / UNVERIFIED`, `3. VERIFIED`, `4. FAILED`.
4. **Gate Lock Summary:** Labels `G1: Witness Transformation | G2: Read-Only Evaluator | G3: Performance/T60 | G4: Integrity | G5: Final Master Lock`.

### 2.2 Existing Dependencies
- `CorpusModels.kt` defines `PhysicalAnchor`, `WitnessPillar`, `DEFAULT_PHYSICAL_ANCHORS`, and `GeneratedLyricStanza`, and includes `val hasPhysicalWitnessAnchors: Boolean` on `ArtifactEntity`.
- `CorpusRepository.readEvidenceAndHash()` (lines 473–485) checks SAF-imported files for the 11 witness keywords to populate `ArtifactEntity.hasPhysicalWitnessAnchors`.
- `ElyzarethViewModel.kt` holds `generatorSelectedAnchor`, `generatorSelectedPillar`, `generatorMeter`, `generatorBpm`, `generatorCustomSeed`, `generatedStanzas`, `customLyricInput`, `customLyricEvaluation`, and `protocolStep`.
- `ExampleRobolectricTest.kt` directly tests `generateWitnessLyrics()` and `evaluateCustomLyrics()`.

### 2.3 Potential Conflicts with Master Blueprint 1.1
1. **Gate Definition Conflict (`G1`–`G5`):**
   - `ProtocolConsoleScreen.kt` line 285 defines `G1–G5` as:
     - `G1: Witness Transformation`, `G2: Read-Only Evaluator`, `G3: Performance/T60`, `G4: Integrity`, `G5: Final Master Lock`.
   - **Master Blueprint 1.1 Section 4** defines the five constitutional gates as:
     - `G1: Concept and curated lyrics`, `G2: Provenance and evidence`, `G3: Creative DNA`, `G4: Forensic clearance`, `G5: Final release sealing`.
2. **Universal Hardcoding vs. Artist Creative DNA Profile:**
   - Hardcoding `9 Physical Witness Anchors`, `68–88 BPM`, and `T60 = 0.42s dry room` into the global Lyric Generator and Governance Console restricts the OS to a single aesthetic preset (`C17 / Witness Vault`), whereas Master Blueprint 1.1 places artist-specific imagery rules, banned tropes, and sonic parameters inside **Module 4 — Creative DNA Engine** (`CreativeDnaProfile`).
3. **Deterministic Stanza Template vs. Module 2 Lyric Generator UI Reference:**
   - `generateWitnessLyrics()` outputs the same fixed stanza lines regardless of the user's prompt theme, which caused the runtime behavior noted in the user's forensic diagnosis (`"baba blacksheep" -> "Cold floorboards under bare heels..."`).

### 2.4 Data and Creative-History Preservation Requirements
1. `ArtifactEntity.hasPhysicalWitnessAnchors` in `CorpusModels.kt` must be retained so existing Room tables and SAF scan records remain 100% compatible.
2. `DEFAULT_PHYSICAL_ANCHORS`, `WitnessPillar`, `evaluateCustomLyrics()`, and `ProtocolConsoleScreen.kt` represent recovered historical IP from the C17 / v38.1 generation and must **never** be deleted without preserving them in an accessible module or profile.
3. Existing Robolectric tests in `ExampleRobolectricTest.kt` that verify `evaluateCustomLyrics()` and `generateWitnessLyrics()` must continue to pass until the Student-Director authorizes test updates.

### 2.5 Options for Future Policy Integration (`PENDING STUDENT-DIRECTOR APPROVAL`)
- **Policy Option A (Encapsulate as the Canonical `"Elyzareth C17 Witness / Dry-Room DNA Profile"` inside Module 4 — Creative DNA Engine):**
  - Preserve all 9 Physical Witness Anchors, 5 Witness Pillars, Banned Trope regex rules, and `T60 = 0.42s` acoustic constraints as a selectable, first-class `CreativeDnaProfile` in **Module 4 (`creative/creative-dna/`)** and **Creative Factory (`creative/creative-factory/imagery-control/`)**, while allowing **Module 2 (`Lyric Generator`)** and **Module 9 (`Governance Console`)** to follow the Master Blueprint 1.1 `G1`–`G5` definitions.
- **Policy Option B (Maintain Dual-Mode Switch in Module 2 and Module 9):**
  - Provide a mode toggle in Module 2 (`Standard Blueprint Mode` vs. `C17 Physical-Witness Workbench`) and in Module 9 (`G1–G5 Constitutional Matrix` vs. `3.2.1.0 Protocol Console`).
- **Policy Option C (Retain Current Screens As-Is):**
  - Keep `LyricGeneratorScreen.kt` and `ProtocolConsoleScreen.kt` unchanged as the primary screens for Modules 2 and 9.
