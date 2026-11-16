# Sovereign Kernel Branch — Conceptual Taxonomy & Authoritative Ownership Index

- **Work Order:** `EOS-STRUCT-UI-003` (Section 3.1 & Section 7.2)
- **Branch Role:** Conceptual taxonomy / documentation index only (not a competing implementation root)
- **Authoritative Gradle Module:** `:core:kernel` (`/core/kernel/`)
- **Authoritative Kotlin Source Locations:**
  - Kernel Engine & State Machine: `/core/kernel/src/main/java/com/example/kernel/SovereignKernel.kt`
  - Kernel UI Screen (`15 sovereign_kernel`): `/app/src/main/java/com/example/ui/services/SystemAndKernelScreens.kt` (`SovereignKernelScreen`)
- **Sub-Branches Mapped:**
  - `identity/`, `authority/`, `governor/`, `decision-engine/`, `event-router/`, `state-manager/`, `policy-engine/`, `capability-registry/`, `integrity-engine/`, `recovery-manager/`, `transaction-manager/`, `kernel-api/` -> `:core:kernel` (`SovereignKernel.kt`)
