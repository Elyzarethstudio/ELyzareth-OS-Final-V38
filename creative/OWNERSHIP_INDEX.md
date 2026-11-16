# Creative Production & Creative Factory Branch — Conceptual Taxonomy & Authoritative Ownership Index

- **Work Order:** `EOS-STRUCT-UI-003` (Section 3.2 & Section 7.1)
- **Branch Role:** Conceptual branch taxonomy only (single Creative Factory subtree reconciled at `/creative/creative-factory/`; duplicate root `/creative-factory/` removed after verifying identical 0-byte `.gitkeep` contents)
- **Authoritative Gradle Module:** `:app`
- **Authoritative Kotlin UI & Persistence Locations:**
  - `01 idea_blueprint` (`IdeaBlueprintStudioScreen`): `/app/src/main/java/com/example/ui/creative/CreativeBranchScreens.kt`
  - `02 lyric_generator` (`LyricGeneratorScreen` — Preserved App 01): `/app/src/main/java/com/example/ui/generator/LyricGeneratorScreen.kt`
  - `03 lyric_curator` (`LyricCuratorModuleScreen`): `/app/src/main/java/com/example/ui/creative/CreativeBranchScreens.kt`
  - `04 creative_dna` (`CreativeDnaEngineScreen`): `/app/src/main/java/com/example/ui/creative/CreativeBranchScreens.kt`
  - Persistence & Models: `/app/src/main/java/com/example/data/model/OsBranchModels.kt`, `/app/src/main/java/com/example/data/repository/OsBranchRepository.kt`
- **Creative Factory Subtree (`/creative/creative-factory/`):**
  - Conceptual branch taxonomy only (`idea-processing/`, `dna-matching/`, `imagery-control/`, `emotional-logic/`, `structural-validation/`, `language-validation/`, `quality-assessment/`, `correction-suggestions/`, `factory-orchestrator/`).
  - Dedicated executable factory implementation has **not yet been established or verified**.

