import React, { useState } from "react";
import { BaseTitleGroup, CorpusArtifact } from "../types";
import { Search, ChevronDown, ChevronRight, FileText, CheckCircle2, AlertTriangle, Eye, Lock } from "lucide-react";

interface CorpusTableProps {
  titles: BaseTitleGroup[];
  onSelectArtifact: (artifact: CorpusArtifact) => void;
}

export default function CorpusTable({ titles, onSelectArtifact }: CorpusTableProps) {
  const [searchTerm, setSearchTerm] = useState("");
  const [filterMode, setFilterMode] = useState<"ALL" | "REVIEW" | "ORPHANS" | "DUPLICATES">("ALL");
  const [expandedTitles, setExpandedTitles] = useState<Record<string, boolean>>({});

  const toggleExpand = (id: string) => {
    setExpandedTitles(prev => ({ ...prev, [id]: !prev[id] }));
  };

  const filteredTitles = titles.filter(group => {
    const matchesSearch = 
      group.baseTitle.toLowerCase().includes(searchTerm.toLowerCase()) ||
      group.artifacts.some(a => a.path.toLowerCase().includes(searchTerm.toLowerCase()));

    if (!matchesSearch) return false;

    if (filterMode === "REVIEW") return group.containsAmbiguity;
    if (filterMode === "ORPHANS") return group.artifacts.some(a => a.isOrphan);
    if (filterMode === "DUPLICATES") return group.artifacts.some(a => a.isDuplicateCandidate);

    return true;
  });

  return (
    <div className="w-full bg-slate-900 border border-slate-800 rounded-xl overflow-hidden font-mono text-xs">
      <div className="p-4 bg-slate-950 border-b border-slate-800 flex flex-col sm:flex-row items-center justify-between gap-3">
        <div className="relative w-full sm:w-80">
          <Search className="w-4 h-4 text-slate-500 absolute left-3 top-2.5" />
          <input
            type="text"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            placeholder="Search titles, filenames, hashes..."
            className="w-full bg-slate-900 border border-slate-800 focus:border-cyan-500 text-slate-200 pl-9 pr-3 py-1.5 rounded-lg text-xs outline-none transition-colors"
          />
        </div>

        <div className="flex items-center gap-1.5 w-full sm:w-auto overflow-x-auto pb-1 sm:pb-0">
          <button
            onClick={() => setFilterMode("ALL")}
            className={`px-3 py-1.5 rounded-lg font-medium transition-colors ${
              filterMode === "ALL"
                ? "bg-cyan-600 text-white"
                : "bg-slate-900 text-slate-400 hover:bg-slate-800"
            }`}
          >
            All Titles ({titles.length})
          </button>
          <button
            onClick={() => setFilterMode("REVIEW")}
            className={`px-3 py-1.5 rounded-lg font-medium transition-colors ${
              filterMode === "REVIEW"
                ? "bg-red-600 text-white"
                : "bg-slate-900 text-slate-400 hover:bg-slate-800"
            }`}
          >
            Requires Review ({titles.filter(t => t.containsAmbiguity).length})
          </button>
          <button
            onClick={() => setFilterMode("ORPHANS")}
            className={`px-3 py-1.5 rounded-lg font-medium transition-colors ${
              filterMode === "ORPHANS"
                ? "bg-purple-600 text-white"
                : "bg-slate-900 text-slate-400 hover:bg-slate-800"
            }`}
          >
            Orphans ({titles.filter(t => t.artifacts.some(a => a.isOrphan)).length})
          </button>
          <button
            onClick={() => setFilterMode("DUPLICATES")}
            className={`px-3 py-1.5 rounded-lg font-medium transition-colors ${
              filterMode === "DUPLICATES"
                ? "bg-amber-600 text-white"
                : "bg-slate-900 text-slate-400 hover:bg-slate-800"
            }`}
          >
            Duplicates ({titles.filter(t => t.artifacts.some(a => a.isDuplicateCandidate)).length})
          </button>
        </div>
      </div>

      <div className="overflow-x-auto max-h-[500px] overflow-y-auto">
        <table className="w-full text-left border-collapse">
          <thead className="bg-slate-950 text-slate-400 border-b border-slate-800 sticky top-0 z-10">
            <tr>
              <th className="py-2.5 px-4 font-semibold w-10"></th>
              <th className="py-2.5 px-4 font-semibold">BASE TITLE / ARTIFACT GROUP</th>
              <th className="py-2.5 px-4 font-semibold">LANG</th>
              <th className="py-2.5 px-4 font-semibold text-center">VERSIONS</th>
              <th className="py-2.5 px-4 font-semibold">COMPONENTS AVAILABLE</th>
              <th className="py-2.5 px-4 font-semibold">G1–G5 SCORE</th>
              <th className="py-2.5 px-4 font-semibold text-right">EVIDENCE STATUS</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-800/60">
            {filteredTitles.length === 0 ? (
              <tr>
                <td colSpan={7} className="py-8 text-center text-slate-500">
                  No matching base titles discovered in current scan.
                </td>
              </tr>
            ) : (
              filteredTitles.map((group) => {
                const isExpanded = !!expandedTitles[group.id];

                return (
                  <React.Fragment key={group.id}>
                    <tr className="hover:bg-slate-800/50 transition-colors group">
                      <td className="py-2.5 px-4">
                        <button
                          onClick={() => toggleExpand(group.id)}
                          className="p-1 text-slate-400 hover:text-cyan-400 transition-colors"
                        >
                          {isExpanded ? (
                            <ChevronDown className="w-4 h-4 text-cyan-400" />
                          ) : (
                            <ChevronRight className="w-4 h-4" />
                          )}
                        </button>
                      </td>
                      <td className="py-2.5 px-4">
                        <div className="flex items-center gap-2">
                          <span className="font-bold text-slate-100 text-sm">
                            {group.baseTitle}
                          </span>
                          {group.containsAmbiguity && (
                            <span className="px-1.5 py-0.5 text-[10px] bg-red-950 text-red-400 border border-red-800 rounded">
                              REVIEW REQ
                            </span>
                          )}
                        </div>
                      </td>
                      <td className="py-2.5 px-4 text-slate-400">
                        <span className="px-2 py-0.5 bg-slate-800 rounded text-slate-300">
                          {group.primaryLanguage}
                        </span>
                      </td>
                      <td className="py-2.5 px-4 text-center font-bold text-cyan-300">
                        {group.artifactsCount}
                      </td>
                      <td className="py-2.5 px-4">
                        <div className="flex items-center gap-1.5 text-[10px]">
                          <span
                            className={`px-1.5 py-0.5 rounded ${
                              group.hasLyrics
                                ? "bg-emerald-950 text-emerald-400 border border-emerald-800"
                                : "bg-slate-800 text-slate-500"
                            }`}
                          >
                            LYRICS
                          </span>
                          <span
                            className={`px-1.5 py-0.5 rounded ${
                              group.hasMetadata
                                ? "bg-cyan-950 text-cyan-400 border border-cyan-800"
                                : "bg-slate-800 text-slate-500"
                            }`}
                          >
                            META
                          </span>
                          <span
                            className={`px-1.5 py-0.5 rounded ${
                              group.hasAudioRef
                                ? "bg-purple-950 text-purple-400 border border-purple-800"
                                : "bg-slate-800 text-slate-500"
                            }`}
                          >
                            AUDIO REF
                          </span>
                        </div>
                      </td>
                      <td className="py-2.5 px-4 text-amber-400 font-semibold text-[11px] flex items-center gap-1">
                        <Lock className="w-3 h-3 text-amber-500" />
                        <span>NOT MEASURED</span>
                      </td>
                      <td className="py-2.5 px-4 text-right">
                        <span className="px-2 py-0.5 bg-slate-950 text-emerald-400 border border-slate-800 rounded font-semibold text-[11px]">
                          READ-ONLY LOGGED
                        </span>
                      </td>
                    </tr>

                    {isExpanded &&
                      group.artifacts.map((artifact) => (
                        <tr
                          key={artifact.id}
                          className="bg-slate-950/60 hover:bg-slate-800/80 transition-colors border-l-2 border-cyan-500"
                        >
                          <td></td>
                          <td className="py-2 px-4 pl-8" colSpan={2}>
                            <div className="flex items-center gap-2">
                              <FileText className="w-3.5 h-3.5 text-slate-400" />
                              <span className="text-slate-300 font-medium">
                                {artifact.filename}
                              </span>
                              <span className="text-slate-500 text-[10px]">
                                ({artifact.versionLabel})
                              </span>
                            </div>
                            <div className="text-[10px] text-slate-500 mt-0.5 font-mono truncate max-w-md">
                              Path: {artifact.path}
                            </div>
                          </td>
                          <td className="py-2 px-4 text-center text-slate-400 text-[11px]">
                            {(artifact.evidence.sizeBytes / 1024).toFixed(1)} KB
                          </td>
                          <td className="py-2 px-4">
                            <span className="text-[10px] text-slate-400 font-mono">
                              Hash: {artifact.evidence.fileHash.slice(0, 16)}...
                            </span>
                          </td>
                          <td className="py-2 px-4 text-slate-400">
                            {artifact.status === "PARSED" ? (
                              <span className="text-emerald-400 flex items-center gap-1">
                                <CheckCircle2 className="w-3 h-3" /> PARSED
                              </span>
                            ) : (
                              <span className="text-red-400 flex items-center gap-1">
                                <AlertTriangle className="w-3 h-3" /> FAILED
                              </span>
                            )}
                          </td>
                          <td className="py-2 px-4 text-right">
                            <button
                              onClick={() => onSelectArtifact(artifact)}
                              className="px-2.5 py-1 bg-slate-800 hover:bg-cyan-950 hover:text-cyan-300 text-slate-300 rounded border border-slate-700 text-[11px] flex items-center gap-1 ml-auto"
                            >
                              <Eye className="w-3 h-3" /> Inspect Evidence
                            </button>
                          </td>
                        </tr>
                      ))}
                  </React.Fragment>
                );
              })
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
