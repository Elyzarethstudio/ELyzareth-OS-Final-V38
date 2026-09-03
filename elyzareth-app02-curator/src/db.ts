import { CorpusArtifact, BaseTitleGroup, IngestionReport } from './types';

const STORAGE_KEY_REPORT = 'elyzareth_app02_report_v1';
const STORAGE_KEY_TITLES = 'elyzareth_app02_titles_v1';
const STORAGE_KEY_ARTIFACTS = 'elyzareth_app02_artifacts_v1';
const STORAGE_KEY_LAST_SCAN = 'elyzareth_app02_last_scan';

export function saveCorpusData(
  report: IngestionReport,
  titles: BaseTitleGroup[],
  artifacts: CorpusArtifact[]
): void {
  try {
    localStorage.setItem(STORAGE_KEY_REPORT, JSON.stringify(report));
    localStorage.setItem(STORAGE_KEY_TITLES, JSON.stringify(titles));
    localStorage.setItem(STORAGE_KEY_ARTIFACTS, JSON.stringify(artifacts));
    localStorage.setItem(STORAGE_KEY_LAST_SCAN, new Date().toISOString());
  } catch (err) {
    console.warn('LocalStorage save limit reached or restricted', err);
  }
}

export function loadCorpusData(): {
  report: IngestionReport | null;
  titles: BaseTitleGroup[];
  artifacts: CorpusArtifact[];
  lastScan: string | null;
} {
  try {
    const reportStr = localStorage.getItem(STORAGE_KEY_REPORT);
    const titlesStr = localStorage.getItem(STORAGE_KEY_TITLES);
    const artifactsStr = localStorage.getItem(STORAGE_KEY_ARTIFACTS);
    const lastScan = localStorage.getItem(STORAGE_KEY_LAST_SCAN);

    return {
      report: reportStr ? JSON.parse(reportStr) : null,
      titles: titlesStr ? JSON.parse(titlesStr) : [],
      artifacts: artifactsStr ? JSON.parse(artifactsStr) : [],
      lastScan: lastScan || null,
    };
  } catch (err) {
    console.error('Failed to load persisted corpus data', err);
    return { report: null, titles: [], artifacts: [], lastScan: null };
  }
}

export function clearCorpusData(): void {
  try {
    localStorage.removeItem(STORAGE_KEY_REPORT);
    localStorage.removeItem(STORAGE_KEY_TITLES);
    localStorage.removeItem(STORAGE_KEY_ARTIFACTS);
    localStorage.removeItem(STORAGE_KEY_LAST_SCAN);
  } catch (err) {
    console.warn('LocalStorage clear error', err);
  }
}
