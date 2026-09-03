import React, { useState, useMemo } from "react";
import { BaseTitleGroup, CorpusArtifact } from "../types";
import { Search, ChevronDown, ChevronRight, FileText, CheckCircle2, AlertTriangle, Eye, Lock, Globe, ArrowUpDown, Filter, Sparkles } from "lucide-react";

interface CorpusTableProps {
  titles: BaseTitleGroup[];
  onSelectArtifact: (artifact: CorpusArtifact) => void;
}

export default function CorpusTable({ titles, onSelectArtifact }: CorpusTableProps) {
  const [searchTerm, setSearchTerm] = useState("");
  const [filterMode, setFilterMode] = useState<"ALL" | "REVIEW" | "ORPHANS" | "DUPLICATES">("ALL");
  const [selectedLanguage, setSelectedLanguage] = useState<"ALL" | "Hindi" | "English" | "Multilingual">("ALL");
  const [sortMode, setSortMode] = useState<"DEFAULT" | "HINDI_FIRST" | "TITLE_ASC" | "VERSIONS_DESC">("DEFAULT");
  const [expandedTitles, setExpandedTitles] = useState<Record<string, boolean>>({});
  const [showAdvancedGates, setShowAdvancedGates] = useState(false);

  const toggleExpand = (id: string) => {
    setExpandedTitles(prev => ({ ...prev, [id]: !prev[id] }));
  };

  const languageCounts = useMemo(() => {
    let hindi = 0;
    let english = 0;
    let multi = 0;
    titles.forEach(t => {
      const l = t.primaryLanguage.toLowerCase();
      if (l === "hindi") hindi++;
      else if (l === "multilingual") multi++;
      else english++;
    });
    return { all: titles.length, hindi, english, multi };
  }, [titles]);

  const filteredTitles = useMemo(() => {
    const list = titles.filter(group => {
      // 1. Language Filter
      if (selectedLanguage !== "ALL") {
        if (group.primaryLanguage.toLowerCase() !== selectedLanguage.toLowerCase()) {
          return false;
        }
      }

      // 2. Search Filter
      const matchesSearch = 
        group.baseTitle.toLowerCase().includes(searchTerm.toLowerCase()) ||
        group.primaryLanguage.toLowerCase().includes(searchTerm.toLowerCase()) ||
        group.artifacts.some(a => a.path.toLowerCase().includes(searchTerm.toLowerCase()) || a.filename.toLowerCase().includes(searchTerm.toLowerCase()));

      if (!matchesSearch) return false;

      // 3. Status Filter
      if (filterMode === "REVIEW") return group.containsAmbiguity;
      if (filterMode === "ORPHANS") return group.artifacts.some(a => a.isOrphan);
      if (filterMode === "DUPLICATES") return group.artifacts.some(a => a.isDuplicateCandidate);

      return true;
    });

    // 4. Sorting
    if (sortMode === "HINDI_FIRST") {
      return [...list].sort((a, b) => {
        const aIsHindi = a.primaryLanguage.toLowerCase() === "hindi" ? 1 : 0;
        const bIsHindi = b.primaryLanguage.toLowerCase() === "hindi" ? 1 : 0;
        if (aIsHindi !== bIsHindi) return bIsHindi - aIsHindi;
        return a.baseTitle.localeCompare(b.baseTitle);
      });
    } else if (sortMode === "TITLE_ASC") {
      return [...list].sort((a, b) => a.baseTitle.localeCompare(b.baseTitle));
    } else if (sortMode === "VERSIONS_DESC") {
      return [...list].sort((a, b) => b.artifactsCount - a.artifactsCount);
    }

    return list;
  }, [titles, selectedLanguage, searchTerm, filterMode, sortMode]);

  return (
    <div className="w-full bg-slate-900 border border-slate-800 rounded-xl overflow-hidden font-mono text-xs shadow-xl">
      {/* Top Bar: Search & Status Filters */}
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
                ? "bg-cyan-600 text-white shadow-sm"
                : "bg-slate-900 text-slate-400 hover:bg-slate-800"
            }`}
          >
            All Titles ({titles.length})
          </button>
          <button
            onClick={() => setFilterMode("REVIEW")}
            className={`px-3 py-1.5 rounded-lg font-medium transition-colors ${
              filterMode === "REVIEW"
                ? "bg-red-600 text-white shadow-sm"
                : "bg-slate-900 text-slate-400 hover:bg-slate-800"
            }`}
          >
            Requires Review ({titles.filter(t => t.containsAmbiguity).length})
          </button>
          <button
            onClick={() => setFilterMode("ORPHANS")}
            className={`px-3 py-1.5 rounded-lg font-medium transition-colors ${
              filterMode === "ORPHANS"
                ? "bg-purple-600 text-white shadow-sm"
                : "bg-slate-900 text-slate-400 hover:bg-slate-800"
            }`}
          >
            Orphans ({titles.filter(t => t.artifacts.some(a => a.isOrphan)).length})
          </button>
          <button
            onClick={() => setFilterMode("DUPLICATES")}
            className={`px-3 py-1.5 rounded-lg font-medium transition-colors ${
              filterMode === "DUPLICATES"
                ? "bg-amber-600 text-white shadow-sm"
                : "bg-slate-900 text-slate-400 hover:bg-slate-800"
            }`}
          >
            Duplicates ({titles.filter(t => t.artifacts.some(a => a.isDuplicateCandidate)).length})
          </button>

          <button
            onClick={() => setShowAdvancedGates(!showAdvancedGates)}
            className="ml-auto px-2.5 py-1.5 bg-slate-800 hover:bg-slate-700 text-slate-300 border border-slate-700 rounded-lg text-[11px] font-mono flex items-center gap-1 transition-colors whitespace-nowrap"
          >
            <Lock className="w-3 h-3 text-amber-400" />
            <span>{showAdvancedGates ? "Hide Technical Gates" : "Show Gate Details"}</span>
          </button>
        </div>
      </div>

      {/* Language Isolation Filter Bar & Sort Controls */}
      <div className="px-4 py-2.5 bg-slate-950/70 border-b border-slate-800 flex flex-wrap items-center justify-between gap-3 text-xs">
        <div className="flex items-center gap-2 flex-wrap">
          <div className="flex items-center gap-1.5 text-slate-400 font-semibold mr-1">
            <Globe className="w-3.5 h-3.5 text-cyan-400" />
            <span className="text-[11px] uppercase tracking-wider text-slate-400">Language Isolation:</span>
          </div>

          <button
            onClick={() => setSelectedLanguage("ALL")}
            className={`px-2.5 py-1 rounded-md text-[11px] font-medium transition-all ${
              selectedLanguage === "ALL"
                ? "bg-slate-700 text-white border border-slate-600"
                : "bg-slate-900/90 text-slate-400 hover:bg-slate-800 border border-slate-800/80"
            }`}
          >
            All Languages ({languageCounts.all})
          </button>

          <button
            onClick={() => setSelectedLanguage("Hindi")}
            className={`px-2.5 py-1 rounded-md text-[11px] font-semibold flex items-center gap-1.5 transition-all ${
              selectedLanguage === "Hindi"
                ? "bg-amber-500/20 text-amber-300 border border-amber-500/60 shadow-[0_0_12px_rgba(245,158,11,0.25)]"
                : "bg-slate-900/90 text-amber-400/80 hover:bg-amber-950/40 border border-amber-900/30"
            }`}
          >
            <Sparkles className="w-3 h-3 text-amber-400" />
            <span>Hindi Witness Vault ({languageCounts.hindi})</span>
          </button>

          <button
            onClick={() => setSelectedLanguage("English")}
            className={`px-2.5 py-1 rounded-md text-[11px] font-medium transition-all ${
              selectedLanguage === "English"
                ? "bg-cyan-500/20 text-cyan-300 border border-cyan-500/60 shadow-sm"
                : "bg-slate-900/90 text-slate-400 hover:bg-slate-800 border border-slate-800/80"
            }`}
          >
            English ({languageCounts.english})
          </button>

          <button
            onClick={() => setSelectedLanguage("Multilingual")}
            className={`px-2.5 py-1 rounded-md text-[11px] font-medium transition-all ${
              selectedLanguage === "Multilingual"
                ? "bg-purple-500/20 text-purple-300 border border-purple-500/60 shadow-sm"
                : "bg-slate-900/90 text-purple-400/80 hover:bg-purple-950/40 border border-purple-900/30"
            }`}
          >
            Multilingual Anchors ({languageCounts.multi})
          </button>
        </div>

        {/* Sort Controls */}
        <div className="flex items-center gap-2">
          <ArrowUpDown className="w-3.5 h-3.5 text-slate-500" />
          <span className="text-slate-500 text-[11px]">Sort:</span>
          <select
            value={sortMode}
            onChange={(e) => setSortMode(e.target.value as any)}
            className="bg-slate-900 border border-slate-800 text-slate-300 px-2 py-1 rounded-md text-[11px] outline-none cursor-pointer hover:border-slate-700"
          >
            <option value="DEFAULT">Default (Corpus Index)</option>
            <option value="HINDI_FIRST">Hindi Vault First</option>
            <option value="TITLE_ASC">Title (A-Z)</option>
            <option value="VERSIONS_DESC">Version Count (High to Low)</option>
          </select>
        </div>
      </div>

      {/* Active Language Filter Notice */}
      {selectedLanguage === "Hindi" && (
        <div className="bg-amber-950/30 border-b border-amber-900/40 px-4 py-2 flex items-center justify-between text-[11px] text-amber-300">
          <div className="flex items-center gap-2">
            <Sparkles className="w-3.5 h-3.5 text-amber-400" />
            <span className="font-semibold">HINDI WITNESS VAULT ISOLATION ACTIVE:</span>
            <span>Displaying {filteredTitles.length} titles for phonetic anchor & physical witness verification.</span>
          </div>
          <button
            onClick={() => setSelectedLanguage("ALL")}
            className="text-[10px] text-amber-400 hover:text-amber-200 underline cursor-pointer"
          >
            Reset Language Filter
          </button>
        </div>
      )}

      <div className="overflow-x-auto max-h-[500px] overflow-y-auto">
        <table className="w-full text-left border-collapse">
          <thead className="bg-slate-950 text-slate-400 border-b border-slate-800 sticky top-0 z-10">
            <tr>
              <th className="py-2.5 px-4 font-semibold w-10"></th>
              <th className="py-2.5 px-4 font-semibold">BASE TITLE / ARTIFACT GROUP</th>
              <th className="py-2.5 px-4 font-semibold">LANG</th>
              <th className="py-2.5 px-4 font-semibold text-center">VERSIONS</th>
              <th className="py-2.5 px-4 font-semibold">COMPONENTS AVAILABLE</th>
              <th className="py-2.5 px-4 font-semibold">
                {showAdvancedGates ? "G1–G5 GATE STATUS" : "CURATION STATUS"}
              </th>
              <th className="py-2.5 px-4 font-semibold text-right">EVIDENCE RECORD</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-800/60">
            {filteredTitles.length === 0 ? (
              <tr>
                <td colSpan={7} className="py-8 text-center text-slate-500">
                  No matching base titles discovered in current scan for selected language/filter.
                </td>
              </tr>
            ) : (
              filteredTitles.map((group) => {
                const isExpanded = !!expandedTitles[group.id];
                const isHindi = group.primaryLanguage.toLowerCase() === "hindi";
                const isMulti = group.primaryLanguage.toLowerCase() === "multilingual";

                return (
                  <React.Fragment key={group.id}>
                    <tr className={`hover:bg-slate-800/50 transition-colors group ${isHindi ? "bg-amber-950/10" : ""}`}>
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
                          <span className={`font-bold text-sm ${isHindi ? "text-amber-200" : "text-slate-100"}`}>
                            {group.baseTitle}
                          </span>
                          {isHindi && (
                            <span className="px-1.5 py-0.2 text-[9px] bg-amber-950 text-amber-300 border border-amber-800 rounded font-semibold">
                              WITNESS VAULT
                            </span>
                          )}
                          {group.containsAmbiguity && (
                            <span className="px-1.5 py-0.5 text-[10px] bg-red-950 text-red-400 border border-red-800 rounded">
                              REVIEW REQ
                            </span>
                          )}
                        </div>
                      </td>
                      <td className="py-2.5 px-4 text-slate-400">
                        <span className={`px-2 py-0.5 rounded text-[11px] font-medium ${
                          isHindi 
                            ? "bg-amber-950/80 text-amber-300 border border-amber-800/60" 
                            : (isMulti 
                                ? "bg-purple-950/80 text-purple-300 border border-purple-800/60"
                                : "bg-slate-800 text-slate-300")
                        }`}>
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
                      <td className="py-2.5 px-4 font-semibold text-[11px]">
                        {showAdvancedGates ? (
                          <span className="text-amber-400 flex items-center gap-1">
                            <Lock className="w-3 h-3 text-amber-500" /> G1–G5: NOT MEASURED
                          </span>
                        ) : (
                          <span className="px-2 py-0.5 bg-slate-800 text-slate-300 rounded font-normal">
                            Unchecked / Pending
                          </span>
                        )}
                      </td>
                      <td className="py-2.5 px-4 text-right">
                        <span className="px-2 py-0.5 bg-slate-950 text-emerald-400 border border-slate-800 rounded font-semibold text-[11px]">
                          VERIFIED HASH
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
