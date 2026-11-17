# ELYZARETH OS — BUILD AND RUNTIME EVIDENCE STATUS 1.0

**Reference:** Master Blueprint 1.1 (Section 7 — Build and Runtime Evidence) & `EOS-STRUCT-UI-003`  
**Authority:** Student-Director / Human Creator  
**Last Updated:** 2026-11-17  
**Implementation Freeze:** ACTIVE (`Application logic, database schema, and contracts remain frozen awaiting Phase 1 authorization`)

> **Forensic Evidence Principle:** The existence of `.apk` build artifacts (`/.build-outputs/app-debug.apk` or `app/build/outputs/apk/debug/app-debug.apk`; note that `/public/*.apk` copies were intentionally removed and excluded via `.gitignore` in `EOS-STRUCT-UI-003`) does **not** by itself prove live Android device/emulator runtime behavior. Each verification layer is reported separately and honestly below.

---

## 1. Eight-Layer Verification Matrix

| # | Evidence Layer | Status in Current Pass | Forensic Evidence & Technical Details | What Remains Unverified / Missing |
|---:|---|---|---|---|
| 1 | **Source Inspection** | **COMPLETED (Verified)** | Direct read-only inspection completed via `view_file` and `list_dir` across `/settings.gradle.kts`, `/app/build.gradle.kts`, `/app/src/main/java/com/example/**`, `/core/{foundation,kernel,shared}/**`, `/elyzareth-app02-curator/**`, `*/OWNERSHIP_INDEX.md`, and `/app/src/test/java/com/example/**`. | None — 100% of source and ownership index files on disk have been inspected and cataloged. |
| 2 | **Gradle Configuration Inspection** | **COMPLETED (Verified)** | Inspected `/settings.gradle.kts` (4 modules retained per `EOS-STRUCT-UI-003` / `ARCH-DEC-02`: `:app`, `:core:foundation`, `:core:kernel`, `:core:shared`), `/build.gradle.kts`, `/app/build.gradle.kts` (`namespace = "com.example"`, `applicationId = "com.aistudio.elyzareth.lyricv"`, `compileSdk = 36`, `minSdk = 24`, `targetSdk = 36`), and `/gradle/libs.versions.toml`. | `ARCH-DEC-07` remains pending regarding commenting out unused Firebase/Retrofit dependencies in `app/build.gradle.kts`. |
| 3 | **Dependency Resolution** | **VERIFIED** | Dependencies resolved cleanly from local Gradle cache for `:app`, `:core:foundation`, `:core:kernel`, and `:core:shared`. | Re-verification after `ARCH-DEC-07` cleanup is approved. |
| 4 | **Compilation** | **VERIFIED** | Zero `.kt`, `.java`, `.xml`, or `.gradle.kts` files were modified during `EOS-ARCH-RECON-001` or `EOS-STRUCT-UI-003`; the 4-module project compiles cleanly (`Build succeeded - the applet is compiled`). | Re-verification when Phase 1 (`RM-P0-01`..`RM-P0-04`) is authorized. |
| 5 | **Unit Tests (Local JVM / Robolectric)** | **BASELINE PRESENT (11 New DB/SAF Tests Specified in v1.1 Test Plan)** | Five test classes exist in `/app/src/test/java/com/example/`: `CurationLifecycleVerificationTest.kt`, `ExampleRobolectricTest.kt`, `ExampleUnitTest.kt`, `GreetingScreenshotTest.kt`, and `SovereignKernelAndOsIntegrationTest.kt`. In addition, 11 mandatory DB/SAF acceptance tests (`A1`–`A4`, `B1`–`B7`) are fully specified in `DATABASE_REMEDIATION_TEST_PLAN_1_0.md` awaiting Phase 1 execution approval. | Local JVM/Robolectric tests verify repository/DAO state transitions in an isolated test environment, **not** end-to-end physical device SAF scanning or live APK runtime behavior. |
| 6 | **Instrumentation Tests (Emulator / Device `androidTest`)** | **NOT TESTED (Unavailable in Container)** | No `androidTest/` instrumentation suite has been executed; the cloud build environment has no attached Android Emulator or ADB bridge (`No Android Emulator or ADB available`). | Must be verified on physical Android hardware or external emulator by the Student-Director. |
| 7 | **APK Built from Current Source** | **STATIC BUILD OUTPUT PRESENT (`/.build-outputs/app-debug.apk`)** | Per `EOS-STRUCT-UI-003` Approved APK Export Policy (`STRUCTURAL_COMPLETION_REGISTER.md` Section 4), duplicate APK binaries were removed from `/public/` and excluded via `/.gitignore` (`public/*.apk`), while standard build output (`/.build-outputs/app-debug.apk` / `app/build/outputs/apk/debug/app-debug.apk`) and GitHub Actions artifact uploads remain active. | Independent verification by building the exported ZIP via GitHub Actions (`.github/workflows/main.yml`) or Android Studio. |
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
