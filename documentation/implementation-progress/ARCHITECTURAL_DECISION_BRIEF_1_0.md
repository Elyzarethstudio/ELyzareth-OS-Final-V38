# ELYZARETH OS — ARCHITECTURAL DECISION BRIEF 1.0

**Reference:** Master Blueprint 1.1  
**Input:** `FINAL_ARCHITECTURAL_RECONCILIATION_1_1.md`  
**Authority:** Student-Director / Human Creator  
**Execution Mode:** READ-ONLY AUDIT AND DOCUMENTATION  
**Implementation Freeze:** ACTIVE

> **Notice:** None of the six architectural decisions below have been applied. Every decision is prepared with exact file paths, Gradle ownership, risk analysis, verification criteria, and marked **`PENDING STUDENT-DIRECTOR APPROVAL`**.

---

## ARCH-DEC-01 — Canonical Location of Creative Factory

- **Status:** `PENDING STUDENT-DIRECTOR APPROVAL`
- **Exact Architectural Conflict:**
  - The repository contains two identical 9-sub-branch directory hierarchies for the Creative Factory:
    1. Nested inside the Creative domain: `/creative/creative-factory/`
    2. At the repository root: `/creative-factory/`
- **Affected Files and Directories:**
  - `/creative/creative-factory/{idea-processing,dna-matching,imagery-control,emotional-logic,structural-validation,language-validation,quality-assessment,correction-suggestions,factory-orchestrator}/.gitkeep` (9 files)
  - `/creative-factory/{idea-processing,dna-matching,imagery-control,emotional-logic,structural-validation,language-validation,quality-assessment,correction-suggestions,factory-orchestrator}/.gitkeep` (9 files)
  - `/app/src/main/java/com/example/ui/creative/CreativeBranchScreens.kt` (contains unapproved `CreativeDnaEngineScreen` with a Creative Factory card)
- **Existing Gradle Ownership and Dependencies:**
  - Neither `/creative/creative-factory/` nor `/creative-factory/` is registered in `/settings.gradle.kts`. Both contain only zero-byte `.gitkeep` files.
  - Current UI code referencing Creative Factory concepts resides inside `:app` (`/app/src/main/java/com/example/ui/creative/CreativeBranchScreens.kt`).
- **Consequences of Available Options:**
  - **Option 1A (Retain `/creative/creative-factory/` as canonical; preserve root `/creative-factory/` until cleanup is explicitly approved):**
    - Aligns with Master Blueprint 1.1 Section 2 & 6, where the Creative Factory operates inside Layer 3 (`Creative Production`).
    - Zero risk of deleting anything prematurely while the structural freeze is active.
  - **Option 1B (Retain root `/creative-factory/` as canonical):**
    - Elevates the Creative Factory to a root-level peer of `creative/`, `forensic/`, and `governance/`, diverging from the 6-layer hierarchy in Master Blueprint 1.1.
- **Recommended Option & Technical Justification:**
  - **Recommend Option 1A:** Designate `/creative/creative-factory/` as the canonical architectural path and preserve the root `/creative-factory/` duplicate untouched until an explicit cleanup pass is authorized. The Creative Factory's 9 sub-branches validate creative artifacts (ideas, DNA, imagery, emotional logic, structure, language) within the Creative Layer.
- **Data-Loss, Compatibility, Regression, and Migration Risks:**
  - **Data-Loss Risk:** None (`0` source or data files exist in either directory).
  - **Compatibility/Regression Risk:** None (`settings.gradle.kts` does not reference either directory).
  - **Migration Risk:** Low; only `.github/workflows/main.yml` line 21 checks `for dir in ... creative-factory ...` and must be updated at the same time `/creative-factory/` is eventually removed.
- **Verification Required Before Execution:**
  1. Confirm both directories contain only `.gitkeep` files (`0` `.kt` or asset files).
  2. Update `.github/workflows/main.yml` directory check list when root `/creative-factory/` removal is authorized.

---

## ARCH-DEC-02 — Relationship Between `:core` Gradle Modules and Top-Level `foundation/`, `kernel/`, `shared/` Branches

- **Status:** `PENDING STUDENT-DIRECTOR APPROVAL`
- **Exact Architectural Conflict:**
  - Active Gradle library modules reside under `/core/`:
    - `:core:foundation` (`/core/foundation/build.gradle.kts`)
    - `:core:kernel` (`/core/kernel/build.gradle.kts`)
    - `:core:shared` (`/core/shared/build.gradle.kts`)
  - Meanwhile, the approved structural branch hierarchy exists at the repository root:
    - `/foundation/{human-authority,constitution,system-identity}/.gitkeep`
    - `/kernel/{identity,authority,governor,decision-engine,event-router,state-manager,policy-engine,capability-registry,integrity-engine,recovery-manager,transaction-manager,kernel-api}/.gitkeep`
    - `/shared/{common-resources,shared-contracts}/.gitkeep`
- **Affected Files and Directories:**
  - `/settings.gradle.kts` (lines 27–30: `include(":app")`, `include(":core:foundation")`, `include(":core:kernel")`, `include(":core:shared")`)
  - `/app/build.gradle.kts` (lines 79–81: `implementation(project(":core:foundation"))`, `implementation(project(":core:kernel"))`, `implementation(project(":core:shared"))`)
  - `/core/foundation/build.gradle.kts` & `/core/foundation/src/main/java/com/example/foundation/ConstitutionalFoundation.kt`
  - `/core/kernel/build.gradle.kts` & `/core/kernel/src/main/java/com/example/kernel/SovereignKernel.kt`
  - `/core/shared/build.gradle.kts` & `/core/shared/src/main/java/com/example/shared/SystemUtilitiesAndServices.kt`
  - `/foundation/*`, `/kernel/*`, `/shared/*`
- **Existing Gradle Ownership and Dependencies:**
  - `:core:foundation` has no project dependencies.
  - `:core:kernel` depends on `project(":core:foundation")`.
  - `:core:shared` depends on `project(":core:foundation")` and `project(":core:kernel")`.
  - `:app` depends on `:core:foundation`, `:core:kernel`, and `:core:shared`.
- **Consequences of Available Options:**
  - **Option 2A (Keep Gradle modules under `/core/`; retain top-level `/foundation/`, `/kernel/`, `/shared/` as architectural branch maps):**
    - Preserves working Gradle build graphs and module dependencies without moving files or altering `settings.gradle.kts`.
    - Requires documenting clearly that `/core/*` is the physical Gradle implementation root for the `foundation/`, `kernel/`, and `shared/` architectural branches.
  - **Option 2B (Re-bind Gradle `projectDir` to top-level `/foundation/`, `/kernel/`, `/shared/` and remove `/core/`):**
    - Eliminates dual paths, but requires moving `build.gradle.kts` and `src/` trees and changing `settings.gradle.kts` and `/app/build.gradle.kts`.
- **Recommended Option & Technical Justification:**
  - **Recommend Option 2A (per Audit Recommendation):** Keep Gradle modules under `/core/` (`:core:foundation`, `:core:kernel`, `:core:shared`) and retain the top-level `/foundation/`, `/kernel/`, and `/shared/` directories as the canonical architectural branch maps until module-by-module implementation is authorized. This avoids unnecessary build-system churn during the architectural freeze.
- **Data-Loss, Compatibility, Regression, and Migration Risks:**
  - **Option 2A Risk:** Zero build regression or file movement risk.
  - **Option 2B Risk:** Moving Gradle project directories without synchronized `settings.gradle.kts` and CI updates can break Gradle sync.
- **Verification Required Before Execution:**
  1. Verify `:app`, `:core:foundation`, `:core:kernel`, and `:core:shared` dependency graph in `/documentation/architecture/MODULE_DEPENDENCY_MAP.md` reflects Option 2A.
  2. Ensure sub-package namespaces inside `/core/{foundation,kernel,shared}` map cleanly to the sub-branches in `/foundation/`, `/kernel/`, and `/shared/`.

---

## ARCH-DEC-03 — Disposition of Unapproved Phase-A Code, Invented UI Screens, and Premature G5 Logic

- **Status:** `PENDING STUDENT-DIRECTOR APPROVAL`
- **Exact Architectural Conflict:**
  - During the prior pass, 12 files/modules were created or expanded beyond the `STRUCTURE ONLY` authorization (`Scope Deviation Items #1–#12`), introducing:
    - 8 new Room entities (`OsBranchModels.kt`) and `OsBranchDao.kt`
    - Sample database seeding (`OsBranchRepository.kt` with `"Sovereign Horizon"`, `"T60 = 0.42s"`, `"440Hz + 528Hz"`, and pre-approved `G1`–`G4` flags)
    - Invented Compose screens (`CommandCentreScreen.kt`, `CreativeBranchScreens.kt`, `ProductionBranchScreens.kt`, `IntelligenceAndVaultScreens.kt`, `SystemAndKernelScreens.kt`) built before individual screen specifications existed
    - A premature one-click `"Seal Master (G5)"` button and Master Player toggle in `IntelligenceAndVaultScreens.kt`, violating Master Blueprint 1.1 Section 18 (*"No premature implementation of G5. No premature unlocking of Master Player."*)
- **Affected Files and Directories:**
  - `/core/foundation/src/main/java/com/example/foundation/ConstitutionalFoundation.kt`
  - `/core/kernel/src/main/java/com/example/kernel/SovereignKernel.kt`
  - `/core/shared/src/main/java/com/example/shared/SystemUtilitiesAndServices.kt`
  - `/app/src/main/java/com/example/data/model/OsBranchModels.kt`
  - `/app/src/main/java/com/example/data/local/OsBranchDao.kt`
  - `/app/src/main/java/com/example/data/local/ElyzarethDatabase.kt`
  - `/app/src/main/java/com/example/data/repository/OsBranchRepository.kt`
  - `/app/src/main/java/com/example/navigation/ElyzarethRoutes.kt`
  - `/app/src/main/java/com/example/MainActivity.kt`
  - `/app/src/main/java/com/example/viewmodel/ElyzarethViewModel.kt`
  - `/app/src/main/java/com/example/ui/commandcentre/CommandCentreScreen.kt`
  - `/app/src/main/java/com/example/ui/creative/CreativeBranchScreens.kt`
  - `/app/src/main/java/com/example/ui/production/ProductionBranchScreens.kt`
  - `/app/src/main/java/com/example/ui/intelligence/IntelligenceAndVaultScreens.kt`
  - `/app/src/main/java/com/example/ui/services/SystemAndKernelScreens.kt`
  - `/app/src/test/java/com/example/SovereignKernelAndOsIntegrationTest.kt`
- **Existing Gradle Ownership and Dependencies:**
  - Owned by `:app`, `:core:foundation`, `:core:kernel`, and `:core:shared`.
  - `MainActivity.kt` and `ElyzarethViewModel.kt` import and wire these classes into the runtime UI.
- **Consequences of Available Options:**
  - **Option 3A (Quarantine / Disable Unapproved Functionality & Sample Seeding; Preserve All Source Files):**
    - Preserves all written Kotlin files in source history (zero file deletion), disables automatic sample seeding (`seedOsBranchesIfNeeded()`), disables the premature G5 seal / Master Player unlock button (`Release Vault Locked Until G5 Specification`), and marks all invented screens as non-canonical placeholders awaiting official UI specifications.
  - **Option 3B (Hard Delete All 12 Unapproved Files):**
    - Reverts the codebase to the 3-screen `elyzareth-os.zip` state, losing reusable structural boilerplate and violating the no-deletion rule.
  - **Option 3C (Leave Unapproved Sample Seeding and Premature G5 Active):**
    - Violates Master Blueprint 1.1 Constitutional Principles and Section 18 boundaries.
- **Recommended Option & Technical Justification:**
  - **Recommend Option 3A (Quarantine / Disable Unapproved Behaviour While Preserving Source):** Do **not** delete any source files. When authorized, disable `seedOsBranchesIfNeeded()` so illustrative sample data is never inserted as real system state, lock G5 sealing and Master Player playback behind a strict constitutional lock banner, and replace each invented screen layout only when its official Student-Director UI specification is approved.
- **Data-Loss, Compatibility, Regression, and Migration Risks:**
  - **Data-Loss Risk:** Linked to `ElyzarethDatabase` version 2 migration (see `DATABASE_PRESERVATION_AUDIT_1_0.md`).
  - **Regression Risk:** `SovereignKernelAndOsIntegrationTest.kt` currently asserts on the seeded sample data (`"Sovereign Horizon"`) and G5 seal action; disabling sample seeding will require updating that test to assert on clean, unseeded state.
- **Verification Required Before Execution:**
  1. Audit all call sites of `seedOsBranchesIfNeeded()` and `sealReleaseWithG5()`.
  2. Verify that original tests (`CurationLifecycleVerificationTest`, `ExampleRobolectricTest`) remain 100% unaffected.

---

## ARCH-DEC-04 — Policy Review & Role of Legacy Physical-Witness Generator and 3.2.1.0 Protocol

- **Status:** `PENDING STUDENT-DIRECTOR APPROVAL`
- **Exact Architectural Conflict:**
  - The original `elyzareth-os.zip` baseline contains two specialized domain implementations:
    1. **Physical-Witness Lyric Generator (`LyricGeneratorScreen.kt` + `CorpusRepository.generateWitnessLyrics()` + `evaluateCustomLyrics()`):** Enforces 9 tangible physical objects (`Blue Thread`, `Dry Salt`, `Porcelain`, `Iron Key`, etc.), 5 Witness Pillars, fixed acoustic meters/BPMs, a deterministic 2-stanza local template (`"Cold floorboards under bare heels..."`), and a regex Banned Trope Scanner.
    2. **3.2.1.0 Protocol Console (`ProtocolConsoleScreen.kt`):** Enforces a 4-step countdown (`3 Listen -> 2 Evaluate -> 1 Decide -> 0 Freeze`) with `T60 decay 0.42s dry` room acoustics and a 4-State Verification Model (`SPEC`, `IMPLEMENTED / UNVERIFIED`, `VERIFIED`, `FAILED`).
  - In contrast, **Master Blueprint 1.1** and **`REF-IMG-02` (`Complete UI/UX Suite v1.0`)** define:
    - Module 2 (`Lyric Generator`) as a general prompt-based creative drafting workstation (`Verse`, `Chorus`, `Bridge`, `Full Song`, `Variations`, `Drafts`, `Generation History`).
    - Module 4 (`Creative DNA Engine`) as the configurable registry for artist identity, stylistic rules, and forbidden imagery.
    - Module 9 (`Governance Console`) as the 5-Gate (`G1`–`G5`) human sign-off matrix.
- **Affected Files and Directories:**
  - `/app/src/main/java/com/example/ui/generator/LyricGeneratorScreen.kt`
  - `/app/src/main/java/com/example/ui/protocol/ProtocolConsoleScreen.kt`
  - `/app/src/main/java/com/example/data/repository/CorpusRepository.kt` (lines 360–464 & 473–485)
  - `/app/src/main/java/com/example/data/model/CorpusModels.kt` (`PhysicalAnchor`, `WitnessPillar`, `DEFAULT_PHYSICAL_ANCHORS`, `GeneratedLyricStanza`)
  - `/app/src/test/java/com/example/ExampleRobolectricTest.kt` (tests `generateWitnessLyrics` and `evaluateCustomLyrics`)
- **Existing Gradle Ownership and Dependencies:**
  - Owned by `:app`. Coupled to `ElyzarethViewModel.kt`, `CorpusRepository.kt`, and `ExampleRobolectricTest.kt`.
- **Consequences of Available Options:**
  - **Option 4A (Preserve Both Intact Now; Conduct Dedicated Policy Review Before Re-architecting):**
    - Keeps `LyricGeneratorScreen.kt`, `ProtocolConsoleScreen.kt`, and `CorpusRepository.kt` 100% untouched until the Student-Director decides which rules belong to the global OS constitution vs. a specific artist Creative DNA profile (`C17 / Witness Vault` profile).
  - **Option 4B (Immediately Replace or Archive Legacy Generator & Protocol):**
    - Risks discarding domain intellectual property (`9 Physical Anchors`, `Banned Trope regex`, `3.2.1.0 Protocol`, `4-State Verification Model`) before its constitutional role is formally adjudicated.
- **Recommended Option & Technical Justification:**
  - **Recommend Option 4A (per Audit Recommendation):** Preserve both the Physical-Witness Generator and the 3.2.1.0 Protocol Console completely intact in the codebase. Conduct a dedicated policy review with the Student-Director to determine whether Physical Witness / Dry Room (`T60 = 0.42s`) rules should be encapsulated as a selectable **Creative DNA Profile** in Module 4 and whether `3.2.1.0` operates as the human review checklist inside Gates `G1`–`G5`.
- **Data-Loss, Compatibility, Regression, and Migration Risks:**
  - **Option 4A Risk:** Zero data-loss or test regression risk (`ExampleRobolectricTest.kt` continues to pass).
  - **Important Coupling Note:** `CorpusRepository.readEvidenceAndHash()` (lines 473–485) scans SAF-imported files for the 11 witness keywords (`thread`, `salt`, `wood`, `glass`, `key`, `stone`, `porcelain`, `hammer`, `dhaaga`, `naav`, `iron`) to set `ArtifactEntity.hasPhysicalWitnessAnchors`. Any future change must preserve `ArtifactEntity` schema compatibility.
- **Verification Required Before Execution:**
  1. Complete Student-Director policy review classifying each rule in `LyricGeneratorScreen.kt` and `ProtocolConsoleScreen.kt` as either (a) Global Constitutional Rule, (b) Selectable Creative DNA Profile rule, or (c) Legacy C17/v38.1 historical mode.

---

## ARCH-DEC-05 — Integration of App 02 Forensic Corpus Curator with `Corpus & Archive` Hub

- **Status:** `PENDING STUDENT-DIRECTOR APPROVAL`
- **Exact Architectural Conflict:**
  - `REF-IMG-02` Frame 7 (`7. Corpus & Archive`) depicts a 6-item module hub (`Song Catalogue`, `Version Registry`, `Lyric Library`, `Music Library`, `Search & Retrieve`, `Historical Archive`).
  - Meanwhile, the existing working application screen is `CorpusDashboardScreen.kt` (`App 02 — Lyric Corpus Curator`), which provides the Forensic Audit Evidence Report (`510/390/505/5/46`), SAF Directory Scanner, SHA-256 verification badges, language/status filters, and the 3-stage curation lifecycle (`CurationWorkflowPipeline.kt`).
- **Affected Files and Directories:**
  - `/app/src/main/java/com/example/ui/curator/CorpusDashboardScreen.kt`
  - `/app/src/main/java/com/example/ui/curator/CurationWorkflowPipeline.kt`
  - `/app/src/main/java/com/example/data/repository/CorpusRepository.kt`
  - `/app/src/main/java/com/example/data/local/CorpusSeedData.kt`
  - `/corpus/{song-catalog,base-title-registry,version-registry,clip-registry,libraries,historical-archive,witness-vault,duplicate-detection,similarity-index,search,metadata,statistics,ingestion,export,corpus-curator}/`
- **Existing Gradle Ownership and Dependencies:**
  - Owned by `:app` and verified by `CurationLifecycleVerificationTest.kt` and `ExampleRobolectricTest.kt`.
- **Consequences of Available Options:**
  - **Option 5A (Use `Corpus & Archive` Frame 7 as the Parent Hub and Retain `CorpusDashboardScreen.kt` as its Active Workstation):**
    - Preserves 100% of the working SAF scanner, Room database queries, and curation lifecycle while supporting the 6 sub-branches shown in Frame 7 (`Song Catalogue`, `Version Registry`, `Lyric Library`, `Music Library`, `Search & Retrieve`, `Historical Archive`).
  - **Option 5B (Replace `CorpusDashboardScreen.kt` with a Static Menu):**
    - Would destroy or hide the working SAF Corpus Curator and break existing Robolectric verification tests.
- **Recommended Option & Technical Justification:**
  - **Recommend Option 5A (per Audit Recommendation):** Designate `Corpus & Archive` as the parent module hub and retain the existing `CorpusDashboardScreen.kt` (`App 02 — Lyric Corpus Curator`) intact as its primary curation and SAF ingestion workstation.
- **Data-Loss, Compatibility, Regression, and Migration Risks:**
  - Zero data-loss risk for `base_titles` and `artifacts` tables when `CorpusDashboardScreen.kt` and `CorpusRepository.kt` are preserved intact.
- **Verification Required Before Execution:**
  1. Receive the detailed UI specification for `Module 7 — Corpus & Archive` defining how the 6 Frame 7 sub-sections route into the preserved App 02 Curator workstation.
  2. Verify `CurationLifecycleVerificationTest.kt` and `ExampleRobolectricTest.kt` pass without modification.

---

## ARCH-DEC-06 — Consolidation of `/tools/` and `/devtools/`

- **Status:** `PENDING STUDENT-DIRECTOR APPROVAL`
- **Exact Architectural Conflict:**
  - The repository root currently has both:
    - `/tools/{audit-tools,diagnostics,validation-tools}/.gitkeep` (approved in `ELYZARETH_OS_STRUCTURE_1.0`)
    - `/devtools/corpus_integrity_audit.sh` (created during an earlier turn)
- **Affected Files and Directories:**
  - `/tools/audit-tools/.gitkeep`
  - `/tools/diagnostics/.gitkeep`
  - `/tools/validation-tools/.gitkeep`
  - `/devtools/corpus_integrity_audit.sh`
- **Existing Gradle Ownership and Dependencies:**
  - Non-Gradle shell utility (`0` Gradle dependencies).
- **Consequences of Available Options:**
  - **Option 6A (Keep `/tools/` as the intended canonical destination; preserve `/devtools/corpus_integrity_audit.sh` in place until migration is explicitly approved):**
    - Honours the current read-only/no-deletion freeze while establishing `/tools/audit-tools/` as the target home for `corpus_integrity_audit.sh`.
  - **Option 6B (Retain both `/tools/` and `/devtools/` permanently):**
    - Leaves a redundant top-level directory outside `ELYZARETH_OS_STRUCTURE_1.0`.
- **Recommended Option & Technical Justification:**
  - **Recommend Option 6A (per Audit Recommendation):** Designate `/tools/` (`/tools/audit-tools/`) as the canonical destination, and preserve `/devtools/corpus_integrity_audit.sh` untouched until the structural cleanup pass is explicitly authorized.
- **Data-Loss, Compatibility, Regression, and Migration Risks:**
  - Zero risk.
- **Verification Required Before Execution:**
  1. Verify no CI workflow or script references `/devtools/corpus_integrity_audit.sh` before moving it to `/tools/audit-tools/corpus_integrity_audit.sh` in an authorized cleanup pass.
