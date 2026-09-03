export type ArtifactStatus = 
  | 'DISCOVERED'
  | 'PARSED'
  | 'VERIFIED'
  | 'UNVERIFIED'
  | 'FAILED'
  | 'NOT MEASURED';

export interface ArtifactEvidence {
  fileHash: string;
  sizeBytes: number;
  lastModifiedMs: number;
  extension: string;
  detectedLanguage?: string;
  hasPhysicalWitnessAnchors?: boolean;
  parseError?: string;
  checksumMatch: boolean;
}

export interface CorpusArtifact {
  id: string;
  path: string;
  filename: string;
  baseTitleSuggested: string;
  versionLabel: string;
  artifactType: 'lyric_txt' | 'lyric_json' | 'metadata' | 'audio_reference' | 'witness_manifest';
  status: ArtifactStatus;
  evidence: ArtifactEvidence;
  contentPreview?: string;
  isOrphan: boolean;
  isDuplicateCandidate: boolean;
  requiresHumanReview: boolean;
  reviewReason?: string;
}

export interface BaseTitleGroup {
  id: string;
  baseTitle: string;
  artifactsCount: number;
  artifacts: CorpusArtifact[];
  primaryLanguage: string;
  hasLyrics: boolean;
  hasMetadata: boolean;
  hasAudioRef: boolean;
  containsAmbiguity: boolean;
  g1Score: 'NOT MEASURED' | 'VERIFIED' | 'UNVERIFIED';
  g2Score: 'NOT MEASURED' | 'VERIFIED' | 'UNVERIFIED';
  g3Score: 'NOT MEASURED' | 'VERIFIED' | 'UNVERIFIED';
  g4Score: 'NOT MEASURED' | 'VERIFIED' | 'UNVERIFIED';
  g5Score: 'NOT MEASURED' | 'VERIFIED' | 'UNVERIFIED';
}

export interface IngestionReport {
  timestamp: string;
  totalFilesDiscovered: number;
  baseTitlesDiscovered: number;
  totalArtifactsDiscovered: number;
  successfullyParsed: number;
  unparsed: number;
  duplicateCandidates: number;
  orphanArtifacts: number;
  missingExpectedComponents: number;
  languagesDetected: Record<string, number>;
  evidenceAvailableCount: number;
  evidenceUnavailableCount: number;
  itemsRequiringHumanReview: number;
}
