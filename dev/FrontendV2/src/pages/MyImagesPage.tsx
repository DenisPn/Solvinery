import { Link } from 'react-router-dom';

// הגדרת סוג הנתונים (כדי ש-TypeScript יהיה שמח)
interface Problem {
  id: number;
  title: string;
  status: 'Solved' | 'Conflicts Found' | 'Processing' | 'Draft';
  description: string;
  date: string;
  icon: string;
  colorTheme: 'blue' | 'orange' | 'purple' | 'gray' | 'teal' | 'green';
}

// נתונים דמה (במציאות זה יגיע מהשרת)
const problems: Problem[] = [
  {
    id: 1,
    title: "Q3 Logistics Optimization",
    status: "Solved",
    description: "Optimizing delivery routes and staff allocation for the third quarter peak season. Includes 50 drivers and 200 delivery points.",
    date: "Oct 24, 2023",
    icon: "event_note",
    colorTheme: "blue"
  },
  {
    id: 2,
    title: "University Course Timetable",
    status: "Conflicts Found",
    description: "Scheduling for the Science department, Fall 2024 semester. Constraints include professor availability and lab room capacity.",
    date: "Oct 22, 2023",
    icon: "warning",
    colorTheme: "orange"
  },
  {
    id: 3,
    title: "Nurse Shift Rotation - Nov",
    status: "Processing",
    description: "Monthly shift planning for the ICU ward. Balancing night shifts and weekend leaves for 30 staff members.",
    date: "Oct 20, 2023",
    icon: "schedule",
    colorTheme: "purple"
  },
  {
    id: 4,
    title: "Factory Line A Maintenance",
    status: "Draft",
    description: "Scheduling preventive maintenance downtime without disrupting the main production quotas for Week 45.",
    date: "Oct 18, 2023",
    icon: "factory",
    colorTheme: "gray"
  },
  {
    id: 5,
    title: "Regional Soccer Tournament",
    status: "Solved",
    description: "Match scheduling for 16 teams across 4 venues over a single weekend. Constraints: minimal travel time between venues.",
    date: "Oct 15, 2023",
    icon: "sports_soccer",
    colorTheme: "teal"
  }
];

// פונקציות עזר לצבעים
const getStatusStyles = (status: string) => {
  switch (status) {
    case 'Solved': return 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400';
    case 'Conflicts Found': return 'bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-400';
    case 'Processing': return 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400';
    default: return 'bg-gray-200 text-gray-700 dark:bg-gray-700 dark:text-gray-300';
  }
};

const getIconStyles = (theme: string) => {
  const styles:Record<string, string> = {
    blue: 'bg-blue-100 dark:bg-blue-900/30 text-blue-600 dark:text-blue-400',
    orange: 'bg-orange-100 dark:bg-orange-900/30 text-orange-600 dark:text-orange-400',
    purple: 'bg-purple-100 dark:bg-purple-900/30 text-purple-600 dark:text-purple-400',
    gray: 'bg-gray-100 dark:bg-gray-800 text-gray-600 dark:text-gray-400',
    teal: 'bg-teal-100 dark:bg-teal-900/30 text-teal-600 dark:text-teal-400',
  };
  return styles[theme] || styles.blue;
};

export default function MyImagesPage() {
  return (
    <div className="relative flex h-auto min-h-screen w-full flex-col overflow-x-hidden bg-background-light dark:bg-background-dark text-text-main-light dark:text-white font-display">
      
      {/* Header - בתוך העמוד כרגע */}
      <header className="sticky top-0 z-50 flex items-center justify-between whitespace-nowrap border-b border-solid border-border-light dark:border-border-dark bg-surface-light dark:bg-surface-dark px-10 py-3 shadow-sm">
        <div className="flex items-center gap-8">
          <div className="flex items-center gap-3">
            <div className="size-8 text-primary flex items-center justify-center rounded-lg bg-primary/10">
              <span className="material-symbols-outlined text-3xl">calendar_month</span>
            </div>
            <h2 className="text-text-main-light dark:text-white text-lg font-bold leading-tight tracking-[-0.015em]">SchedulerPro</h2>
          </div>
          
          <label className="hidden md:flex flex-col min-w-40 !h-10 max-w-64">
            <div className="flex w-full flex-1 items-stretch rounded-lg h-full bg-[#f0f3f4] dark:bg-[#2c3b47] overflow-hidden">
              <div className="text-text-secondary-light dark:text-gray-400 flex items-center justify-center px-4">
                <span className="material-symbols-outlined text-[20px]">search</span>
              </div>
              <input 
                className="flex w-full min-w-0 flex-1 resize-none overflow-hidden border-none bg-transparent focus:outline-0 focus:ring-0 h-full placeholder:text-text-secondary-light dark:placeholder:text-gray-500 px-0 text-base font-normal leading-normal text-text-main-light dark:text-white" 
                placeholder="Search problems..." 
              />
            </div>
          </label>
        </div>

        <div className="flex items-center gap-8">
          <nav className="hidden lg:flex items-center gap-6">
            <a className="text-text-main-light dark:text-gray-200 hover:text-primary dark:hover:text-primary transition-colors text-sm font-medium leading-normal" href="#">Dashboard</a>
            <a className="text-primary text-sm font-bold leading-normal" href="#">My Problems</a>
            <a className="text-text-main-light dark:text-gray-200 hover:text-primary dark:hover:text-primary transition-colors text-sm font-medium leading-normal" href="#">Solvers</a>
            <a className="text-text-main-light dark:text-gray-200 hover:text-primary dark:hover:text-primary transition-colors text-sm font-medium leading-normal" href="#">Settings</a>
          </nav>
          <div className="flex items-center gap-4">
            <button className="flex items-center justify-center size-10 rounded-full hover:bg-gray-100 dark:hover:bg-gray-700 text-text-main-light dark:text-white transition-colors">
              <span className="material-symbols-outlined">notifications</span>
            </button>
            <div 
              className="bg-center bg-no-repeat aspect-square bg-cover rounded-full size-10 ring-2 ring-transparent hover:ring-primary cursor-pointer transition-all" 
              style={{ backgroundImage: 'url("https://i.pravatar.cc/150?img=12")' }}
            ></div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="flex-1 flex justify-center py-8 px-4 sm:px-8 lg:px-20">
        <div className="w-full max-w-[1200px] flex flex-col gap-6">
          
          {/* Top Section */}
          <div className="flex flex-col md:flex-row flex-wrap justify-between items-start md:items-center gap-4">
            <div>
              <h1 className="text-text-main-light dark:text-white text-3xl md:text-4xl font-black leading-tight tracking-[-0.033em]">My Scheduling Problems</h1>
              <p className="text-gray-500 dark:text-gray-400 mt-1 text-sm">Manage and solve your scheduling challenges in one place</p>
            </div>
            
            {/* כפתור יצירה חדשה - מקושר לראוטר */}
            <Link to="/new">
              <button className="flex items-center gap-2 bg-primary hover:bg-blue-700 text-white rounded-lg h-10 px-5 text-sm font-bold leading-normal tracking-[0.015em] transition-all shadow-md hover:shadow-lg">
                <span className="material-symbols-outlined text-[20px]">add_circle</span>
                <span className="truncate">Create New Problem</span>
              </button>
            </Link>
          </div>

          {/* Filters */}
          <div className="flex flex-wrap items-center gap-3">
            <button className="flex h-9 items-center justify-center gap-x-2 rounded-full bg-primary text-white px-5 transition-colors shadow-sm">
              <span className="text-sm font-medium leading-normal">All</span>
            </button>
            <button className="flex h-9 items-center justify-center gap-x-2 rounded-full bg-white dark:bg-surface-dark border border-gray-200 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-700 text-text-main-light dark:text-gray-200 px-5 transition-colors">
              <span className="text-sm font-medium leading-normal">Solved</span>
            </button>
            <button className="flex h-9 items-center justify-center gap-x-2 rounded-full bg-white dark:bg-surface-dark border border-gray-200 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-700 text-text-main-light dark:text-gray-200 px-5 transition-colors">
              <span className="text-sm font-medium leading-normal">In Progress</span>
            </button>
            <div className="mr-auto ml-0 md:ml-auto md:mr-0 w-full md:w-auto mt-2 md:mt-0">
              <button className="flex items-center gap-2 text-sm font-medium text-gray-600 dark:text-gray-400 hover:text-primary">
                <span className="material-symbols-outlined text-[20px]">sort</span>
                Sort by Date
              </button>
            </div>
          </div>

          {/* Problems List */}
          <div className="flex flex-col gap-4">
            {problems.map((problem) => (
              <div key={problem.id} className="group flex flex-col md:flex-row bg-surface-light dark:bg-surface-dark rounded-xl overflow-hidden shadow-sm hover:shadow-md transition-all duration-300 border border-transparent hover:border-primary/20 p-5 gap-4 md:gap-6 items-start md:items-center">
                <div className="flex-shrink-0">
                  <div className={`size-12 md:size-14 rounded-full flex items-center justify-center ${getIconStyles(problem.colorTheme)}`}>
                    <span className="material-symbols-outlined text-2xl md:text-3xl">{problem.icon}</span>
                  </div>
                </div>
                
                <div className="flex-1 flex flex-col gap-1">
                  <div className="flex items-center gap-2">
                    <h3 className="text-text-main-light dark:text-white text-lg font-bold leading-tight">{problem.title}</h3>
                    <span className={`px-2 py-0.5 rounded-full text-xs font-semibold ${getStatusStyles(problem.status)}`}>
                      {problem.status}
                    </span>
                  </div>
                  <p className="text-gray-500 dark:text-gray-400 text-sm font-normal line-clamp-2">{problem.description}</p>
                  <p className="text-gray-400 dark:text-gray-500 text-xs mt-1">Last edited: {problem.date}</p>
                </div>

                <div className="flex items-center gap-3 w-full md:w-auto mt-2 md:mt-0 border-t md:border-t-0 pt-4 md:pt-0 border-gray-100 dark:border-gray-700">
                  {/* כפתור View Details */}
                  <Link to={`/image/${problem.id}`} className="flex-1 md:flex-none">
                    <button className="w-full flex items-center justify-center gap-2 h-10 px-4 rounded-lg bg-gray-50 dark:bg-gray-700 hover:bg-gray-100 dark:hover:bg-gray-600 text-text-main-light dark:text-white text-sm font-medium transition-colors border border-gray-200 dark:border-gray-600">
                      <span className="material-symbols-outlined text-[18px]">visibility</span>
                      <span className="whitespace-nowrap">View Details</span>
                    </button>
                  </Link>
                  
                  {/* כפתור Edit */}
                  <Link to={`/image/${problem.id}/edit`} className="flex-1 md:flex-none">
                    <button className="w-full flex items-center justify-center gap-2 h-10 px-4 rounded-lg border border-primary text-primary hover:bg-primary hover:text-white text-sm font-medium transition-colors">
                      <span className="material-symbols-outlined text-[18px]">edit</span>
                      <span className="whitespace-nowrap">Edit Problem</span>
                    </button>
                  </Link>
                </div>
              </div>
            ))}
          </div>

          {/* Pagination */}
          <div className="flex items-center justify-center py-6 mt-4">
            <div className="flex items-center gap-1">
              <a className="flex size-9 items-center justify-center text-text-main-light dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors" href="#">
                <span className="material-symbols-outlined text-[20px]">chevron_left</span>
              </a>
              <a className="text-sm font-bold leading-normal flex size-9 items-center justify-center text-white rounded-lg bg-primary shadow-md" href="#">1</a>
              <a className="text-sm font-normal leading-normal flex size-9 items-center justify-center text-text-main-light dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors" href="#">2</a>
              <a className="text-sm font-normal leading-normal flex size-9 items-center justify-center text-text-main-light dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors" href="#">3</a>
              <span className="flex items-center justify-center w-8 text-gray-400">...</span>
              <a className="text-sm font-normal leading-normal flex size-9 items-center justify-center text-text-main-light dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors" href="#">5</a>
              <a className="flex size-9 items-center justify-center text-text-main-light dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors" href="#">
                <span className="material-symbols-outlined text-[20px]">chevron_right</span>
              </a>
            </div>
          </div>

        </div>
      </main>
    </div>
  );
}