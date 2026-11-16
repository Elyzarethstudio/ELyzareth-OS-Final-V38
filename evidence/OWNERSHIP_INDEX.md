# Evidence & Provenance Branch — Conceptual Taxonomy & Authoritative Ownership Index

- **Work Order:** `EOS-STRUCT-UI-003` (Section 3.4)
- **Branch Role:** Conceptual branch taxonomy only
- **Authoritative Gradle Module:** `:app` (UI & Room persistence) and `:core:kernel` (Kernel event ledger)
- **Authoritative Kotlin UI & Persistence Locations:**
  - `10 evidence_vault` (`EvidenceVaultScreen`): `/app/src/main/java/com/example/ui/intelligence/IntelligenceAndVaultScreens.kt`
  - Evidence Models & Repository: `/app/src/main/java/com/example/data/model/OsBranchModels.kt` (`EvidenceLedgerEntity`), `/app/src/main/java/com/example/data/repository/OsBranchRepository.kt`
  - Kernel Event Ledger: `/core/kernel/src/main/java/com/example/kernel/SovereignKernel.kt`
