# Production Branch — Conceptual Taxonomy & Authoritative Ownership Index

- **Work Order:** `EOS-STRUCT-UI-003` (Section 3.3)
- **Branch Role:** Conceptual branch taxonomy only
- **Authoritative Gradle Module:** `:app`
- **Authoritative Kotlin UI & Persistence Locations:**
  - `05 music_production` (`MusicProductionConsoleScreen`): `/app/src/main/java/com/example/ui/production/ProductionBranchScreens.kt`
  - `06 visual_direction` (`VisualDirectionStudioScreen`): `/app/src/main/java/com/example/ui/production/ProductionBranchScreens.kt`
  - `11 asset_registry` (`AssetRegistryScreen`): `/app/src/main/java/com/example/ui/production/ProductionBranchScreens.kt`
  - Persistence & Models: `/app/src/main/java/com/example/data/model/OsBranchModels.kt`, `/app/src/main/java/com/example/data/repository/OsBranchRepository.kt`
