import React, { useState, useEffect } from "react";
import { IngestionReport, BaseTitleGroup, CorpusArtifact } from "./types";
import { loadCorpusData, saveCorpusData, clearCorpusData } from "./db";
import { generateRealCorpusDryRun } from "./mockCorpus";
import { parseDiscoveredFileList } from "./parser";
import Header from "./components/Header";
import ReportSummary from "./components/ReportSummary";
import CorpusTable from "./components/CorpusTable";
import ArtifactDetailModal from "./components/ArtifactDetailModal";
import { Play, Download, Database, RefreshCw } from "lucide-react";

export default function Component() {
  const [report, setReport] = useState<IngestionReport | null>(null);
  const [titles, setTitles] = useState<BaseTitleGroup[]>([]);
  const [artifacts, setArtifacts] = useState<CorpusArtifact[]>([]);
  const [lastScan, setLastScan] = useState<string | null>(null);
  const [selectedArtifact, setSelectedArtifact] = useState<CorpusArtifact | null>(null);
  const [isProcessing, setIsProcessing] = useState<boolean>(false);
  const [protocolActivated, setProtocolActivated] = useState<boolean>(false);

  useEffect(() => {
    const persisted = loadCorpusData();
    if (persisted.report && persisted.titles.length > 0) {
      setReport(persisted.report);
      setTitles(persisted.titles);
      setArtifacts(persisted.artifacts);
      setLastScan(persisted.lastScan);
    } else {
      handleSimulateDryRun();
    }
  }, []);

  const handleSimulateDryRun = () => {
    setIsProcessing(true);
    setTimeout(() => {
      const data = generateRealCorpusDryRun();
      setReport(data.report);
      setTitles(data.titles);
      setArtifacts(data.artifacts);
      const now = new Date().toISOString();
      setLastScan(now);
      saveCorpusData(data.report, data.titles, data.artifacts);
      setIsProcessing(false);
    }, 400);
  };

  const handleSelectDirectory = async (files: FileList) => {
    setIsProcessing(true);
    try {
      const data = await parseDiscoveredFileList(files);
      setReport(data.report);
      setTitles(data.titles);
      setArtifacts(data.artifacts);
      const now = new Date().toISOString();
      setLastScan(now);
      saveCorpusData(data.report, data.titles, data.artifacts);
    } catch (e) {
      console.error("Folder scan failed", e);
    } finally {
      setIsProcessing(false);
    }
  };

  const handleClearData = () => {
    clearCorpusData();
    setReport(null);
    setTitles([]);
    setArtifacts([]);
    setLastScan(null);
  };

  const handleExportManifest = () => {
    if (!report) return;
    const jsonStr = JSON.stringify({ report, titles, artifacts }, null, 2);
    const blob = new Blob([jsonStr], { type: "application/json" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = `elyzareth_corpus_discovery_manifest_${Date.now()}.json`;
    a.click();
    URL.revokeObjectURL(url);
  };

  return (
    <div className="min-h-screen w-full bg-slate-950 text-slate-100 font-sans flex flex-col items-center justify-start antialiased selection:bg-cyan-500 selection:text-slate-950">
      <div className="w-full max-w-7xl flex flex-col min-h-screen border-x border-slate-900 shadow-2xl bg-slate-950">
        <Header
          onSelectDirectory={handleSelectDirectory}
          onSimulateDryRun={handleSimulateDryRun}
          onClearData={handleClearData}
          lastScanTimestamp={lastScan}
          totalTitles={titles.length}
          totalArtifacts={artifacts.length}
        />

        <main className="flex-1 p-4 md:p-6 space-y-6">
          <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3 bg-slate-900 border border-slate-800 p-3.5 rounded-xl font-mono text-xs">
            <div className="flex items-center gap-2">
              <span className="w-2 h-2 rounded-full bg-emerald-500 animate-ping"></span>
              <span className="text-slate-300 font-semibold">HUMAN GOVERNOR:</span>
              <span className="text-slate-400">Read-Only Discovery & Evidence Phase</span>
            </div>

            <div className="flex items-center gap-2 w-full sm:w-auto">
              <button
                onClick={handleExportManifest}
                disabled={!report}
                className="px-3.5 py-1.5 rounded-lg font-semibold flex items-center gap-1.5 transition-all border bg-slate-800 hover:bg-slate-700 text-cyan-300 border-slate-700"
                title="Export compressed JSON manifest for forensic verification"
              >
                <Download className="w-3.5 h-3.5 text-cyan-400" />
                <span>Export Corpus Manifest</span>
              </button>

              <button
                onClick={() => setProtocolActivated(true)}
                disabled={!report}
                className={`px-3.5 py-1.5 rounded-lg font-semibold flex items-center gap-1.5 transition-all border ${
                  protocolActivated
                    ? "bg-emerald-950 text-emerald-300 border-emerald-700"
                    : "bg-slate-800 hover:bg-cyan-950 hover:text-cyan-300 text-slate-300 border-slate-700"
                }`}
              >
                <Play className="w-3.5 h-3.5" />
                <span>
                  {protocolActivated
                    ? "3.2.1.0 Protocol Ready (Listen → Evaluate → Decide → Freeze)"
                    : "Prepare 3.2.1.0 Protocol"}
                </span>
              </button>
            </div>
          </div>

          {isProcessing ? (
            <div className="w-full py-16 bg-slate-900 border border-slate-800 rounded-xl flex flex-col items-center justify-center gap-3 font-mono">
              <RefreshCw className="w-8 h-8 text-cyan-400 animate-spin" />
              <div className="text-slate-200 font-bold">SCANNING & INGESTING CORPUS EVIDENCE...</div>
              <div className="text-xs text-slate-500">Computing SHA-256 hashes & grouping base titles</div>
            </div>
          ) : report ? (
            <>
              <ReportSummary report={report} />
              <CorpusTable
                titles={titles}
                onSelectArtifact={(art) => setSelectedArtifact(art)}
              />
            </>
          ) : (
            <div className="w-full py-20 bg-slate-900 border border-slate-800 rounded-xl flex flex-col items-center justify-center gap-4 text-center p-6 font-mono">
              <Database className="w-12 h-12 text-slate-600" />
              <div>
                <h3 className="text-base font-bold text-slate-200">NO CORPUS DISCOVERY DATA</h3>
                <p className="text-xs text-slate-500 mt-1 max-w-md">
                  Select your local corpus directory using your browser or run the 510 artifact real corpus discovery baseline.
                </p>
              </div>
              <button
                onClick={handleSimulateDryRun}
                className="px-5 py-2.5 bg-cyan-600 hover:bg-cyan-500 text-white rounded-lg font-semibold text-xs transition-all shadow-lg"
              >
                Run 510 Artifact Real Corpus Baseline
              </button>
            </div>
          )}
        </main>

        <footer className="p-4 bg-slate-950 border-t border-slate-900 font-mono text-[11px] text-slate-500 flex flex-col sm:flex-row items-center justify-between gap-2">
          <div>
            ELYZARETH OS v38.1 • Governance Rule: Zero-Mutation Ingestion Pass
          </div>
          <div className="flex items-center gap-3 text-slate-400">
            <span>Stop Condition Active</span>
            <span>•</span>
            <span className="text-emerald-400 font-semibold">Human Authority Intact</span>
          </div>
        </footer>
      </div>

      <ArtifactDetailModal
        artifact={selectedArtifact}
        onClose={() => setSelectedArtifact(null)}
      />
    </div>
  );
}

// Support Vite entry point direct mounting to #root if loaded directly in browser
if (typeof document !== 'undefined') {
  const rootElement = document.getElementById('root');
  if (rootElement && !rootElement.hasChildNodes()) {
    import('react-dom/client').then(({ createRoot }) => {
      createRoot(rootElement).render(
        <React.StrictMode>
          <Component />
        </React.StrictMode>
      );
    });
  }
}
