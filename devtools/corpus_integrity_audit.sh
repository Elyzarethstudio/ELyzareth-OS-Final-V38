#!/usr/bin/env bash
# ELYZARETH OS v1.0 — Development Tools: Repository & Structural Integrity Audit
set -e

echo "=== ELYZARETH OS v1.0 STRUCTURAL AUDIT ==="
echo "Checking Gradle modules in settings.gradle.kts..."
grep -E 'include\(":app"\)|include\(":core:foundation"\)|include\(":core:kernel"\)|include\(":core:shared"\)' settings.gradle.kts

echo "Checking Master Progress Register..."
test -f documentation/implementation-progress/MASTER_PROGRESS_REGISTER.md && echo "MASTER_PROGRESS_REGISTER.md: PRESENT"

echo "Checking TSX Source Mapping Register..."
test -f documentation/architecture/TSX_SOURCE_MAPPING_REGISTER.md && echo "TSX_SOURCE_MAPPING_REGISTER.md: PRESENT"

echo "=== AUDIT PASSED ==="
