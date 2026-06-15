import React, { useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import type { SolverResponse } from '../types/apiTypes';
import {
  DndContext,
  closestCenter,
  PointerSensor,
  useSensor,
  useSensors,
  type DragEndEvent,
} from '@dnd-kit/core';
import {
  arrayMove,
  SortableContext,
  horizontalListSortingStrategy,
  useSortable,
} from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';

interface LocationState {
  solutionData: SolverResponse;
  problemTitle: string;
}

// --- Sortable Column Header ---
const SortableHeader = ({
  id,
  label,
  sortKey,
  sortConfig,
  onSort,
}: {
  id: string;
  label: string;
  sortKey: string;
  sortConfig: { key: string; dir: 'asc' | 'desc' } | null;
  onSort: (key: string) => void;
}) => {
  const { attributes, listeners, setNodeRef, transform, transition, isDragging } =
    useSortable({ id });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    opacity: isDragging ? 0.5 : 1,
  };

  const isActive = sortConfig?.key === sortKey;

  return (
    <th
      ref={setNodeRef}
      style={style}
      className="px-6 py-3 font-semibold uppercase tracking-wider text-[11px] bg-slate-50 dark:bg-gray-900 text-slate-500 dark:text-slate-400"
    >
      <div className="flex items-center gap-1">
        {/* אייקון גרירה */}
        <span
          {...attributes}
          {...listeners}
          className="material-symbols-outlined text-[16px] text-slate-300 cursor-grab mr-1"
        >
          drag_indicator
        </span>

        {/* כותרת + sort */}
        <div
          className="flex items-center gap-1 cursor-pointer select-none"
          onClick={() => onSort(sortKey)}
        >
          {label}
          <span className="material-symbols-outlined text-[14px]">
            {isActive ? (sortConfig?.dir === 'asc' ? 'arrow_upward' : 'arrow_downward') : 'unfold_more'}
          </span>
        </div>
      </div>
    </th>
  );
};

// --- Single Variable Table ---
const VariableTable = ({ varName, varData }: { varName: string; varData: any }) => {
  const allObjValuesAreOne = varData.solutions.every((sol: any) => sol.objectiveValue === 1);

  const initialCols = [
    { id: 'idx', label: '#' },
    ...varData.typeStructure.map((type: string, i: number) => ({
      id: `col_${i}`,
      label: type,
    })),
    ...(!allObjValuesAreOne
      ? [{ id: 'obj', label: varData.objectiveValueAlias || 'Objective Value' }]
      : []),
  ];

  const [columns, setColumns] = useState(initialCols);
  const [sortConfig, setSortConfig] = useState<{ key: string; dir: 'asc' | 'desc' } | null>(null);

  const sensors = useSensors(useSensor(PointerSensor));

  const handleDragEnd = (event: DragEndEvent) => {
    const { active, over } = event;
    if (over && active.id !== over.id) {
      setColumns((cols) => {
        const oldIndex = cols.findIndex((c) => c.id === active.id);
        const newIndex = cols.findIndex((c) => c.id === over.id);
        return arrayMove(cols, oldIndex, newIndex);
      });
    }
  };

  const handleSort = (key: string) => {
    setSortConfig((prev) =>
      prev?.key === key
        ? { key, dir: prev.dir === 'asc' ? 'desc' : 'asc' }
        : { key, dir: 'asc' }
    );
  };

  const getSortedSolutions = () => {
    if (!sortConfig) return varData.solutions;
    return [...varData.solutions].sort((a: any, b: any) => {
      let aVal: any, bVal: any;
      if (sortConfig.key === 'obj') {
        aVal = a.objectiveValue;
        bVal = b.objectiveValue;
      } else {
        const idx = parseInt(sortConfig.key.replace('col_', ''));
        aVal = a.values[idx];
        bVal = b.values[idx];
      }
      if (aVal < bVal) return sortConfig.dir === 'asc' ? -1 : 1;
      if (aVal > bVal) return sortConfig.dir === 'asc' ? 1 : -1;
      return 0;
    });
  };

  const sortedSolutions = getSortedSolutions();

  return (
    <div className="bg-white dark:bg-gray-800 rounded-xl border border-[#dbe2e6] dark:border-gray-700 shadow-sm overflow-hidden mb-6">
      <div className="px-6 py-4 border-b border-[#dbe2e6] dark:border-gray-700 flex items-center justify-between">
        <h3 className="text-lg font-bold text-[#111618] dark:text-white">{varName}</h3>
        <span className="text-xs text-gray-400">{varData.solutions.length} rows</span>
      </div>

      <div className="overflow-x-auto">
        <table className="w-full text-left text-sm">
          <thead>
            <DndContext sensors={sensors} collisionDetection={closestCenter} onDragEnd={handleDragEnd}>
              <SortableContext items={columns.map((c) => c.id)} strategy={horizontalListSortingStrategy}>
                <tr>
                  {columns.map((col) =>
                    col.id === 'idx' ? (
                      <th
                        key="idx"
                        className="px-6 py-3 font-semibold uppercase tracking-wider text-[11px] bg-slate-50 dark:bg-gray-900 text-slate-500 dark:text-slate-400"
                      >
                        #
                      </th>
                    ) : (
                      <SortableHeader
                        key={col.id}
                        id={col.id}
                        label={col.label}
                        sortKey={col.id}
                        sortConfig={sortConfig}
                        onSort={handleSort}
                      />
                    )
                  )}
                </tr>
              </SortableContext>
            </DndContext>
          </thead>
          <tbody className="divide-y divide-slate-100 dark:divide-gray-700">
            {sortedSolutions.map((sol: any, i: number) => (
              <tr key={i} className="hover:bg-slate-50 dark:hover:bg-gray-700/30">
                {columns.map((col) => {
                  if (col.id === 'idx') {
                    return <td key="idx" className="px-6 py-3 text-slate-400 text-xs">{i + 1}</td>;
                  }
                  if (col.id === 'obj') {
                    return <td key="obj" className="px-6 py-3 text-[#111618] dark:text-slate-300">{sol.objectiveValue}</td>;
                  }
                  const idx = parseInt(col.id.replace('col_', ''));
                  return (
                    <td key={col.id} className="px-6 py-3 text-[#111618] dark:text-slate-300">
                      {sol.values[idx]}
                    </td>
                  );
                })}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

// --- Main Page ---
const SolutionPage: React.FC = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const location = useLocation();

  const state = location.state as LocationState;
  const { solutionData, problemTitle } = state || {};

  if (!solutionData) {
    return (
      <div className="flex flex-col items-center justify-center min-h-screen">
        <h2 className="text-xl font-bold mb-4">No solution data found.</h2>
        <button onClick={() => navigate('/myimages')} className="text-blue-500 underline">Back to My Problems</button>
      </div>
    );
  }

  return (
    <div className="bg-[#f6f7f8] dark:bg-background-dark font-sans text-[#111618] dark:text-white min-h-screen flex flex-col">
      <style>{`
        .material-symbols-outlined { font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24; }
        .custom-scrollbar::-webkit-scrollbar { width: 8px; height: 8px; }
        .custom-scrollbar::-webkit-scrollbar-track { background: #f1f1f1; }
        .custom-scrollbar::-webkit-scrollbar-thumb { background: #d1d5db; border-radius: 4px; }
        .dark .custom-scrollbar::-webkit-scrollbar-track { background: #1f2937; }
        .dark .custom-scrollbar::-webkit-scrollbar-thumb { background: #4b5563; }
      `}</style>

      <main className="flex-1 px-4 sm:px-10 py-8 max-w-[1440px] mx-auto w-full">

        {/* Breadcrumbs */}
        <div className="flex flex-wrap gap-2 mb-4">
          <button onClick={() => navigate('/myimages')} className="text-[#617c89] text-sm font-medium leading-normal hover:text-primary">My Problems</button>
          <span className="text-[#617c89] text-sm font-medium leading-normal">/</span>
          <span className="text-[#111618] dark:text-white text-sm font-medium leading-normal">Solution Results</span>
        </div>

        {/* Page Heading */}
        <div className="flex flex-wrap justify-between items-end gap-3 mb-8">
          <div className="flex min-w-72 flex-col gap-2">
            <div className="flex items-center gap-3">
              <h1 className="text-[#111618] dark:text-white text-4xl font-black leading-tight tracking-[-0.033em]">
                {problemTitle}
              </h1>
              {solutionData.solved ? (
                <span className="bg-[#e7f6ed] text-[#078836] px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider">Solved Successfully</span>
              ) : (
                <span className="bg-red-100 text-red-600 px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider">Unsolved</span>
              )}
            </div>
            <p className="text-[#617c89] text-base font-normal leading-normal">
              Problem ID: {id} • Time taken: {solutionData.solvingTime}ms • Objective Value: {solutionData.objectiveValue.toFixed(2)}
            </p>
          </div>
          <div className="flex gap-3">
            <button onClick={() => navigate(`/problems/${id}`)} className="flex min-w-[84px] cursor-pointer items-center justify-center rounded-lg h-10 px-4 bg-[#f0f3f4] dark:bg-gray-700 text-[#111618] dark:text-white text-sm font-bold leading-normal hover:bg-[#e0e3e4] transition-all">
              <span className="truncate text-primary">Back to Problem Details</span>
            </button>
          </div>
        </div>

        {/* Tables */}
        {Object.entries(solutionData.solution).map(([varName, varData]: [string, any]) => (
          <VariableTable key={varName} varName={varName} varData={varData} />
        ))}

      </main>
    </div>
  );
};

export default SolutionPage;