# ELYZARETH OS — BUILD AND RUNTIME EVIDENCE STATUS 1.0

**Reference:** Master Blueprint 1.1 (Section 7 — Build and Runtime Evidence)  
**Authority:** Student-Director / Human Creator  
**Execution Mode:** READ-ONLY AUDIT AND DOCUMENTATION  
**Implementation Freeze:** ACTIVE (`No APK rebuild executed during this pass per Restriction #9`)

> **Forensic Evidence Principle:** The existence of `.apk` files on disk (`/public/elyzareth-os-structure-v1.0.apk`, `/public/app-debug.apk`, `/.build-outputs/app-debug.apk`) does **not** by itself prove that the current source tree was freshly compiled during this read-only pass, nor does it prove live Android device/emulator runtime behavior. Each verification layer is reported separately and honestly below.

---

## 1. Eight-Layer Verification Matrix

| # | Evidence Layer | Status in Current Pass | Forensic Evidence & Technical Details | What Remains Unverified / Missing |
|---:|---|---|---|---|
| 1 | **Source Inspection** | **COMPLETED (Verified in Current Pass)** | Direct read-only inspection completed via `view_file` and `list_dir` across `/settings.gradle.kts`, `/app/build.gradle.kts`, `/app/src/main/java/com/example/**`, `/core/{foundation,kernel,shared}/**`, `/elyzareth-app02-curator/**`, and `/app/src/test/java/com/example/**`. | None — 100% of source files on disk have been inspected and cataloged. |
| 2 | **Gradle Configuration Inspection** | **COMPLETED (Verified in Current Pass)** | Inspected `/settings.gradle.kts` (4 modules: `:app`, `:core:foundation`, `:core:kernel`, `:core:shared`), `/build.gradle.kts`, `/app/build.gradle.kts` (`namespace = "com.example"`, `applicationId = "com.aistudio.elyzareth.lyricv"`, `compileSdk = 36`, `minSdk = 24`, `targetSdk = 36`), and `/gradle/libs.versions.toml`. | Decision `ARCH-DEC-02` remains pending regarding `/core/*` vs top-level `/foundation/`, `/kernel/`, `/shared/`. |
| 3 | **Dependency Resolution** | **NOT TESTED IN THIS PASS** *(Previously resolved in prior turn)* | Per Section 9 Restriction (*"No APK rebuild"*), no Gradle build or sync command was executed in this read-only pass. In the prior turn (`2026-11-14T23:50:27-08:00`), `compile_applet` resolved dependencies from the local Gradle cache. | Fresh dependency resolution will be re-verified when build commands are next authorized. |
| 4 | **Compilation** | **NOT TESTED IN THIS PASS** *(Previously compiled at `2026-11-14T23:50:27-08:00`)* | Zero `.kt`, `.java`, `.xml`, or `.gradle.kts` files were modified after `2026-11-14T23:50:27-08:00` when `compile_applet` succeeded (`Build succeeded - the applet is compiled`). Per Restriction #9, no rebuild was triggered in this pass. | Fresh compilation check after `ARCH-DEC-01`–`06` cleanup is approved. |
| 5 | **Unit Tests (Local JVM / Robolectric)** | **NOT TESTED IN THIS PASS** *(Previously executed prior to Structural Freeze)* | Five test classes exist in `/app/src/test/java/com/example/`: `CurationLifecycleVerificationTest.kt`, `ExampleRobolectricTest.kt`, `ExampleUnitTest.kt`, `GreetingScreenshotTest.kt`, and `SovereignKernelAndOsIntegrationTest.kt`. Not re-executed during this read-only pass per Restriction #9. | Note: Local JVM/Robolectric tests verify repository/DAO state transitions in an isolated test environment, **not** end-to-end physical device SAF scanning or live APK runtime behavior. |
| 6 | **Instrumentation Tests (Emulator / Device `androidTest`)** | **NOT TESTED (Unavailable in Container)** | No `androidTest/` instrumentation suite has been executed; the cloud build environment has no attached Android Emulator or ADB bridge (`No Android Emulator or ADB available`). | Must be verified on physical Android hardware or external emulator by the Student-Director. |
| 7 | **APK Built from Current Source** | **UNVERIFIED IN THIS PASS (Static Artifact Present on Disk)** | `/public/elyzareth-os-structure-v1.0.apk` and `/public/app-debug.apk` (`22,978,201 bytes`, SHA-256: `aa42d755cdea3bd62e6818bed64b52207dd94b90dfbbed67c324e1f598ba80b1`) were copied from `.build-outputs/app-debug.apk` at `2026-11-14T23:49:49-08:00`. No source code has changed since that timestamp, but no rebuild was performed in this pass. | Independent verification by building the exported ZIP via GitHub Actions (`.github/workflows/main.yml`) or Android Studio. |
| 8 | **Runtime Launch & Live Feature Execution** | **NOT TESTED / UNVERIFIED** | Studio cannot launch or interact with the streaming Android emulator or inspect live device screen rendering, SAF folder picker dialogs, or process-restart persistence on hardware. | Requires live human runtime testing on an Android device by the Student-Director (specifically including the 1,000+ local corpus SAF Scan and scroll-bound verification). |

---

## 2. Known Runtime & Data Integrity Risks in the Current APK on Disk

Even though an APK binary (`22,978,201 bytes`) exists on disk, independent audit and source inspection establish that **the current APK must not be accepted as a production baseline** for the following verified reasons:

1. **Destructive Database Upgrade Risk (`ElyzarethDatabase.kt` line 48):**
   - Contains `version = 2` with `.fallbackToDestructiveMigration()` and no `Migration(1, 2)`. Installing over a `version = 1` database will wipe existing `base_titles` and `artifacts` tables.
2. **Unapproved Sample State Seeding (`OsBranchRepository.kt`):**
   - On launch, `ElyzarethViewModel` invokes `osBranchRepository.seedOsBranchesIfNeeded()`, which writes illustrative sample strings (`"Sovereign Horizon (Nocturne in D Minor)"`, `"T60 = 0.42s"`, pre-approved `G1`–`G4` statuses) into Room.
3. **Premature G5 Sealing Affordance (`IntelligenceAndVaultScreens.kt`):**
   - Module 12 (`MasterVaultAndPlayerScreen`) exposes a button that marks a release package as `SEALED_MASTER` without enforcing full G1–G5 constitutional verification or cryptographic manifest signing.
4. **Invented UI Screens (`Scope Deviations SD-08 through SD-12`):**
   - Modules 1, 3, 4, 5, 6, 8, 10, 11, 12, 13, and 14 display Studio-invented layouts created prior to detailed Student-Director UI specifications.
5. **Deterministic Stanza Template in App 01 (`CorpusRepository.generateWitnessLyrics`):**
   - Clicking `"Synthesize Concrete Witness Stanzas"` in App 01 outputs hardcoded witness lines rather than open-ended creative generation.

---

## 3. Evidence Still Missing Before Architectural & Runtime Acceptance

1. **Student-Director Sign-Off on `ARCH-DEC-01` through `ARCH-DEC-06`** (documented in `ARCHITECTURAL_DECISION_BRIEF_1_0.md`).
2. **Database Migration Remediation & Verification** (removing `fallbackToDestructiveMigration()`, implementing non-destructive `Migration(1, 2)` or reverting experimental tables, and proving via Robolectric migration test that v1 corpus records survive upgrade to v2).
3. **Quarantine of Unapproved Sample Data & Premature G5 Action** (verifying that cold launch starts with clean state and locked G5/Master Vault).
4. **Detailed Screen-by-Screen UI Specifications** for the 11 modules that currently have only composite thumbnails in `Complete UI/UX Suite v1.0`.
5. **Live Device Runtime Verification by Student-Director** confirming:
   - App launch and navigation stability.
   - Real Android Storage Access Framework (SAF) scan of the 1,000+ local corpus replacing the 390/510 fallback seed cleanly.
   - Full vertical scrolling of the scanned inventory above the fixed bottom navigation bar to the final discovered record.
