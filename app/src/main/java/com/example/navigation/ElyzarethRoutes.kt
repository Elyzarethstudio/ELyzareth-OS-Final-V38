package com.example.navigation

/**
 * ELYZARETH OS — AUTHORITATIVE NAVIGATION SYSTEM.
 * Reference: ELYZARETH_OS_STRUCTURE_1.0 & Complete UI/UX Suite v1.0
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
        routeId = "command_centre",
        badgeNumber = "00",
        title = "Command Centre",
        shortTitle = "Command Centre",
        subtitle = "Main Dashboard • Sovereign Control & 14-Stage Pipeline",
        architecturalLayer = "Application Shell"
    ),
    IDEA_BLUEPRINT(
        routeId = "idea_blueprint",
        badgeNumber = "01",
        title = "1. Idea & Blueprint Studio",
        shortTitle = "Idea & Blueprint",
        subtitle = "Concept • Intent • Structure • Plan",
        architecturalLayer = "Creative Layer"
    ),
    LYRIC_GENERATOR(
        routeId = "lyric_generator",
        badgeNumber = "02",
        title = "2. Lyric Generator",
        shortTitle = "Lyric Generator",
        subtitle = "Drafts • Variations • Verse • Chorus (App 01)",
        architecturalLayer = "Creative Layer"
    ),
    LYRIC_CURATOR(
        routeId = "lyric_curator",
        badgeNumber = "03",
        title = "3. Lyric Curator",
        shortTitle = "Lyric Curator",
        subtitle = "Edit • Refine • Emotional Logic • Originality",
        architecturalLayer = "Creative Layer"
    ),
    CREATIVE_DNA(
        routeId = "creative_dna",
        badgeNumber = "04",
        title = "4. Creative DNA Engine",
        shortTitle = "Creative DNA",
        subtitle = "Identity • Style • Conformance • Imagery Control",
        architecturalLayer = "Creative Layer"
    ),
    MUSIC_PRODUCTION(
        routeId = "music_production",
        badgeNumber = "05",
        title = "5. Music Production Console",
        shortTitle = "Music Production",
        subtitle = "Style • Suno Ingest • Stems • Production Notes",
        architecturalLayer = "Production Layer"
    ),
    VISUAL_DIRECTION(
        routeId = "visual_direction",
        badgeNumber = "06",
        title = "6. Visual Direction Studio",
        shortTitle = "Visual Direction",
        subtitle = "Artwork • Visuals • Promotional Assets",
        architecturalLayer = "Production Layer"
    ),
    CORPUS_ARCHIVE(
        routeId = "corpus_archive",
        badgeNumber = "07",
        title = "7. Corpus & Archive",
        shortTitle = "Corpus & Archive",
        subtitle = "Song Catalogue • Versions • Search • Archive (App 02)",
        architecturalLayer = "Corpus & Archive"
    ),
    FORENSIC_LAB(
        routeId = "forensic_lab",
        badgeNumber = "08",
        title = "8. Forensic Lab",
        shortTitle = "Forensic Lab",
        subtitle = "Similarity • Audio Integrity • Collision Reports",
        architecturalLayer = "Intelligence Layer"
    ),
    GOVERNANCE_CONSOLE(
        routeId = "governance_console",
        badgeNumber = "09",
        title = "9. Governance Console",
        shortTitle = "Governance",
        subtitle = "G1–G5 Approvals • 3.2.1.0 Protocol • Release Control",
        architecturalLayer = "Governance Layer"
    ),
    EVIDENCE_VAULT(
        routeId = "evidence_vault",
        badgeNumber = "10",
        title = "10. Evidence Vault",
        shortTitle = "Evidence Vault",
        subtitle = "Provenance • Hash • History • Integrity",
        architecturalLayer = "Intelligence Layer"
    ),
    ASSET_REGISTRY(
        routeId = "asset_registry",
        badgeNumber = "11",
        title = "11. Asset Registry",
        shortTitle = "Asset Registry",
        subtitle = "Register Asset • Audio/Lyric/Artwork • Checksums",
        architecturalLayer = "Production Layer"
    ),
    MASTER_VAULT(
        routeId = "master_vault",
        badgeNumber = "12",
        title = "12. Master Vault & Player",
        shortTitle = "Master Vault",
        subtitle = "Authorized Release • Sealed Package • Playback",
        architecturalLayer = "Release & Master Vault"
    ),
    SYSTEM_SERVICES(
        routeId = "system_services",
        badgeNumber = "13",
        title = "13. System Services",
        shortTitle = "System Services",
        subtitle = "Database • Backup • Security • Background • Health",
        architecturalLayer = "Supporting System Branches"
    ),
    PROJECT_OVERVIEW(
        routeId = "project_overview",
        badgeNumber = "14",
        title = "14. Project & System Overview",
        shortTitle = "Project Overview",
        subtitle = "Active Projects • Total Songs • Pipeline Progress",
        architecturalLayer = "Analytics & System Health"
    ),
    SOVEREIGN_KERNEL(
        routeId = "sovereign_kernel",
        badgeNumber = "SK",
        title = "Sovereign Kernel & Constitutional Laws",
        shortTitle = "Sovereign Kernel",
        subtitle = "Governor State Machine • Constitutional Laws • End-to-End Factory",
        architecturalLayer = "Kernel & Foundation Layer"
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
