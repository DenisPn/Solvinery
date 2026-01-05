import React, { useState } from 'react';

// ייבוא הקומפוננטות הנפרדות
import MaterialIcon from '../components/ui/MaterialIcon';
import VariablesTab from '../components/tabs/VariablesTab';
import PreferencesTab from '../components/tabs/PreferencesTab';
import ConstraintsTab from '../components/tabs/ConstraintsTab';

export default function NewImagePage() {
  // ניהול הטאב הפעיל.
  const [activeTab, setActiveTab] = useState('constraints');

  // רשימת הטאבים
  const tabs = [
    { id: 'general', label: 'General Information' },
    { id: 'resources', label: 'Resources' },
    { id: 'constraints', label: 'Constraint Modules' },
    { id: 'preferences', label: 'Preference Modules' },
    { id: 'variables', label: 'Variables' },
    { id: 'objectives', label: 'Objectives' },
    { id: 'summary', label: 'Summary' },
  ];

  // פונקציה לקבלת התיאור המתאים לכל טאב
  const getPageDescription = () => {
    switch (activeTab) {
      case 'variables':
        return "Define the decision variables for your optimization model.";
      case 'preferences':
        return "Define preference modules to optimize schedule quality and satisfaction.";
      case 'constraints':
        return "Define the rules and limitations for your scheduling model.";
      default:
        return "Configure the settings for your new scheduling problem.";
    }
  };

  return (
    <div className="font-display bg-[#f6f7f8] dark:bg-[#101c22] text-[#111618] dark:text-white min-h-screen flex flex-col transition-colors duration-200">
      
      {/* --- HEADER --- */}
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

      {/* --- MAIN LAYOUT --- */}
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
                {/* Save Draft Button (Global) */}
                <div className="flex items-center gap-3">
                   <button className="flex items-center gap-2 rounded-lg border border-[#dbe2e6] dark:border-gray-600 px-4 py-2 text-sm font-medium text-[#111618] dark:text-gray-200 bg-white dark:bg-[#182830] hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors">
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

            {/* --- DYNAMIC CONTENT AREA --- */}
            <div className="px-4">
              {/* כאן מתבצעת ההחלפה הנקייה בין הקומפוננטות */}
              {activeTab === 'variables' && <VariablesTab />}
              {activeTab === 'preferences' && <PreferencesTab />}
              {activeTab === 'constraints' && <ConstraintsTab />}
              
              {/* Placeholders for other tabs */}
              {['general', 'resources', 'objectives', 'summary'].includes(activeTab) && (
                <div className="flex flex-col items-center justify-center min-h-[400px] bg-white dark:bg-[#1A2C38] rounded-xl border border-[#dbe2e6] dark:border-gray-700 p-8 text-center">
                  <div className="w-16 h-16 bg-gray-100 dark:bg-gray-800 rounded-full flex items-center justify-center mb-4 text-gray-400">
                    <MaterialIcon icon="construction" className="text-3xl" />
                  </div>
                  <h3 className="text-lg font-bold text-[#111618] dark:text-white mb-2">Work in Progress</h3>
                  <p className="text-[#617c89] dark:text-gray-400">The <strong>{tabs.find(t=>t.id===activeTab)?.label}</strong> module is currently under development.</p>
                </div>
              )}
            </div>

            {/* Footer Navigation */}
            <div className="flex items-center justify-between px-4 mt-4 pb-12">
                <button className="flex min-w-[100px] cursor-pointer items-center justify-center rounded-lg h-10 px-6 border border-[#dbe2e6] dark:border-gray-600 bg-transparent hover:bg-gray-100 dark:hover:bg-gray-800 text-[#111618] dark:text-white text-sm font-bold transition-colors">
                    <MaterialIcon icon="arrow_back" className="text-lg mr-2" />
                    Back
                </button>
                <div className="flex gap-4">
                  <button className="flex min-w-[120px] cursor-pointer items-center justify-center rounded-lg h-10 px-6 bg-[#13a4ec] hover:bg-[#0f8ecb] text-white text-sm font-bold shadow-lg shadow-blue-500/20 transition-all">
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