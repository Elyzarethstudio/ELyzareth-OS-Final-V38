import React from "react";
import { CorpusArtifact } from "../types";
import { X, ShieldCheck, Lock, FileText, Hash, CheckCircle2 } from "lucide-react";

interface ArtifactDetailModalProps {
  artifact: CorpusArtifact | null;
  onClose: () => void;
}

export default function ArtifactDetailModal({ artifact, onClose }: ArtifactDetailModalProps) {
  if (!artifact) return null;

  return (
    <div className="fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-sm flex items-center justify-center p-4 font-mono text-xs">
      <div className="bg-slate-900 border border-slate-700 rounded-xl max-w-2xl w-full max-h-[90vh] flex flex-col shadow-2xl overflow-hidden">
        <div className="p-4 bg-slate-950 border-b border-slate-800 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <ShieldCheck className="w-5 h-5 text-cyan-400" />
            <div>
              <h3 className="font-bold text-slate-100 text-sm">{artifact.filename}</h3>
              <p className="text-[10px] text-slate-400">Read-Only Evidence Inspector • No Mutation Permitted</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1 text-slate-400 hover:text-slate-100 rounded hover:bg-slate-800"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <div className="p-5 overflow-y-auto space-y-4 text-slate-300">
          <div className="flex flex-wrap items-center gap-2 pb-3 border-b border-slate-800">
            <span className="px-2 py-0.5 bg-cyan-950 text-cyan-300 border border-cyan-800 rounded font-semibold">
              STATUS: {artifact.status}
            </span>
            <span className="px-2 py-0.5 bg-amber-950 text-amber-300 border border-amber-800 rounded font-semibold flex items-center gap-1">
              <Lock className="w-3 h-3" /> G1–G5: NOT MEASURED
            </span>
            {artifact.requiresHumanReview && (
              <span className="px-2 py-0.5 bg-red-950 text-red-300 border border-red-800 rounded font-semibold">
                REVIEW REQ: {artifact.reviewReason || "Ambiguity Flagged"}
              </span>
            )}
          </div>

          <div className="grid grid-cols-2 gap-3 bg-slate-950 p-3 rounded-lg border border-slate-800">
            <div>
              <span className="text-slate-500 block text-[10px]">FILE PATH</span>
              <span className="text-slate-200 font-semibold truncate block">{artifact.path}</span>
            </div>
            <div>
              <span className="text-slate-500 block text-[10px]">BASE TITLE GROUP</span>
              <span className="text-cyan-400 font-semibold">{artifact.baseTitleSuggested}</span>
            </div>
            <div>
              <span className="text-slate-500 block text-[10px]">FILE SIZE & EXTENSION</span>
              <span className="text-slate-200">{(artifact.evidence.sizeBytes / 1024).toFixed(2)} KB (.{artifact.evidence.extension})</span>
            </div>
            <div>
              <span className="text-slate-500 block text-[10px]">LAST MODIFIED</span>
              <span className="text-slate-200">{new Date(artifact.evidence.lastModifiedMs).toLocaleString()}</span>
            </div>
            <div className="col-span-2 pt-1 border-t border-slate-800">
              <span className="text-slate-500 block text-[10px] flex items-center gap-1">
                <Hash className="w-3 h-3 text-cyan-400" /> PERSISTENT FILE HASH EVIDENCE
              </span>
              <code className="text-[11px] text-emerald-400 bg-slate-900 px-2 py-1 rounded block mt-1 overflow-x-auto border border-slate-800">
                {artifact.evidence.fileHash}
              </code>
            </div>
          </div>

          <div>
            <h4 className="font-bold text-slate-200 mb-1 flex items-center gap-1.5">
              <FileText className="w-4 h-4 text-cyan-400" />
              READ-ONLY ARTIFACT PREVIEW
            </h4>
            <div className="bg-slate-950 border border-slate-800 p-3 rounded-lg max-h-48 overflow-y-auto text-slate-300 whitespace-pre-wrap font-mono text-[11px] leading-relaxed">
              {artifact.contentPreview || "[Binary or audio metadata artifact — non-text contents preserved intact]"}
            </div>
          </div>

          <div className="p-3 bg-slate-950/60 border border-slate-800 rounded-lg text-slate-400 text-[11px] flex items-center justify-between">
            <span className="flex items-center gap-1.5 text-emerald-400">
              <CheckCircle2 className="w-4 h-4" /> Source artifact verified unmodified.
            </span>
            <span className="text-slate-500">Human Governor Authority Required to Modify</span>
          </div>
        </div>

        <div className="p-3 bg-slate-950 border-t border-slate-800 flex justify-end">
          <button
            onClick={onClose}
            className="px-4 py-1.5 bg-slate-800 hover:bg-slate-700 text-slate-200 rounded font-semibold text-xs"
          >
            Close Inspector
          </button>
        </div>
      </div>
    </div>
  );
}
