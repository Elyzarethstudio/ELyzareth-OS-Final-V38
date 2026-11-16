# ELYZARETH OS — SECURITY, HUMAN AUTHORITY AND RECOVERY SPECIFICATION 1.0

**Assignment ID:** `EOS-ARCH-RECON-001` (Section 4.I, Section 4.J, Section 4.K, `DEFECT P1-07` & Deliverable #7)  
**Architectural Authority:** ELYZARETH OS — Final Master Blueprint (Sections 9, 10, 15)  
**Decision Authority:** Student-Director / Human Creator  
**Status:** `SPECIFICATION COMPLETE — IMPLEMENTATION FROZEN PENDING APPROVAL`

---

## 1. CRYPTOGRAPHIC HUMAN AUTHORITY & ANDROID KEYSTORE ARCHITECTURE (SECTION 4.I)

### 1.1 Diagnosis of `DEFECT P1-07` in Current Code
In `/core/shared/src/main/java/com/example/shared/SystemUtilitiesAndServices.kt` (`lines 29–35`):
```kotlin
object SovereignSecurityManager {
    val activeSession = SovereignSession()
    fun signPayload(payload: String): String {
        return SovereignKernel.computeSha256Hex("${activeSession.principal}::$payload")
    }
}
```
This computes a plain SHA-256 digest of a public string prefix (`"Student-Director / Human Creator::"`) concatenated with `payload`. It does **not** use `AndroidKeyStore`, does **not** involve a private key, and cannot distinguish a genuine human approval from an automated string concatenation.

---

### 1.2 Target Android Keystore Asymmetric Signature Architecture
1. **Key Generation & Hardware Backing:**
   - Provider: `"AndroidKeyStore"`
   - Algorithm: `KeyProperties.KEY_ALGORITHM_EC` (`ECDSA` over NIST curve `secp256r1` / `P-256`)
   - Digest: `KeyProperties.DIGEST_SHA256` (`SHA256withECDSA`)
   - Key Alias per Role:
     - `com.elyzareth.os.key.HUMAN_CREATOR_V1`
     - `com.elyzareth.os.key.HUMAN_CURATOR_V1`
     - `com.elyzareth.os.key.HUMAN_AUDITOR_V1`
     - `com.elyzareth.os.key.LEDGER_CHECKPOINT_V1`
   - Hardware Security Module: Requests StrongBox (`setIsStrongBoxBacked(true)`) where supported by the Android device, falling back gracefully to the hardware-backed Trusted Execution Environment (`TEE`), and falling back to a software keystore provider strictly inside JVM/Robolectric unit test environments.
2. **7-Tuple Canonical Human Approval Envelope (`HumanSignOffPayload`):**
   Every human gate approval, veto, or G5 release seal must construct and sign a deterministic RFC 8785 canonical JSON envelope binding all 7 mandatory attributes:
   ```json
   {
     "approvingIdentity": "Student-Director / Human Creator",
     "publicKeyFingerprintSha256": "sha256_...",
     "targetProjectId": "PROJ-ELYZ-001",
     "targetArtifactId": "art_1001",
     "targetArtifactVersion": 2,
     "targetArtifactSha256": "sha256_...",
     "gateId": "G1",
     "decisionVerb": "APPROVE",
     "evidenceMerkleRootSha256": "sha256_...",
     "policyVersion": "CONST-1.0::DNA-ELYZ-1.0",
     "approvalTimestampIso": "2026-11-15T14:00:00.000Z"
   }
   ```
3. **Signature Generation & Verification (`SovereignSignatureEngine`):**
   - **Sign:** `Signature.getInstance("SHA256withECDSA").apply { initSign(privateKey); update(canonicalBytes) }.sign()` -> Base64-encoded ECDSA signature (`ecdsaSigBase64`).
   - **Verify:** `Signature.getInstance("SHA256withECDSA").apply { initVerify(certificate.publicKey); update(canonicalBytes) }.verify(Base64.decode(ecdsaSigBase64))`.
   - `SovereignKernel.submitGateDecision(...)` and `ReleaseSealingAuthority.sealMasterRelease(...)` **must** execute `verify(...) == true` before committing any state transition.

---

### 1.3 Session Lifecycle, Key Rotation, Revocation & Device Compromise Boundaries
1. **Authentication vs. Authorization vs. Creative Authority:**
   - *Authentication:* Verifies the active human actor session (`BiometricPrompt` / device credential or explicit Sovereign Session challenge) with a configurable session TTL (`30 minutes` of inactivity before re-authentication is required for G1–G5 signing).
   - *Authorization:* Checks `CapabilityRegistry` to ensure the authenticated `ActorRole` (`HUMAN_CREATOR`, `HUMAN_CURATOR`, `HUMAN_AUDITOR`) is permitted to execute the requested verb on the target gate.
   - *Creative Authority:* Exclusively `HUMAN_CREATOR` possesses G5 release sealing and veto-override authority. Technical database access or system maintenance (`MODULE 13`) never grants `HUMAN_CREATOR` signing authority.
2. **Key Rotation & Key Loss Handling:**
   - Each `HumanSignOffRecord` stores the `publicKeyFingerprintSha256` and exported X.509 public certificate (`publicKeyDerBase64`) alongside the signature in `evidence_ledger`.
   - When a key is rotated (`V1 -> V2`) or a user migrates to a new Android device, historical signatures remain 100% verifiable against their pinned `publicKeyDerBase64` recorded in the ledger chain, while new decisions are signed with the new key after recording a `KEY_ROTATION_AUTHORITY` event.
3. **Honest Device Compromise Disclosure:**
   - On a standard unrooted Android device, the `AndroidKeyStore` private key is non-exportable and protected by TEE/StrongBox.
   - On a compromised/rooted device controlled by an attacker with kernel privileges, local software checks can be hooked. Therefore, Elyzareth OS supports exporting signed `ReleaseManifest` and `EvidenceCheckpoint` bundles off-device so external verification can detect any post-export local database tampering.

---

## 2. BACKUP, RECOVERY & DISASTER HANDLING SPECIFICATION (SECTION 4.J)

### 2.1 Replacing the Prototype `SnapshotBackupManager`
The current `SnapshotBackupManager.generateSnapshot()` (`SystemUtilitiesAndServices.kt:49–76`) only hashes 4 integer counts in memory. The target **Real Backup & Recovery System** implements physical, verifiable archive bundles (`.elyzbak`):

### 2.2 Backup Bundle Structure & Creation Pipeline
1. **Step 1 — Quiesce & Checkpoint SQLite:**
   - Acquire kernel write lock (`GovernorMutex`).
   - Execute `PRAGMA wal_checkpoint(FULL)` on `ElyzarethDatabase` so all WAL pages are flushed into the primary `.db` file.
2. **Step 2 — Assemble Staging Bundle (`files/backup_staging/<snapshotId>/`):**
   - Copy `elyzareth_corpus_vault.db` -> `database/elyzareth_corpus_vault.db` and compute its exact byte-stream `dbSha256`.
   - Export `evidence_ledger` canonical JSONL dump -> `ledger/evidence_ledger.jsonl` and compute `ledgerSha256`.
   - Copy all locally stored media/lyric assets referenced in `asset_registry` -> `assets/<assetId>_<fileName>` and verify each file's `checksumSha256`.
3. **Step 3 — Generate & Sign `BackupSnapshotManifest`:**
   - Record `snapshotId`, `schemaVersion`, `createdAtIso`, `dbSha256`, `ledgerSha256`, per-asset checksum map, row counts (`baseTitlesCount`, `artifactsCount`, `projectsCount`, `evidenceEntriesCount`, `releasesCount`), and `publicKeyFingerprint`.
   - Sign the canonical manifest JSON using the Android Keystore key (`manifestSignatureBase64`).
4. **Step 4 — AES-256-GCM Encryption:**
   - Package the staging directory into a deterministic TAR/ZIP stream and encrypt using `Cipher.getInstance("AES/GCM/NoPadding")` (`256-bit` key managed via Android Keystore `KeyGenerator` or PBKDF2-HMAC-SHA256 user recovery passphrase for cross-device restore, with a random 96-bit IV and 128-bit authentication tag).
5. **Step 5 — Atomic Commit & Cleanup:**
   - Write to `<snapshotId>.elyzbak.tmp`, verify file hash, atomically rename to `<snapshotId>.elyzbak`, delete staging directory, and record `SNAPSHOT_SEALED` in `evidence_ledger`.

### 2.3 Restore Validation & Constitutional Law #12 Enforcement
1. **Staging & Decryption:** Decrypt `<snapshotId>.elyzbak` into an isolated `files/restore_staging/` folder. If GCM authentication tag verification fails, abort immediately (`RESTORE_ABORTED_CORRUPTED_CIPHERTEXT`).
2. **Manifest & Parity Proof:**
   - Verify `manifestSignatureBase64`.
   - Recompute `SHA-256` of `database/elyzareth_corpus_vault.db`, `ledger/evidence_ledger.jsonl`, and every media file in `assets/`, confirming 100% match against `BackupSnapshotManifest`.
   - Open the staged SQLite database read-only and verify that every `RegisteredAssetEntity` row in the staged DB matches the exact `checksumSha256` of the corresponding media file in `assets/` (proving database-to-media state correspondence).
3. **Zero Fabricated Authority (`Constitutional Law #12`):**
   - Run `IntegrityEngine.verifyEvidenceChain()` and `SovereignSignatureEngine.verifyAllGateSignatures()` on the staged database.
   - Any gate approval or release package in the backup lacking a valid cryptographic signature or unbroken hash chain is quarantined (`QUARANTINED_UNVERIFIED_RESTORE`), never upgraded to `APPROVED`.
4. **Interrupted Backup/Restore Safety & Duplicate Prevention:**
   - If the process dies during backup or restore, startup `RecoveryManager` cleans up `backup_staging/` and `restore_staging/` without touching the live database.
   - Restoring a snapshot whose `manifestSha256` matches the currently active database state is detected as a duplicate restore and skipped idempotently.

---

## 3. AI AND EXTERNAL ADAPTER ISOLATION CONTRACT (SECTION 4.K)

### 3.1 Standard Non-Authoritative Adapter Interface (`IsolatedExternalAdapter`)
All external or AI integrations (`ADP-SUNO-01`, `ADP-AI-02`, `ADP-CLOUD-03`, `ADP-MEDIA-04`) must implement a strictly sandboxed contract:

```kotlin
enum class AdapterCapabilityScope {
    DRAFT_LYRIC_PROPOSAL_ONLY,
    ADVISORY_ANALYSIS_ONLY,
    LOCAL_AUDIO_SIGNAL_INSPECTION,
    EXTERNAL_STEM_FILE_IMPORT,
    ENCRYPTED_ARCHIVE_EXPORT
}

interface IsolatedExternalAdapter<Req, Resp> {
    val adapterId: String
    val displayName: String
    val capabilityScope: Set<AdapterCapabilityScope>
    val isOptionalOfflineSafe: Boolean // Must always be true

    suspend fun executeIsolated(request: Req, timeoutMs: Long = 10_000L): AdapterExecutionResult<Resp>
}
```

### 3.2 Six Non-Negotiable Isolation Boundaries
1. **Zero Kernel State Access:** Adapters have no reference to `SovereignKernel` transition methods, `DecisionAuthorityService`, or Room DAOs.
2. **Zero Gate Approval Capability:** Adapters cannot emit `GateDecision` objects or transition `G1..G5`.
3. **Zero Signing Key Access:** Adapters cannot access `AndroidKeyStore` signing aliases (`HUMAN_CREATOR`, `HUMAN_CURATOR`, `HUMAN_AUDITOR`).
4. **Mandatory Provenance Tagging:** Any draft or analysis returned by an adapter is wrapped in an `AdapterExecutionResult` containing `actorType = ActorType.AI`, `adapterId`, `modelIdentity`, `inputPromptSha256`, `outputPayloadSha256`, and `timestampIso`.
5. **Zero Veto Override:** An adapter cannot clear, modify, or retry a `VETOED` or `REJECTED` artifact without human initiation.
6. **100% Offline Graceful Degradation:** When an adapter is disabled or offline (the default operating state of Elyzareth OS), the application uses its built-in deterministic offline engines (App 01 Witness Stanza Synthesizer, App 02 SAF Corpus Curator, Local Trope/N-Gram Scanner, Local WAV/PCM Inspector) with zero loss of governance functionality.
