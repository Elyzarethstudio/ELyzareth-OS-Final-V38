# Elyzareth OS v38.1 — App 02: Lyric Corpus Curator

An immutable, forensic lyric ingestion and curation engine built for **Elyzareth OS v38.1**. Designed for zero-mutation discovery, SHA-256 evidence hashing, and strict human governance across large lyric corpuses (390+ base titles / 950+ artifacts).

---

## 🏛️ System Architecture & Governance

- **OS Shell:** Elyzareth OS v38.1 UniversalWindowShell (Single-space desktop & Android APK target)
- **Governance:** Human Governor Gate (Read-Only Discovery & Evidence Phase)
- **Zero-Bypass Gate Pipeline:**
  - **G1:** Witness Transformation Contract
  - **G2:** Read-Only Evaluator Logic
  - **G3:** Performance & Calibration Gate
  - **G4:** Integrity Gate (SHA-256 Checksum Match)
  - **G5:** Final Master Release Lock (`ELYZARETH_FINAL/`)
- **Verification Model:** 4-State Verification (`VERIFIED`, `IMPLEMENTED/UNVERIFIED`, `FAILED`, `SPEC`, default: `NOT MEASURED`)
- **Curation Protocol:** 3.2.1.0 Protocol (*Listen → Evaluate → Decide → Freeze*)

---

## 📁 Repository Directory Structure

```
Corpus-curator/
├── app/                        # Android Native APK Shell (Kotlin + Jetpack Compose + Room DB + SAF)
├── elyzareth-app02-curator/    # Web Applet Core (React + TypeScript + Tailwind)
│   ├── index.html
│   ├── vite.config.ts
│   ├── package.json
│   └── src/
│       ├── types.ts            # Forensic schemas & 4-state verification types
│       ├── db.ts               # LocalStorage persistent index
│       ├── mockCorpus.ts       # 500-title / 950+ artifact real-corpus simulator
│       ├── parser.ts           # Non-destructive file parser & SHA-256 hashing
│       ├── main.tsx            # UniversalWindowShell entry component
│       └── components/         # Header, Summary, Table, and Inspection Modal
├── gradle/                     # Gradle Wrapper configuration
├── build.gradle.kts            # Root Android build configuration
└── settings.gradle.kts         # Gradle settings & app module bindings
```

---

## 🚀 Getting Started

### Web Applet Development
```bash
cd elyzareth-app02-curator
npm install
npm run dev
```

### Android APK Build
```bash
gradle assembleDebug
```

---

## 🛡️ License & Governance Rule
**Zero-Mutation Ingestion Pass:** Source files are never modified during discovery or evaluation. All G1–G5 audit metrics remain `NOT MEASURED` pending explicit Human Governor approval.
