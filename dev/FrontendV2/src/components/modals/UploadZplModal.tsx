import { useState, useRef, type DragEvent, type ChangeEvent } from 'react'; // <-- שינוי משמעותי כאן
import MaterialIcon from '../ui/MaterialIcon';

interface UploadZplModalProps {
  onUpload: (file: File) => void;
  onBack: () => void;
}

export default function UploadZplModal({ onUpload, onBack }: UploadZplModalProps) {
  const [dragActive, setDragActive] = useState(false);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const inputRef = useRef<HTMLInputElement>(null);

  // טיפול בגרירה (כניסה ויציאה מהאזור)
  const handleDrag = (e: DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    e.stopPropagation();
    if (e.type === "dragenter" || e.type === "dragover") {
      setDragActive(true);
    } else if (e.type === "dragleave") {
      setDragActive(false);
    }
  };

  // טיפול בשחרור הקובץ
  const handleDrop = (e: DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(false);
    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      handleFile(e.dataTransfer.files[0]);
    }
  };

  // טיפול בבחירה דרך הקליק
  const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
    e.preventDefault();
    if (e.target.files && e.target.files[0]) {
      handleFile(e.target.files[0]);
    }
  };

  // לוגיקה מרכזית לבדיקת הקובץ
  const handleFile = (file: File) => {
    // כאן אפשר להוסיף בדיקות ולידציה (סוג קובץ, גודל וכו')
    setSelectedFile(file);
  };

  const handleSubmit = () => {
    if (selectedFile) {
      onUpload(selectedFile);
    }
  };

  const removeFile = () => {
    setSelectedFile(null);
    if (inputRef.current) {
      inputRef.current.value = "";
    }
  };

  return (
    <div className="fixed inset-0 z-[100] flex items-center justify-center p-4">
      {/* Dimmed Overlay */}
      <div className="absolute inset-0 bg-[#111618]/60 backdrop-blur-sm transition-opacity"></div>

      {/* Modal */}
      <div className="relative z-20 w-full max-w-[560px] bg-white dark:bg-[#15232b] rounded-xl shadow-2xl flex flex-col overflow-hidden animate-in fade-in zoom-in-95 duration-200 border border-gray-200 dark:border-gray-700">
        
        {/* Modal Header */}
        <div className="flex items-center justify-between px-6 pt-6 pb-2">
          <div>
            <h2 className="text-[#111618] dark:text-white text-2xl font-bold leading-tight tracking-tight">Upload ZPL Problem</h2>
            <p className="text-gray-500 dark:text-gray-400 text-sm font-normal leading-normal pt-1">Select a .zpl file to define your new scheduling problem.</p>
          </div>
          <button onClick={onBack} className="text-gray-400 hover:text-gray-600 dark:hover:text-gray-200 transition-colors p-1 rounded-full hover:bg-gray-100 dark:hover:bg-gray-800">
            <MaterialIcon icon="close" />
          </button>
        </div>

        {/* Modal Body */}
        <div className="p-6">
          {!selectedFile ? (
            // State: No File Selected (Drop Zone)
            <div 
              className={`group relative flex flex-col items-center justify-center w-full h-64 border-2 border-dashed rounded-xl transition-all duration-200 cursor-pointer
                ${dragActive 
                  ? 'border-[#13a4ec] bg-blue-50 dark:bg-[#13a4ec]/10' 
                  : 'border-[#dbe2e6] dark:border-gray-600 bg-background-light dark:bg-[#1a2c36] hover:bg-blue-50 dark:hover:bg-[#1e3440] hover:border-[#13a4ec]'
                }`}
              onDragEnter={handleDrag}
              onDragLeave={handleDrag}
              onDragOver={handleDrag}
              onDrop={handleDrop}
            >
              <input 
                ref={inputRef}
                accept=".zpl,.txt" 
                className="absolute inset-0 w-full h-full opacity-0 cursor-pointer z-10" 
                type="file"
                onChange={handleChange}
              />
              <div className="flex flex-col items-center justify-center pt-5 pb-6 text-center space-y-4 pointer-events-none">
                {/* Icon Circle */}
                <div className="w-16 h-16 rounded-full bg-[#13a4ec]/10 dark:bg-[#13a4ec]/20 flex items-center justify-center mb-1 group-hover:scale-110 transition-transform duration-200">
                  <MaterialIcon icon="cloud_upload" className="text-4xl text-[#13a4ec]" />
                </div>
                <div className="space-y-1">
                  <p className="text-[#111618] dark:text-white text-lg font-bold leading-tight">Drag & Drop your file here</p>
                  <p className="text-gray-500 dark:text-gray-400 text-sm">or click to browse</p>
                </div>
                <div className="px-3 py-1 bg-white dark:bg-[#15232b] border border-gray-200 dark:border-gray-700 rounded-md shadow-sm">
                  <p className="text-xs text-gray-500 dark:text-gray-400 font-medium">Supported formats: .ZPL, .TXT (Max 5MB)</p>
                </div>
              </div>
            </div>
          ) : (
            // State: File Selected
            <div className="mt-4 flex items-center justify-between p-4 bg-blue-50 dark:bg-[#13a4ec]/10 border border-blue-100 dark:border-[#13a4ec]/20 rounded-lg animate-in fade-in slide-in-from-bottom-2">
                <div className="flex items-center gap-4">
                    <div className="w-10 h-10 rounded-lg bg-white dark:bg-[#15232b] flex items-center justify-center text-[#13a4ec] shadow-sm">
                        <MaterialIcon icon="description" className="text-2xl" />
                    </div>
                    <div className="flex flex-col">
                        <span className="text-sm font-bold text-[#111618] dark:text-white">{selectedFile.name}</span>
                        <span className="text-xs text-gray-500 dark:text-gray-400">{(selectedFile.size / 1024).toFixed(1)} KB</span>
                    </div>
                </div>
                <button onClick={removeFile} className="text-gray-400 hover:text-red-500 transition-colors p-2 hover:bg-white dark:hover:bg-[#15232b] rounded-lg">
                    <MaterialIcon icon="delete" className="text-xl" />
                </button>
            </div>
          )}
        </div>

        {/* Modal Footer */}
        <div className="px-6 pb-6 pt-2">
          <div className="flex gap-3 justify-end items-center border-t border-gray-100 dark:border-gray-800 pt-5">
            <button 
              onClick={onBack}
              className="flex min-w-[120px] cursor-pointer items-center justify-center overflow-hidden rounded-lg h-10 px-4 bg-transparent border border-gray-300 dark:border-gray-600 text-[#111618] dark:text-gray-200 hover:bg-gray-50 dark:hover:bg-gray-800 text-sm font-semibold leading-normal transition-colors"
            >
              Back to Home
            </button>
            <button 
              onClick={handleSubmit}
              disabled={!selectedFile}
              className={`flex min-w-[120px] cursor-pointer items-center justify-center overflow-hidden rounded-lg h-10 px-4 text-white text-sm font-bold leading-normal tracking-[0.015em] shadow-sm transition-all
                ${selectedFile 
                  ? 'bg-[#13a4ec] hover:bg-sky-500 shadow-[#13a4ec]/20' 
                  : 'bg-gray-300 dark:bg-gray-700 cursor-not-allowed opacity-70'}`}
            >
              <span className="mr-2 flex items-center"><MaterialIcon icon="upload_file" className="text-[18px]" /></span>
              <span>Upload</span>
            </button>
          </div>
        </div>

      </div>
    </div>
  );
}