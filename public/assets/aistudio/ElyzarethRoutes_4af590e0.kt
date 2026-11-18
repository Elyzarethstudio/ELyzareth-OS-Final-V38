package com.example.navigation

/**
 * ELYZARETH OS — AUTHORITATIVE NAVIGATION SYSTEM.
 *
 * Canonical route registry for the V38 application shell.
 * Keep routeId values stable: they are navigation identifiers, not display labels.
 */
enum class OsModuleDestination(
    val routeId: String,
    val badgeNumber: String,
    val title: String,
    val shortTitle: String,
    val subtitle: String,
    val architecturalLayer: String
) {
    COMMAND_CENTRE(
        "command_centre", "00", "Command Centre", "Command Centre",
        "Main Dashboard • Sovereign Control & 14-Stage Pipeline", "Application Shell"
    ),
    IDEA_BLUEPRINT(
        "idea_blueprint", "01", "1. Idea & Blueprint Studio", "Idea & Blueprint",
        "Concept • Intent • Structure • Plan", "Creative Layer"
    ),
    LYRIC_GENERATOR(
        "lyric_generator", "02", "2. Lyric Generator", "Lyric Generator",
        "Drafts • Variations • Verse • Chorus (App 01)", "Creative Layer"
    ),
    LYRIC_CURATOR(
        "lyric_curator", "03", "3. Lyric Curator", "Lyric Curator",
        "Edit • Refine • Emotional Logic • Originality", "Creative Layer"
    ),
    CREATIVE_DNA(
        "creative_dna", "04", "4. Creative DNA Engine", "Creative DNA",
        "Identity • Style • Conformance • Imagery Control", "Creative Layer"
    ),
    MUSIC_PRODUCTION(
        "music_production", "05", "5. Music Production Console", "Music Production",
        "Style • Suno Ingest • Stems • Production Notes", "Production Layer"
    ),
    VISUAL_DIRECTION(
        "visual_direction", "06", "6. Visual Direction Studio", "Visual Direction",
        "Artwork • Visuals • Promotional Assets", "Production Layer"
    ),
    CORPUS_ARCHIVE(
        "corpus_archive", "07", "7. Corpus & Archive", "Corpus & Archive",
        "Song Catalogue • Versions • Search • Archive (App 02)", "Corpus & Archive"
    ),
    FORENSIC_LAB(
        "forensic_lab", "08", "8. Forensic Lab", "Forensic Lab",
        "Similarity • Audio Integrity • Collision Reports", "Intelligence Layer"
    ),
    GOVERNANCE_CONSOLE(
        "governance_console", "09", "9. Governance Console", "Governance",
        "G1–G5 Approvals • 3.2.1.0 Protocol • Release Control", "Governance Layer"
    ),
    EVIDENCE_VAULT(
        "evidence_vault", "10", "10. Evidence Vault", "Evidence Vault",
        "Provenance • Hash • History • Integrity", "Intelligence Layer"
    ),
    ASSET_REGISTRY(
        "asset_registry", "11", "11. Asset Registry", "Asset Registry",
        "Register Asset • Audio/Lyric/Artwork • Checksums", "Production Layer"
    ),
    MASTER_VAULT(
        "master_vault", "12", "12. Master Vault & Player", "Master Vault",
        "Authorized Release • Sealed Package • Playback", "Release & Master Vault"
    ),
    SYSTEM_SERVICES(
        "system_services", "13", "13. System Services", "System Services",
        "Database • Backup • Security • Background • Health", "Supporting System Branches"
    ),
    PROJECT_OVERVIEW(
        "project_overview", "14", "14. Project & System Overview", "Project Overview",
        "Active Projects • Total Songs • Pipeline Progress", "Analytics & System Health"
    ),
    SOVEREIGN_KERNEL(
        "sovereign_kernel", "SK", "Sovereign Kernel & Constitutional Laws", "Sovereign Kernel",
        "Governor State Machine • Constitutional Laws • End-to-End Factory",
        "Kernel & Foundation Layer"
    );

    companion object {
        /** The twelve primary module cards shown in the Command Centre grid. */
        val COMMAND_CENTRE_GRID_MODULES: List<OsModuleDestination> = listOf(
            IDEA_BLUEPRINT,
            LYRIC_GENERATOR,
            LYRIC_CURATOR,
            CREATIVE_DNA,
            MUSIC_PRODUCTION,
            VISUAL_DIRECTION,
            CORPUS_ARCHIVE,
            FORENSIC_LAB,
            GOVERNANCE_CONSOLE,
            EVIDENCE_VAULT,
            ASSET_REGISTRY,
            MASTER_VAULT
        )
    }
}
