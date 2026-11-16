# Shared Branch — Conceptual Taxonomy & Authoritative Ownership Index

- **Work Order:** `EOS-STRUCT-UI-003` (Section 3.1 & Section 7.2)
- **Branch Role:** Conceptual taxonomy / documentation index only (not a competing implementation root)
- **Authoritative Gradle Module:** `:core:shared` (`/core/shared/`)
- **Authoritative Kotlin Source Location:**
  - `/core/shared/src/main/java/com/example/shared/SystemUtilitiesAndServices.kt`
- **Sub-Branches Mapped:**
  - `common-resources/` -> `:core:shared` (`SystemHealthSnapshot`, `ExternalAdapterDescriptor`)
  - `shared-contracts/` -> `:core:shared` (`SovereignSecurityManager`, `SnapshotBackupManager`, `BackgroundJobCoordinator`, `OfflineFirstIntegrations`)
