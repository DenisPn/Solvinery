import { useState, useMemo, useEffect } from 'react';
import { useLocation } from 'react-router-dom';

// Component Imports
import MaterialIcon from '../components/ui/MaterialIcon';
import VariablesTab from '../components/tabs/VariablesTab';
import PreferencesTab from '../components/tabs/PreferencesTab';
import ConstraintsTab from '../components/tabs/ConstraintsTab';
import SetsTab from '../components/tabs/SetsTab';
import ParametersTab, { type ParameterData } from '../components/tabs/ParametersTab';
import UploadZplModal from '../components/modals/UploadZplModal';
import ReviewTab from '../components/tabs/ReviewTab';

// Service & Context Imports
import { NewImageService, type ParseModelResponse } from '../services/NewImageService';
import { useAuth } from '../context/AuthContext';

// Type Imports
import type { SetData } from '../components/sets/SetTableRow';
import type { VariableData } from '../components/variables/VariableTableRow';
import type { ModelPayload, ImageDto } from '../types/apiTypes';

export default function NewImagePage() {
  const { userId } = useAuth();
  const location = useLocation();
  const editState = location.state as { editMode?: boolean; imageId?: string; imageDto?: ImageDto } | null;
  const isEditMode = editState?.editMode ?? false;

  // --- UI State ---
  const [showUploadModal, setShowUploadModal] = useState(false);
  const [activeTab, setActiveTab] = useState('general');

  // --- Data State ---
  const [modelData, setModelData] = useState<ParseModelResponse | null>(null);
  const [zplCode, setZplCode] = useState<string>("");
  const [modelName, setModelName] = useState<string>("");
  const [modelDescription, setModelDescription] = useState<string>("");

  // Local State for Tabs (Editable Data)
  const [variables, setVariables] = useState<VariableData[]>([]);
  const [preferences, setPreferences] = useState<any[]>([]);
  const [constraints, setConstraints] = useState<any[]>([]);

  // --- Set upload modal based on edit mode ---
  useEffect(() => {
    setShowUploadModal(!isEditMode);
  }, [isEditMode]);

  // --- Load Edit Data ---
  useEffect(() => {
    if (isEditMode && editState?.imageDto) {
      const dto = editState.imageDto;
      setModelName(dto.name || "");
      setModelDescription(dto.description || "");
      setZplCode(dto.code || "");
      setVariables(
        (dto.variables || []).map(v => ({
          name: v.identifier,
          type: v.structure?.length ? `[${v.structure.join(', ')}]` : 'Integer',
          alias: v.alias || '-',
          objectiveValueAlias: v.objectiveValueAlias || '-',
          desc: v.alias ? `Alias: ${v.alias}` : 'Decision Variable'
        }))
      );
      setConstraints(dto.constraintModules || []);
      setPreferences(dto.preferenceModules || []);
      setModelData({
        variables: dto.variables || [],
        constraints: (dto.constraintModules || []).map(m => ({ identifier: m.moduleName })),
        preferences: (dto.preferenceModules || []).map(m => ({ identifier: m.moduleName })),
        setTypes: Object.fromEntries((dto.sets || []).map(s => [s.setDefinition.name, s.values])),
        paramTypes: Object.fromEntries((dto.parameters || []).map(p => [p.parameterDefinition.name, p.parameterDefinition.structure])),
      });
    }
  }, [isEditMode]);

  // --- Upload Logic ---
  const handleUploadComplete = (file: File) => {
    if (!userId) {
      alert("Session error: Please log in again.");
      return;
    }

    const reader = new FileReader();

    reader.onload = async (e) => {
      const text = e.target?.result;
      if (typeof text === 'string') {
        try {
          setZplCode(text);
          const data = await NewImageService.parseImageModel(userId, text);
          setModelData(data);
          setShowUploadModal(false);
        } catch (error) {
          console.error("Failed to parse model:", error);
          alert("Error parsing ZPL file. Please try again.");
        }
      }
    };

    reader.readAsText(file);
  };

  // --- Data Initialization (When file is uploaded) ---
  useEffect(() => {
    if (modelData && !isEditMode) {
      if (modelData.variables) {
        const mappedVariables: VariableData[] = modelData.variables.map((v) => {
          let structureDisplay = "Integer";
          if (Array.isArray(v.structure) && v.structure.length > 0) {
            structureDisplay = `[${v.structure.join(', ')}]`;
          }
          return {
            name: v.identifier,
            type: structureDisplay,
            alias: v.alias || '-',
            objectiveValueAlias: v.objectiveValueAlias || '-',
            desc: v.alias ? `Alias: ${v.alias}` : 'Decision Variable'
          };
        });
        setVariables(mappedVariables);
      }
      if (modelData.preferences) setPreferences([]);
      if (modelData.constraints) setConstraints([]);
    }
  }, [modelData]);

  // --- Mappers for Read-Only Tabs ---
  const setsData: SetData[] = useMemo(() => {
    if (!modelData?.setTypes) return [];
    return Object.entries(modelData.setTypes).map(([setName, members]) => ({
      name: setName,
      desc: `Defined set with ${members.length} elements`,
      members: members.slice(0, 3),
      extraCount: Math.max(0, members.length - 3),
      totalCount: members.length
    }));
  }, [modelData]);

  const parametersData: ParameterData[] = useMemo(() => {
    if (!modelData?.paramTypes) return [];
    return Object.entries(modelData.paramTypes).map(([name, type]) => ({
      name: name,
      type: type
    }));
  }, [modelData]);

  // --- Helper to Collect All Data for Review Tab & API ---
  const collectAllData = (): ModelPayload => {
    return {
      name: modelName || "New Optimization Model",
      description: modelDescription || "Generated from ZPL Upload",
      code: zplCode,
      variables: variables.map(v => ({
        identifier: v.name,
        structure: v.type.startsWith('[')
          ? v.type.replace(/[\[\]]/g, '').split(', ')
          : ["scalar"],
        alias: (v.alias === '-' || !v.alias) ? "" : v.alias,
        objectiveValueAlias: (v.objectiveValueAlias === '-' || !v.objectiveValueAlias) ? "" : v.objectiveValueAlias
      })),
      constraintModules: constraints
        .filter((c: any) => c.constraints && c.constraints.length > 0)
        .map((c: any) => ({
          moduleName: c.name || c.title || c.moduleName || "Unnamed Constraint Module",
          description: c.description || c.desc || "",
          constraints: c.constraints,
          active: true
        })),
      preferenceModules: preferences
        .filter((p: any) => p.preferences && p.preferences.length > 0)
        .map((p: any) => ({
          moduleName: p.name || p.title || p.moduleName || "Unnamed Preference Module",
          description: p.description || p.desc || "",
          preferences: p.preferences,
          scalar: p.scalar || 1
        })),
      sets: Object.entries(modelData?.setTypes || {}).map(([setName, members]) => ({
        setDefinition: {
          name: setName,
          structure: ["string"],
          alias: ""
        },
        values: members
      })),
      parameters: Object.entries(modelData?.paramTypes || {}).map(([name, type]) => ({
        parameterDefinition: {
          name: name,
          structure: type,
          alias: ""
        },
        value: "0"
      }))
    };
  };

  // --- Tabs Configuration ---
  const tabs = [
    { id: 'general', label: 'General Information' },
    { id: 'sets', label: 'Sets' },
    { id: 'parameters', label: 'Parameters' },
    { id: 'variables', label: 'Variables' },
    { id: 'constraints', label: 'Constraint Modules' },
    { id: 'preferences', label: 'Preference Modules' },
    { id: 'summary', label: 'Review' },
  ];

  const getPageDescription = () => {
    switch (activeTab) {
      case 'sets': return "Groups of entities extracted from your ZPL model.";
      case 'parameters': return "Global constants and parameters defined in the system.";
      case 'variables': return "Decision variables that the optimizer will solve for.";
      case 'constraints': return "Rules and limitations extracted from the model.";
      case 'preferences': return "Optimization objectives and soft constraints.";
      case 'summary': return "Review all configuration details before submitting.";
      default: return "Configure the settings for your new scheduling problem.";
    }
  };

  const handleDraftSave = () => {
    const payload = collectAllData();
    console.log("Draft Payload:", payload);
    alert("Draft saved locally! (Check console)");
  };

  return (
    <div className="font-display bg-[#f6f7f8] dark:bg-[#101c22] text-[#111618] dark:text-white min-h-screen flex flex-col transition-colors duration-200">

      {showUploadModal && (
        <UploadZplModal
          onUpload={handleUploadComplete}
          onBack={() => console.log("Back clicked")}
        />
      )}

      {/* Main Layout */}
      <div className="layout-container flex h-full grow flex-col">
        <div className="md:px-10 lg:px-40 flex flex-1 justify-center py-5">
          <div className="layout-content-container flex flex-col w-full max-w-[1024px] flex-1 gap-6">

            {/* Breadcrumbs & Title */}
            <div className="flex flex-col gap-2 px-4">
              <div className="flex flex-wrap gap-2 items-center">
                <a className="text-[#617c89] dark:text-gray-400 text-sm font-medium hover:underline" href="#">Home</a>
                <span className="text-[#617c89] dark:text-gray-400 text-sm font-medium">/</span>
                <a className="text-[#617c89] dark:text-gray-400 text-sm font-medium hover:underline" href="#">Problems</a>
                <span className="text-[#617c89] dark:text-gray-400 text-sm font-medium">/</span>
                <span className="text-[#111618] dark:text-white text-sm font-medium">
                  {isEditMode ? 'Edit Problem' : 'Create New'}
                </span>
              </div>

              <div className="flex flex-wrap justify-between gap-3 mt-2">
                <div className="flex min-w-72 flex-col gap-2">
                  <h1 className="text-[#111618] dark:text-white text-3xl font-black leading-tight tracking-[-0.033em]">
                    {isEditMode ? 'Edit Scheduling Problem' : 'Create New Scheduling Problem'}
                  </h1>
                  <p className="text-[#617c89] dark:text-gray-400 text-base font-normal">
                    {getPageDescription()}
                  </p>
                </div>
                <div className="flex items-center gap-3">
                  <button
                    onClick={handleDraftSave}
                    className="flex items-center gap-2 rounded-lg border border-[#dbe2e6] dark:border-gray-600 px-4 py-2 text-sm font-medium text-[#111618] dark:text-gray-200 bg-white dark:bg-[#182830] hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
                  >
                    <MaterialIcon icon="save" className="text-[20px]" />
                    Save Draft
                  </button>
                </div>
              </div>
            </div>

            {/* Tabs Navigation */}
            <div className="px-4">
              <div className="flex border-b border-[#dbe2e6] dark:border-gray-700 gap-8 overflow-x-auto no-scrollbar">
                {tabs.map((tab) => (
                  <button
                    key={tab.id}
                    onClick={() => setActiveTab(tab.id)}
                    className={`flex flex-col items-center justify-center border-b-[3px] pb-[13px] pt-4 min-w-max transition-colors cursor-pointer
                      ${activeTab === tab.id
                        ? 'border-b-[#13a4ec] text-[#13a4ec] dark:text-[#13a4ec]'
                        : 'border-b-transparent hover:border-b-gray-300 text-[#617c89] dark:text-gray-400 hover:text-[#13a4ec] dark:hover:text-white'
                      }`}
                  >
                    <p className="text-sm leading-normal tracking-[0.015em] font-bold">{tab.label}</p>
                  </button>
                ))}
              </div>
            </div>

            {/* Dynamic Content Area */}
            <div className="px-4">
              {activeTab === 'sets' && <SetsTab data={setsData} />}

              {activeTab === 'variables' && (
                <VariablesTab
                  data={variables}
                  onUpdate={(updatedVars) => setVariables(updatedVars)}
                />
              )}

              {activeTab === 'parameters' && <ParametersTab data={parametersData} />}

              {activeTab === 'constraints' && (
                <ConstraintsTab
                  data={constraints}
                  onUpdate={(newConstraints) => setConstraints(newConstraints)}
                  libraryData={modelData?.constraints || []}
                />
              )}

              {activeTab === 'preferences' && (
                <PreferencesTab
                  data={preferences}
                  onUpdate={(newPrefs) => setPreferences(newPrefs)}
                  libraryData={modelData?.preferences || []}
                />
              )}

              {activeTab === 'summary' && (
                <ReviewTab
                  userId={userId || ""}
                  data={collectAllData()}
                />
              )}

              {activeTab === 'general' && (
                <div className="flex flex-col gap-6 bg-white dark:bg-[#1A2C38] rounded-xl border border-[#dbe2e6] dark:border-gray-700 p-8">
                  <h3 className="text-lg font-bold text-[#111618] dark:text-white">General Information</h3>

                  <label className="flex flex-col gap-2">
                    <span className="text-sm font-medium text-[#111618] dark:text-white">Problem Name</span>
                    <input
                      type="text"
                      value={modelName}
                      onChange={(e) => setModelName(e.target.value)}
                      placeholder="e.g. University Course Scheduling"
                      className="w-full rounded-xl border border-[#dbe2e6] dark:border-gray-600 bg-white dark:bg-[#101c22] px-4 py-3 text-sm outline-none focus:border-[#13a4ec] focus:ring-1 focus:ring-[#13a4ec] text-[#111618] dark:text-white"
                    />
                  </label>

                  <label className="flex flex-col gap-2">
                    <span className="text-sm font-medium text-[#111618] dark:text-white">Description</span>
                    <textarea
                      value={modelDescription}
                      onChange={(e) => setModelDescription(e.target.value)}
                      placeholder="Describe the scheduling problem..."
                      rows={4}
                      className="w-full rounded-xl border border-[#dbe2e6] dark:border-gray-600 bg-white dark:bg-[#101c22] px-4 py-3 text-sm outline-none focus:border-[#13a4ec] focus:ring-1 focus:ring-[#13a4ec] text-[#111618] dark:text-white resize-none"
                    />
                  </label>
                </div>
              )}
            </div>

            {/* Footer Buttons */}
            <div className="flex items-center justify-between px-4 mt-4 pb-12">
              <button className="flex min-w-[100px] cursor-pointer items-center justify-center rounded-lg h-10 px-6 border border-[#dbe2e6] dark:border-gray-600 bg-transparent hover:bg-gray-100 dark:hover:bg-gray-800 text-[#111618] dark:text-white text-sm font-bold transition-colors">
                <MaterialIcon icon="arrow_back" className="text-lg mr-2" />
                Back
              </button>
              <div className="flex gap-4">
                <button
                  onClick={() => setActiveTab('summary')}
                  className="flex min-w-[120px] cursor-pointer items-center justify-center rounded-lg h-10 px-6 bg-[#13a4ec] hover:bg-[#0f8ecb] text-white text-sm font-bold shadow-lg shadow-blue-500/20 transition-all"
                >
                  Next Step
                  <MaterialIcon icon="arrow_forward" className="text-lg ml-2" />
                </button>
              </div>
            </div>

          </div>
        </div>
      </div>
    </div>
  );
}