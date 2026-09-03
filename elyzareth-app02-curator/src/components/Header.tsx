import React from "react";
import { Shield, FolderOpen, Database, RefreshCw, Eye, Lock } from "lucide-react";

interface HeaderProps {
  onSelectDirectory: (files: FileList) => void;
  onSimulateDryRun: () => void;
  onClearData: () => void;
  lastScanTimestamp: string | null;
  totalTitles: number;
  totalArtifacts: number;
}

export default function Header({
  onSelectDirectory,
  onSimulateDryRun,
  onClearData,
  lastScanTimestamp,
  totalTitles,
  totalArtifacts,
}: HeaderProps) {
  const fileInputRef = React.useRef<HTMLInputElement>(null);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      onSelectDirectory(e.target.files);
    }
  };

  return (
    <header className="w-full bg-slate-950 border-b border-slate-800 p-4 text-slate-100 flex flex-col md:flex-row md:items-center justify-between gap-4">
      <div className="flex items-center gap-3">
        <div className="p-2.5 bg-cyan-950/60 border border-cyan-500/40 rounded-lg text-cyan-400 flex items-center justify-center shadow-inner">
          <Shield className="w-6 h-6 animate-pulse" />
        </div>
        <div>
          <div className="flex items-center gap-2">
            <h1 className="text-lg font-bold tracking-tight text-slate-100 uppercase">
              Elyzareth OS v38.1
            </h1>
            <span className="px-2 py-0.5 text-xs font-mono font-semibold bg-cyan-950 text-cyan-300 border border-cyan-700/50 rounded">
              APP 02 — LYRIC CURATOR
            </span>
          </div>
          <div className="flex items-center gap-2 text-xs text-slate-400 font-mono mt-0.5">
            <span className="flex items-center gap-1 text-amber-400 font-semibold">
              <Eye className="w-3.5 h-3.5" /> READ-ONLY DRY RUN
            </span>
            <span>•</span>
            <span className="text-emerald-400 flex items-center gap-1">
              <Lock className="w-3.5 h-3.5" /> HUMAN GOVERNOR ACTIVE
            </span>
            <span>•</span>
            <span>NO MUTATION</span>
          </div>
        </div>
      </div>

      <div className="hidden lg:flex items-center gap-4 text-xs font-mono bg-slate-900 px-3 py-1.5 rounded-lg border border-slate-800 text-slate-400">
        <div>
          <span className="text-slate-500">CORPUS PERSISTENCE:</span>{" "}
          <span className="text-slate-200 font-medium">STORAGE LOCKED</span>
        </div>
        <span>|</span>
        <div>
          <span className="text-slate-500">LAST SCAN:</span>{" "}
          <span className="text-slate-300">
            {lastScanTimestamp ? new Date(lastScanTimestamp).toLocaleTimeString() : "NOT STARTED"}
          </span>
        </div>
        {totalTitles > 0 && (
          <>
            <span>|</span>
            <div>
              <span className="text-cyan-400 font-bold">{totalTitles}</span> TITLES /{" "}
              <span className="text-cyan-400 font-bold">{totalArtifacts}</span> ARTIFACTS
            </div>
          </>
        )}
      </div>

      <div className="flex items-center gap-2">
        <input
          type="file"
          ref={fileInputRef}
          onChange={handleFileChange}
          // @ts-ignore
          webkitdirectory=""
          directory=""
          multiple
          className="hidden"
        />

        <button
          onClick={() => fileInputRef.current?.click()}
          className="px-3.5 py-2 bg-slate-800 hover:bg-slate-700 text-slate-200 border border-slate-700 rounded-lg font-mono text-xs font-medium flex items-center gap-2 transition-colors"
        >
          <FolderOpen className="w-4 h-4 text-cyan-400" />
          <span>Select Folder</span>
        </button>

        <button
          onClick={onSimulateDryRun}
          className="px-4 py-2 bg-cyan-600 hover:bg-cyan-500 text-white font-mono text-xs font-semibold rounded-lg shadow-md flex items-center gap-2 transition-all border border-cyan-400/30"
        >
          <Database className="w-4 h-4 text-cyan-100" />
          <span>Corpus Baseline (510 Artifacts)</span>
        </button>

        {totalTitles > 0 && (
          <button
            onClick={onClearData}
            className="p-2 text-slate-400 hover:text-red-400 hover:bg-red-950/40 border border-slate-800 rounded-lg transition-colors"
            title="Clear scan persistence"
          >
            <RefreshCw className="w-4 h-4" />
          </button>
        )}
      </div>
    </header>
  );
}
