import { useParams, Link } from 'react-router-dom';

// --- טיפוסים (Types) ---

interface KpiStat {
  label: string;
  value: string;
  icon: string;
  extra?: string; // לדוגמה "(Soft)"
  valueColor?: string; // צבע מיוחד לערך
}

interface Shift {
  type: 'Morning' | 'Afternoon' | 'Night' | 'Off';
  time?: string;
  label?: string;
}

interface EmployeeSchedule {
  id: string;
  name: string;
  role: string;
  initials: string;
  color: string; // צבע הרקע של העיגול עם הראשי תיבות
  schedule: Shift[]; // מערך של 7 ימים
}

// --- נתונים דמה (Mock Data) ---

const kpiStats: KpiStat[] = [
  { label: "Total Cost", value: "$12,450", icon: "attach_money", valueColor: "text-text-main-light dark:text-white" },
  { label: "Unassigned Shifts", value: "0", icon: "person_off", valueColor: "text-green-600 dark:text-green-400" },
  { label: "Constraints Violated", value: "45", icon: "warning", extra: "(Soft)", valueColor: "text-amber-500" },
  { label: "Satisfaction", value: "98%", icon: "thumb_up", valueColor: "text-text-main-light dark:text-white" },
];

const employees: EmployeeSchedule[] = [
  {
    id: '1', name: 'John Doe', role: 'Senior Nurse', initials: 'JD', color: 'bg-blue-100 dark:bg-blue-900/30 text-primary',
    schedule: [
      { type: 'Morning', time: '07:00 - 15:00', label: 'Morning Shift' },
      { type: 'Morning', time: '07:00 - 15:00', label: 'Morning Shift' },
      { type: 'Off' },
      { type: 'Afternoon', time: '15:00 - 23:00', label: 'Afternoon' },
      { type: 'Afternoon', time: '15:00 - 23:00', label: 'Afternoon' },
      { type: 'Off' },
      { type: 'Off' },
    ]
  },
  {
    id: '2', name: 'Alice Smith', role: 'ICU Nurse', initials: 'AS', color: 'bg-purple-100 dark:bg-purple-900/30 text-purple-600 dark:text-purple-300',
    schedule: [
      { type: 'Off' },
      { type: 'Off' },
      { type: 'Night', time: '23:00 - 07:00', label: 'Night Shift' },
      { type: 'Night', time: '23:00 - 07:00', label: 'Night Shift' },
      { type: 'Night', time: '23:00 - 07:00', label: 'Night Shift' },
      { type: 'Night', time: '23:00 - 07:00', label: 'Night Shift' },
      { type: 'Off' },
    ]
  },
  {
    id: '3', name: 'Mike K.', role: 'Assistant', initials: 'MK', color: 'bg-orange-100 dark:bg-orange-900/30 text-orange-600 dark:text-orange-300',
    schedule: [
      { type: 'Afternoon', time: '15:00 - 23:00', label: 'Afternoon' },
      { type: 'Afternoon', time: '15:00 - 23:00', label: 'Afternoon' },
      { type: 'Morning', time: '07:00 - 15:00', label: 'Morning Shift' },
      { type: 'Morning', time: '07:00 - 15:00', label: 'Morning Shift' },
      { type: 'Off' },
      { type: 'Off' },
      { type: 'Morning', time: '07:00 - 15:00', label: 'Morning Shift' },
    ]
  },
  {
    id: '4', name: 'Sarah Lee', role: 'Junior Nurse', initials: 'SL', color: 'bg-teal-100 dark:bg-teal-900/30 text-teal-600 dark:text-teal-300',
    schedule: [
      { type: 'Off' },
      { type: 'Morning', time: '07:00 - 15:00', label: 'Morning Shift' },
      { type: 'Morning', time: '07:00 - 15:00', label: 'Morning Shift' },
      { type: 'Morning', time: '07:00 - 15:00', label: 'Morning Shift' },
      { type: 'Off' },
      { type: 'Afternoon', time: '15:00 - 23:00', label: 'Afternoon' },
      { type: 'Afternoon', time: '15:00 - 23:00', label: 'Afternoon' },
    ]
  },
  {
    id: '5', name: 'David R.', role: 'Trainee', initials: 'DR', color: 'bg-pink-100 dark:bg-pink-900/30 text-pink-600 dark:text-pink-300',
    schedule: [
      { type: 'Off' },
      { type: 'Off' },
      { type: 'Off' },
      { type: 'Off' },
      { type: 'Night', time: '23:00 - 07:00', label: 'Night Shift' },
      { type: 'Night', time: '23:00 - 07:00', label: 'Night Shift' },
      { type: 'Night', time: '23:00 - 07:00', label: 'Night Shift' },
    ]
  },
];

// --- פונקציית עזר לעיצוב משמרות ---
const getShiftStyle = (type: Shift['type']) => {
  switch (type) {
    case 'Morning':
      return 'bg-primary/10 dark:bg-primary/20 text-primary-dark dark:text-primary border-l-4 border-primary';
    case 'Afternoon':
      return 'bg-amber-50 dark:bg-amber-900/20 text-amber-700 dark:text-amber-400 border-l-4 border-amber-500';
    case 'Night':
      return 'bg-purple-50 dark:bg-purple-900/20 text-purple-700 dark:text-purple-300 border-l-4 border-purple-500';
    case 'Off':
      return 'bg-slate-100 dark:bg-slate-800 text-slate-400 italic text-center';
    default:
      return '';
  }
};

export default function SolutionViewPage() {
  const { id } = useParams();
  const days = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
  const dates = ['Oct 23', 'Oct 24', 'Oct 25', 'Oct 26', 'Oct 27', 'Oct 28', 'Oct 29'];

  return (
    <div className="flex-1 bg-background-light dark:bg-background-dark p-4 md:p-8 lg:px-12 xl:px-20 min-h-screen">
      <div className="mx-auto max-w-7xl flex flex-col gap-6">
        
        {/* Breadcrumbs */}
        <div className="flex flex-wrap gap-2 items-center text-sm mb-2">
          <Link to="/" className="text-gray-500 hover:text-primary transition-colors">Home</Link>
          <span className="material-symbols-outlined text-gray-500 text-base">chevron_right</span>
          <Link to={`/image/${id || '1'}`} className="text-gray-500 hover:text-primary transition-colors">Problem View</Link>
          <span className="material-symbols-outlined text-gray-500 text-base">chevron_right</span>
          <span className="font-medium text-primary">Solution #{id || '1'}</span>
        </div>

        {/* Page Header & Actions */}
        <div className="flex flex-col lg:flex-row justify-between items-start lg:items-center gap-4 border-b border-slate-200 dark:border-slate-800 pb-6">
          <div className="flex flex-col gap-2">
            <div className="flex items-center gap-3 flex-wrap">
              <h1 className="text-slate-900 dark:text-white text-3xl md:text-4xl font-black leading-tight tracking-[-0.033em]">
                Weekly Nurse Roster - Oct 24
              </h1>
              <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-300">
                Optimal
              </span>
            </div>
            <p className="text-slate-500 dark:text-slate-400 text-sm md:text-base font-normal leading-normal flex items-center gap-2">
              <span className="material-symbols-outlined text-sm">calendar_today</span> Generated: Oct 24, 2023 14:00
              <span className="mx-2 text-slate-300 dark:text-slate-600">|</span>
              <span className="material-symbols-outlined text-sm">analytics</span> Score: 98.5
            </p>
          </div>
          
          <div className="flex gap-3 flex-wrap">
            <button className="group flex items-center justify-center gap-2 rounded-lg h-10 px-4 bg-white dark:bg-surface-dark border border-slate-200 dark:border-slate-700 hover:bg-slate-50 dark:hover:bg-slate-800 transition-colors text-slate-700 dark:text-slate-200 text-sm font-bold shadow-sm">
              <span className="material-symbols-outlined text-[18px]">download</span>
              <span>Export CSV</span>
            </button>
            <button className="group flex items-center justify-center gap-2 rounded-lg h-10 px-4 bg-white dark:bg-surface-dark border border-slate-200 dark:border-slate-700 hover:bg-slate-50 dark:hover:bg-slate-800 transition-colors text-slate-700 dark:text-slate-200 text-sm font-bold shadow-sm">
              <span className="material-symbols-outlined text-[18px]">print</span>
              <span>Print</span>
            </button>
            <button className="group flex items-center justify-center gap-2 rounded-lg h-10 px-4 bg-primary hover:bg-primary-dark text-white text-sm font-bold shadow-md transition-colors">
              <span className="material-symbols-outlined text-[18px] group-hover:animate-spin">refresh</span>
              <span>Recalculate</span>
            </button>
          </div>
        </div>

        {/* KPI Stats */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          {kpiStats.map((stat, idx) => (
            <div key={idx} className="flex flex-col gap-1 rounded-xl p-5 bg-white dark:bg-surface-dark border border-slate-200 dark:border-slate-700 shadow-sm">
              <div className="flex items-center gap-2 text-slate-500 dark:text-slate-400 mb-1">
                <span className="material-symbols-outlined text-[20px]">{stat.icon}</span>
                <p className="text-sm font-medium uppercase tracking-wide">{stat.label}</p>
              </div>
              <p className={`text-2xl font-bold leading-tight ${stat.valueColor}`}>
                {stat.value} <span className="text-xs text-slate-400 font-normal align-middle ml-1">{stat.extra}</span>
              </p>
            </div>
          ))}
        </div>

        {/* Filters & Controls Bar */}
        <div className="flex flex-col sm:flex-row justify-between items-center gap-4 bg-white dark:bg-surface-dark p-2 rounded-xl border border-slate-200 dark:border-slate-700 shadow-sm">
          <div className="bg-slate-100 dark:bg-slate-800 p-1 rounded-lg flex w-full sm:w-auto">
            <button className="flex-1 sm:flex-none px-4 py-1.5 rounded-md bg-white dark:bg-surface-dark text-slate-900 dark:text-white text-sm font-medium shadow-sm transition-all border border-slate-200 dark:border-slate-700">By Employee</button>
            <button className="flex-1 sm:flex-none px-4 py-1.5 rounded-md text-slate-500 dark:text-slate-400 hover:text-slate-700 dark:hover:text-slate-200 text-sm font-medium transition-all">By Shift</button>
          </div>
          <div className="flex items-center gap-3 w-full sm:w-auto justify-end">
            <div className="relative group w-full sm:w-auto">
              <span className="material-symbols-outlined absolute left-2.5 top-1/2 -translate-y-1/2 text-slate-400 text-[18px]">search</span>
              <input className="h-9 pl-9 pr-4 rounded-lg bg-slate-50 dark:bg-slate-800 border-none text-sm text-slate-900 dark:text-white placeholder-slate-400 focus:ring-2 focus:ring-primary w-full sm:w-48 transition-all" placeholder="Search employee..." type="text"/>
            </div>
            <button className="h-9 w-9 flex items-center justify-center rounded-lg bg-slate-50 dark:bg-slate-800 text-slate-500 hover:text-primary transition-colors">
              <span className="material-symbols-outlined text-[20px]">filter_list</span>
            </button>
            <button className="h-9 w-9 flex items-center justify-center rounded-lg bg-slate-50 dark:bg-slate-800 text-slate-500 hover:text-primary transition-colors">
              <span className="material-symbols-outlined text-[20px]">settings</span>
            </button>
          </div>
        </div>

        {/* Main Schedule Visualization */}
        <div className="bg-white dark:bg-surface-dark rounded-xl border border-slate-200 dark:border-slate-700 shadow-sm flex flex-col overflow-hidden h-full min-h-[500px]">
          <div className="overflow-x-auto flex-1 custom-scrollbar">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="bg-slate-50 dark:bg-slate-800 border-b border-slate-200 dark:border-slate-700">
                  <th className="sticky left-0 z-10 bg-slate-50 dark:bg-slate-800 py-4 px-4 min-w-[200px] border-r border-slate-200 dark:border-slate-700">
                    <span className="text-xs font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wider">Employee</span>
                  </th>
                  {days.map((day, idx) => (
                    <th key={day} className={`py-4 px-2 min-w-[140px] text-center border-r border-slate-100 dark:border-slate-700/50 ${idx >= 5 ? 'bg-slate-50/50 dark:bg-slate-800/50' : ''}`}>
                      <div className="flex flex-col">
                        <span className="text-xs font-semibold text-slate-500 dark:text-slate-400 uppercase">{day}</span>
                        <span className="text-sm font-bold text-slate-900 dark:text-white">{dates[idx]}</span>
                      </div>
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 dark:divide-slate-700/50">
                {employees.map((emp) => (
                  <tr key={emp.id} className="group hover:bg-slate-50 dark:hover:bg-slate-800/30 transition-colors">
                    {/* Employee Sticky Column */}
                    <td className="sticky left-0 bg-white dark:bg-surface-dark group-hover:bg-slate-50 dark:group-hover:bg-slate-800/30 py-4 px-4 border-r border-slate-100 dark:border-slate-700">
                      <div className="flex items-center gap-3">
                        <div className={`size-8 rounded-full flex items-center justify-center text-xs font-bold ${emp.color}`}>
                          {emp.initials}
                        </div>
                        <div>
                          <p className="text-sm font-bold text-slate-900 dark:text-white">{emp.name}</p>
                          <p className="text-xs text-slate-500">{emp.role}</p>
                        </div>
                      </div>
                    </td>

                    {/* Schedule Columns */}
                    {emp.schedule.map((shift, idx) => (
                      <td key={idx} className={`p-2 border-r border-slate-50 dark:border-slate-800 ${idx >= 5 ? 'bg-slate-50/30 dark:bg-slate-800/30' : ''}`}>
                        <div className={`rounded p-2 text-xs ${getShiftStyle(shift.type)}`}>
                          {shift.type !== 'Off' ? (
                            <>
                              <div className="font-bold">{shift.time}</div>
                              <div className="opacity-80">{shift.label}</div>
                            </>
                          ) : (
                            'Off'
                          )}
                        </div>
                      </td>
                    ))}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

      </div>
    </div>
  );
}