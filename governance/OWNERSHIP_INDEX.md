# Governance Branch — Conceptual Taxonomy & Authoritative Ownership Index

- **Work Order:** `EOS-STRUCT-UI-003` (Section 3.6)
- **Branch Role:** Conceptual branch taxonomy only
- **Authoritative Gradle Modules:** `:app` (UI) and `:core:kernel` (Sovereign Kernel gate engine)
- **Authoritative Kotlin UI & Kernel Locations:**
  - `09 governance_console` (`GovernanceSuiteScreen`): `/app/src/main/java/com/example/ui/services/SystemAndKernelScreens.kt`
  - Preserved 3.2.1.0 Protocol Console (`ProtocolConsoleScreen`): `/app/src/main/java/com/example/ui/protocol/ProtocolConsoleScreen.kt` (reachable inside `09 governance_console`)
  - Kernel Gate Engine (`G1`–`G5`): `/core/kernel/src/main/java/com/example/kernel/SovereignKernel.kt`
