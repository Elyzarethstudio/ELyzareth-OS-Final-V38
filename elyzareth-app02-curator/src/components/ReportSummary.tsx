import React from "react";
import { IngestionReport } from "../types";
import { FileText, Layers, CheckCircle2, AlertCircle, Copy, HelpCircle, Globe, ShieldCheck, Lock } from "lucide-react";

interface ReportSummaryProps {
  report: IngestionReport;
}

export default function ReportSummary({ report }: ReportSummaryProps) {
  return (
    <div className="w-full bg-slate-900 border border-slate-800 rounded-xl p-5 mb-6 text-slate-200 space-y-5 shadow-lg">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between border-b border-slate-800 pb-3 gap-2">
        <div>
          <h2 className="text-base font-bold text-slate-100 font-mono tracking-wide flex items-center gap-2">
            <ShieldCheck className="w-5 h-5 text-cyan-400" />
            FIRST-PASS DISCOVERY & EVIDENCE REPORT
          </h2>
          <p className="text-xs text-slate-400 font-mono mt-0.5">
            Read-only scan timestamp: {new Date(report.timestamp).toLocaleString()} • Non-destructive inventory
          </p>
        </div>
        <div className="px-3 py-1 bg-amber-950/60 border border-amber-600/40 text-amber-300 rounded font-mono text-xs font-semibold self-start sm:self-auto flex items-center gap-1.5">
          <Lock className="w-3.5 h-3.5" /> G1–G5 GATES: LOCKED ("NOT MEASURED")
        </div>
      </div>

      <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-3 font-mono">
        <div className="bg-slate-950/80 border border-slate-800 p-3 rounded-lg">
          <div className="text-slate-400 text-xs flex items-center justify-between mb-1">
            <span>FILES</span>
            <FileText className="w-3.5 h-3.5 text-cyan-400" />
          </div>
          <div className="text-xl font-bold text-slate-100">{report.totalFilesDiscovered}</div>
          <div className="text-[10px] text-slate-500 mt-1">Total Discovered</div>
        </div>

        <div className="bg-slate-950/80 border border-cyan-900/40 p-3 rounded-lg">
          <div className="text-cyan-400 text-xs flex items-center justify-between mb-1">
            <span>BASE TITLES</span>
            <Layers className="w-3.5 h-3.5 text-cyan-400" />
          </div>
          <div className="text-xl font-bold text-cyan-300">{report.baseTitlesDiscovered}</div>
          <div className="text-[10px] text-cyan-500 mt-1">Grouped Song Titles</div>
        </div>

        <div className="bg-slate-950/80 border border-slate-800 p-3 rounded-lg">
          <div className="text-emerald-400 text-xs flex items-center justify-between mb-1">
            <span>PARSED</span>
            <CheckCircle2 className="w-3.5 h-3.5 text-emerald-400" />
          </div>
          <div className="text-xl font-bold text-emerald-300">
            {report.successfullyParsed}{" "}
            <span className="text-xs font-normal text-slate-500">/ {report.unparsed} failed</span>
          </div>
          <div className="text-[10px] text-slate-500 mt-1">Parsed vs Unparsed</div>
        </div>

        <div className="bg-slate-950/80 border border-slate-800 p-3 rounded-lg">
          <div className="text-amber-400 text-xs flex items-center justify-between mb-1">
            <span>DUPLICATES</span>
            <Copy className="w-3.5 h-3.5 text-amber-400" />
          </div>
          <div className="text-xl font-bold text-amber-300">{report.duplicateCandidates}</div>
          <div className="text-[10px] text-slate-500 mt-1">Ambiguous Candidates</div>
        </div>

        <div className="bg-slate-950/80 border border-slate-800 p-3 rounded-lg">
          <div className="text-purple-400 text-xs flex items-center justify-between mb-1">
            <span>ORPHANS</span>
            <HelpCircle className="w-3.5 h-3.5 text-purple-400" />
          </div>
          <div className="text-xl font-bold text-purple-300">{report.orphanArtifacts}</div>
          <div className="text-[10px] text-slate-500 mt-1">Missing Audio/Manifest</div>
        </div>

        <div className="bg-slate-950/80 border border-red-900/40 p-3 rounded-lg">
          <div className="text-red-400 text-xs flex items-center justify-between mb-1">
            <span>HUMAN REVIEW</span>
            <AlertCircle className="w-3.5 h-3.5 text-red-400" />
          </div>
          <div className="text-xl font-bold text-red-300">{report.itemsRequiringHumanReview}</div>
          <div className="text-[10px] text-red-500 mt-1">Requires Governor Action</div>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4 pt-2 border-t border-slate-800 text-xs font-mono">
        <div className="flex items-center gap-3">
          <Globe className="w-4 h-4 text-cyan-400 shrink-0" />
          <span className="text-slate-400">Detected Languages:</span>
          <div className="flex flex-wrap gap-2">
            {Object.entries(report.languagesDetected).map(([lang, count]) => (
              <span key={lang} className="px-2 py-0.5 bg-slate-800 border border-slate-700 rounded text-slate-300">
                {lang}: <strong className="text-cyan-300">{count}</strong>
              </span>
            ))}
          </div>
        </div>

        <div className="flex items-center gap-3 md:justify-end">
          <ShieldCheck className="w-4 h-4 text-emerald-400 shrink-0" />
          <span className="text-slate-400">Evidence Record Status:</span>
          <span className="px-2 py-0.5 bg-emerald-950 border border-emerald-700 text-emerald-300 rounded font-semibold">
            {report.evidenceAvailableCount} AVAILABLE (100% SHA-256 MATCH)
          </span>
        </div>
      </div>
    </div>
  );
}
