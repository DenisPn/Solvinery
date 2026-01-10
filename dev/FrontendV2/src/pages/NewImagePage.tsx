import { useState, useMemo, useEffect } from 'react';

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
import type { ModelPayload } from '../types/apiTypes';

export default function NewImagePage() {
  const { userId } = useAuth();

  // --- UI State ---
  const [showUploadModal, setShowUploadModal] = useState(true);
  const [activeTab, setActiveTab] = useState('sets');
  
  // --- Data State ---
  const [modelData, setModelData] = useState<ParseModelResponse | null>(null);
  const [zplCode, setZplCode] = useState<string>(""); // State לשמירת קוד ה-ZPL המקורי
  
  // Local State for Tabs (Editable Data)
  const [variables, setVariables] = useState<VariableData[]>([]);
  const [preferences, setPreferences] = useState<any[]>([]); 
  const [constraints, setConstraints] = useState<any[]>([]); 

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
          // שמירת הקוד המקורי ב-State לשימוש מאוחר יותר
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
    if (modelData) {
        // 1. אתחול משתנים
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

        // 2. אתחול רשימות למודולים
        if (modelData.preferences) {
            setPreferences([]); 
        }
        if (modelData.constraints) {
            setConstraints([]);
        }
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
      name: "New Optimization Model", 
      description: "Generated from ZPL Upload", 
      
      // 👇 כאן אנחנו שולחים את תוכן הקובץ המקורי שנשמר ב-State
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
          moduleName: c.name || c.title || "Unnamed Constraint Module",
          description: c.description || c.desc || "",
          constraints: c.constraints,
          active: true 
        })),

      preferenceModules: preferences
        .filter((p: any) => p.preferences && p.preferences.length > 0)
        .map((p: any) => ({
          moduleName: p.name || p.title || "Unnamed Preference Module",
          description: p.description || p.desc || "",
          preferences: p.preferences,
          scalar: 1 
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

      {/* Header */}
      <header className="flex items-center justify-between whitespace-nowrap border-b border-solid border-b-[#f0f3f4] dark:border-b-[#2a3840] bg-white dark:bg-[#101c22] px-10 py-3 sticky top-0 z-50">
        <div className="flex items-center gap-4 text-[#111618] dark:text-white">
          <div className="size-6 text-[#13a4ec]">
            <MaterialIcon icon="calendar_month" className="text-2xl" />
          </div>
          <h2 className="text-[#111618] dark:text-white text-lg font-bold leading-tight tracking-[-0.015em]">Scheduling Pro</h2>
        </div>
        <div className="flex flex-1 justify-end gap-8">
          <div className="hidden md:flex items-center gap-9">
            <a className="text-[#111618] dark:text-gray-200 text-sm font-medium hover:text-[#13a4ec] transition-colors" href="#">Dashboard</a>
            <a className="text-[#111618] dark:text-gray-200 text-sm font-medium hover:text-[#13a4ec] transition-colors" href="#">Problems</a>
            <a className="text-[#111618] dark:text-gray-200 text-sm font-medium hover:text-[#13a4ec] transition-colors" href="#">Schedules</a>
            <a className="text-[#111618] dark:text-gray-200 text-sm font-medium hover:text-[#13a4ec] transition-colors" href="#">Settings</a>
          </div>
          <div className="bg-center bg-no-repeat aspect-square bg-cover rounded-full size-10 border border-gray-200 dark:border-gray-700 bg-gray-100" style={{ backgroundImage: 'url("https://lh3.googleusercontent.com/a/default-user=s96-c")' }}></div>
        </div>
      </header>

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
                <span className="text-[#111618] dark:text-white text-sm font-medium">Create New</span>
              </div>
              
              <div className="flex flex-wrap justify-between gap-3 mt-2">
                <div className="flex min-w-72 flex-col gap-2">
                  <h1 className="text-[#111618] dark:text-white text-3xl font-black leading-tight tracking-[-0.033em]">Create New Scheduling Problem</h1>
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
                    <p className={`text-sm leading-normal tracking-[0.015em] ${activeTab === tab.id ? 'font-bold' : 'font-bold'}`}>{tab.label}</p>
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
                <div className="flex flex-col items-center justify-center min-h-[400px] bg-white dark:bg-[#1A2C38] rounded-xl border border-[#dbe2e6] dark:border-gray-700 p-8 text-center">
                  <div className="w-16 h-16 bg-gray-100 dark:bg-gray-800 rounded-full flex items-center justify-center mb-4 text-gray-400">
                    <MaterialIcon icon="construction" className="text-3xl" />
                  </div>
                  <h3 className="text-lg font-bold text-[#111618] dark:text-white mb-2">General Information</h3>
                  <p className="text-[#617c89] dark:text-gray-400">Configure basic model details here.</p>
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